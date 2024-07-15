package kr.bodywell.health.adapter

import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.bodywell.health.R
import kr.bodywell.health.model.Bluetooth
import kr.bodywell.health.util.BluetoothService
import kr.bodywell.health.util.BluetoothService.Companion.btStatus
import kr.bodywell.health.util.BluetoothService.Companion.deviceNum

class BtListAdapter (
   private val itemList: ArrayList<Bluetooth>
) : RecyclerView.Adapter<BtListAdapter.ViewHolder>() {
   var selectPos = -1

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bluetooth, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.tvName.text = itemList[position].name
      holder.tvStatus.text = itemList[position].status

      if(selectPos == position) {
         // BluetoothService 실행중이면 중지
         val manager = holder.itemView.context.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
         for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if ("com.bodywell.app.util.BluetoothService" == service.service.className) {
               val intent = Intent(holder.itemView.context, BluetoothService::class.java)
               holder.itemView.context.stopService(intent)
            }
         }

         // BluetoothService 시작
         val intent = Intent(holder.itemView.context, BluetoothService::class.java)
         intent.putExtra("deviceNum", position.toString())
         holder.itemView.context.startService(intent)

         holder.tvStatus.visibility = View.VISIBLE

         CoroutineScope(Dispatchers.IO).launch {
            while(true) {
               if(btStatus == "연결 완료") {
                  holder.tvName.setTextColor(Color.parseColor("#035DAC"))
               }else {
                  holder.tvName.setTextColor(Color.parseColor("#454545"))
               }
               holder.tvStatus.text = btStatus
               delay(1000)
            }
         }
      }else {
         holder.tvStatus.visibility = View.GONE
      }

      holder.tvName.setOnClickListener {
         var beforePos = selectPos
         selectPos = position

         notifyItemChanged(beforePos)
         notifyItemChanged(selectPos)
      }

      if(position.toString() == deviceNum && btStatus == "연결 완료") {
         holder.tvStatus.visibility = View.VISIBLE
         CoroutineScope(Dispatchers.IO).launch {
            while(true) {
               holder.tvName.setTextColor(Color.parseColor("#035DAC"))
               holder.tvStatus.text = btStatus
               delay(1000)
            }
         }
      }
   }

   override fun getItemCount(): Int {
      return itemList.count()
   }

   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val tvName: TextView = itemView.findViewById(R.id.tvName)
      val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
   }
}