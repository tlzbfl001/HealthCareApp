package kr.bodywell.android.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import kr.bodywell.android.R
import kr.bodywell.android.model.Image

class PhotoSlideAdapter(
   private val context: Context,
   private val itemList: ArrayList<Image>
) : PagerAdapter() {

   override fun isViewFromObject(view: View, `object`: Any): Boolean {
      return view == `object`
   }

   override fun instantiateItem(container: ViewGroup, position: Int): Any {
      val inflater = LayoutInflater.from(context)
      val view: View = inflater.inflate(R.layout.item_photo_slide, container, false)
      val imageView: ImageView = view.findViewById(R.id.imageView)

      if(itemList[position].bitmap == null) {
         val imgPath = context.filesDir.toString() + "/" + itemList[position].imageName
         val bm = BitmapFactory.decodeFile(imgPath)
         imageView.setImageBitmap(bm)
      }else {
         imageView.setImageBitmap(itemList[position].bitmap)
      }

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