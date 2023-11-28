package com.makebodywell.bodywell.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.makebodywell.bodywell.R

class CalendarPhotoAdapter (
   private var models: List<Int>? = null,
   private var context : Context
) : PagerAdapter() {
   override fun getCount(): Int {
      return models!!.size
   }

   override fun isViewFromObject(view: View, `object`: Any): Boolean {
      return view == `object`
   }

   override fun instantiateItem(container: ViewGroup, position: Int): Any {
      val inflater = LayoutInflater.from(context)
      val view: View = inflater.inflate(R.layout.item_calendar_photo, container, false)
      val imageView: ImageView = view.findViewById(R.id.image)

      imageView.setImageResource(models!![position])

      container.addView(view, 0)

      return view
   }

   override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
      container.removeView(`object` as View)
   }
}