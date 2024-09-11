package kr.bodywell.android.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.android.R
import kr.bodywell.android.model.Image

class GalleryAdapter (
	private val context: Context,
	private val itemList: ArrayList<Image>
) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
	private var onLongClickListener: OnLongClickListener? = null

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		if(itemList[position].bitmap == null) {
			val imgPath = context.filesDir.toString() + "/" + itemList[position].imageUri
			val bm = BitmapFactory.decodeFile(imgPath)
			holder.imageView.setImageBitmap(bm)
		}else {
			holder.imageView.setImageBitmap(itemList[position].bitmap)
		}

		holder.imageView.setOnLongClickListener {
			onLongClickListener!!.onLongClick(position)
			true
		}
	}

	override fun getItemCount(): Int {
		return itemList.count()
	}
	interface OnLongClickListener {
		fun onLongClick(pos: Int)
	}

	fun setOnLongClickListener(listener: OnLongClickListener?) {
		onLongClickListener = listener
	}

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val imageView: ImageView = itemView.findViewById(R.id.imageView)
	}
}