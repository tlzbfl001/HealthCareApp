package kr.bodywell.android.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.PagerAdapter
import kr.bodywell.android.R
import kr.bodywell.android.model.FileItem
import kr.bodywell.android.util.CustomUtil.replaceFragment2
import kr.bodywell.android.view.note.GalleryFragment

class NoteImageSlideAdapter(
   private val context: Context,
   private val noteId: String,
   private val itemList: List<FileItem>
) : PagerAdapter() {
   private val bundle = Bundle()

   override fun isViewFromObject(view: View, `object`: Any): Boolean {
      return view == `object`
   }

   override fun instantiateItem(container: ViewGroup, position: Int): Any {
      val inflater = LayoutInflater.from(context)
      val view: View = inflater.inflate(R.layout.item_photo_slide, container, false)
      val imageView: ImageView = view.findViewById(R.id.imageView)

      val imgPath = context.filesDir.toString() + "/" + itemList[position].name
      val bm = BitmapFactory.decodeFile(imgPath)
      imageView.setImageBitmap(bm)

      imageView.setOnClickListener {
         bundle.putString("noteId", noteId)
         val fragmentManager = (context as FragmentActivity).supportFragmentManager
         replaceFragment2(fragmentManager, GalleryFragment(), bundle)
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