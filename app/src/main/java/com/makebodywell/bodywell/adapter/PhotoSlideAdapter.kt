package com.makebodywell.bodywell.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.model.FoodImage

class PhotoSlideAdapter(
   private val context: Context,
   private val itemList: ArrayList<FoodImage>
) : PagerAdapter() {

   override fun isViewFromObject(view: View, `object`: Any): Boolean {
      return view == `object`
   }

   override fun instantiateItem(container: ViewGroup, position: Int): Any {
      val inflater = LayoutInflater.from(context)
      val view: View = inflater.inflate(R.layout.item_photo_slide, container, false)
      val imageView: ImageView = view.findViewById(R.id.imageView)

      imageView.setImageURI(Uri.parse(itemList[position].imageUri))

      container.addView(view, 0)

      return view
   }

   override fun getCount(): Int {
      return itemList.count()
   }

   override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
      container.removeView(`object` as View)
   }
}