package kr.bodywell.android.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.android.R
import kr.bodywell.android.databinding.ItemBtListBinding
import kr.bodywell.android.model.Bluetooth

class BTItemAdapter(
   private val listener: Listener,
   private val adapterType: Boolean
) : ListAdapter<Bluetooth, BTItemAdapter.MyHolder>(Comparator()) {
   private var oldTextView: TextView? = null
   private var newData = ""

   class MyHolder(view: View, private val adapter: BTItemAdapter, private val listener: Listener, private val adapterType: Boolean) : RecyclerView.ViewHolder(view) {
      private val b = ItemBtListBinding.bind(view)
      private var listItem: Bluetooth? = null

      init {
         itemView.setOnClickListener{
            if(adapterType) {
               try {
                  listItem?.device?.createBond()
               }catch (e: SecurityException) {
                  e.printStackTrace()
               }
            }else {
               listItem?.let { it1 -> listener.onClick(it1) }
               adapter.selectTextView(b.tvStatus)
            }
         }
      }

      fun bind(item: Bluetooth) = with(b) {
         tvName.visibility = if(adapterType) View.GONE else View.VISIBLE
         listItem = item

         try {
            tvName.text = item.device.name
         }catch (e: SecurityException) {
            e.printStackTrace()
         }
      }
   }

   class Comparator : DiffUtil.ItemCallback<Bluetooth>() {
      override fun areItemsTheSame(oldItem: Bluetooth, newItem: Bluetooth): Boolean {
         return oldItem == newItem
      }

      override fun areContentsTheSame(oldItem: Bluetooth, newItem: Bluetooth): Boolean {
         return oldItem == newItem
      }
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bt_list, parent,false)
      return MyHolder(view, this, listener, adapterType)
   }

   override fun onBindViewHolder(holder: MyHolder, pos: Int) {
      holder.bind(getItem(pos))
   }

   fun selectTextView(textView: TextView) {
      oldTextView?.visibility = View.GONE
      oldTextView?.text = ""
      oldTextView = textView
      oldTextView?.visibility = View.VISIBLE

      if(newData != "") {
         oldTextView?.text = newData
         oldTextView?.setTextColor(Color.parseColor("#035DAC"))
      }else {
         oldTextView?.text = "연결중..."
      }
   }

   fun setData(data: String){
      newData = data
      notifyDataSetChanged()
   }

   interface Listener {
      fun onClick(device: Bluetooth)
   }
}