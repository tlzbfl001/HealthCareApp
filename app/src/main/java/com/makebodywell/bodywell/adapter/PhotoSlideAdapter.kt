package com.makebodywell.bodywell.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.model.FoodImage

class PhotoSlideAdapter (
   private val imageList: ArrayList<FoodImage>
) : RecyclerView.Adapter<PhotoSlideAdapter.ViewHolder>() {
   class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val imageView: ImageView = itemView.findViewById(R.id.imageView)
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo_slide, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.imageView.setImageURI(Uri.parse(imageList[position].imageUri))
   }

   override fun getItemCount(): Int {
      return imageList.size
   }

   private val runnable = Runnable {
      imageList.addAll(imageList)
      notifyDataSetChanged()
   }
}