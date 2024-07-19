package kr.bodywell.test.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import kr.bodywell.test.R
import kr.bodywell.test.model.Image

class PhotoSlideAdapter2 (
    private val context: Context,
    private val itemList: ArrayList<Image>
) : PagerAdapter() {
    private var onLongClickListener: OnLongClickListener? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.item_photo_slide2, container, false)
        val imageView: ImageView = view.findViewById(R.id.imageView)

        imageView.setImageURI(Uri.parse(itemList[position].imageUri))

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

    fun setOnLongClickListener(listener: OnLongClickListener?) {
        onLongClickListener = listener
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}