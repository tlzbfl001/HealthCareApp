package kr.bodywell.test.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.test.R

class WaterAdapter (
   private var count: Int
) : RecyclerView.Adapter<WaterAdapter.ViewHolder>() {

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_water, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {}

   override fun getItemCount(): Int {
      return count
   }

   inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
}