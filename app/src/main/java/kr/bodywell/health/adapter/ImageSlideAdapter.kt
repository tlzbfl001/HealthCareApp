package kr.bodywell.health.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import kr.bodywell.health.R
import kr.bodywell.health.model.FileItem

class ImageSlideAdapter (
    private val context: Context,
    private val itemList: ArrayList<FileItem>
) : PagerAdapter() {
    private var onLongClickListener: OnLongClickListener? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_photo_slide2, container, false)
        val imageView: ImageView = view.findViewById(R.id.imageView)

        if(itemList[position].bitmap == null) {
            val imgPath = context.filesDir.toString() + "/" + itemList[position].name
            val bm = BitmapFactory.decodeFile(imgPath)
            imageView.setImageBitmap(bm)
        }else {
            imageView.setImageBitmap(itemList[position].bitmap)
        }

        container.addView(view, 0)

        imageView.setOnLongClickListener {
            onLongClickListener!!.onLongClick(position)
            true
        }

        return view
    }

    override fun getCount(): Int {
        return itemList.count()
    }

    interface OnLongClickListener {
        fun onLongClick(pos: Int)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}