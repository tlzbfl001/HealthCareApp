package kr.bodywell.health.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.health.R
import kr.bodywell.health.model.Bluetooth
import kr.bodywell.health.util.CustomUtil.TAG

class BluetoothItemAdapter (
   private val listener: Listener,
   private val adapterType: Boolean,
   private val list: MutableList<Bluetooth>
) : RecyclerView.Adapter<BluetoothItemAdapter.ViewHolder>() {
   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bt_list, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
      holder.itemView.setOnClickListener {
         if(adapterType) {
            try {
               list[pos].device.createBond()
            }catch(e: SecurityException) {
               Log.e(TAG, "createBond: $e")
            }
         }else {
            list[pos].let { it1 -> listener.onClick(it1) }
         }
      }

      try{
         holder.tvName.text = list[pos].device.name
      }catch(e: SecurityException) {
         Log.e(TAG, "Bluetooth: $e")
      }
   }

   override fun getItemViewType(position: Int): Int {
      return position
   }

   override fun getItemCount(): Int {
      return list.count()
   }

   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val tvName: TextView = itemView.findViewById(R.id.tvName)
   }

   interface Listener {
      fun onClick(device: Bluetooth)
   }
}