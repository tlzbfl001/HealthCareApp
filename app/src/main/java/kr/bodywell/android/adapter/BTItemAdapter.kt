package kr.bodywell.android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
   class MyHolder(view: View, private val listener: Listener, private val adapterType: Boolean) : RecyclerView.ViewHolder(view) {
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
            }
         }
      }

      fun bind(item: Bluetooth) = with(b) {
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
      return MyHolder(view, listener, adapterType)
   }

   override fun onBindViewHolder(holder: MyHolder, pos: Int) {
      holder.bind(getItem(pos))
   }

   interface Listener {
      fun onClick(device: Bluetooth)
   }
}