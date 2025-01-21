package kr.bodywell.android.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.android.R
import kr.bodywell.android.model.FileItem
import kr.bodywell.android.view.MainViewModel
import java.io.File

class GalleryAdapter (
	private val viewModel: MainViewModel,
	private val itemList: ArrayList<FileItem>
) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
	private var context: Context? = null
	private var onLongClickListener: OnLongClickListener? = null
	private var onClickListener: OnClickListener? = null

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
		context = parent.context
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		// 실제 파일이름과 비트맵 파일로 구분해서 적용
		if(itemList[position].bitmap == null) {
			val imgPath = context!!.filesDir.toString() + "/" + itemList[position].name
			val file = File(imgPath)
			if(file.exists()){
				val bm = BitmapFactory.decodeFile(imgPath)
				holder.imageView.setImageBitmap(bm)
			}
		}else {
			holder.imageView.setImageBitmap(itemList[position].bitmap)
		}

		// 이미지 다중선택 로직
		val lifeCycleOwner = context as LifecycleOwner
		viewModel.imgSelectedVM.observe(lifeCycleOwner) {
			if(!it) {
				holder.checkbox.visibility = View.GONE
			}else {
				holder.checkbox.visibility = View.VISIBLE
				holder.checkbox.isChecked = false
			}
		}

		holder.imageView.setOnLongClickListener {
			onLongClickListener!!.onLongClick(position)
			true
		}

		holder.imageView.setOnClickListener {
			holder.checkbox.isChecked = !holder.checkbox.isChecked
			onClickListener!!.onClick(position, holder.checkbox.isChecked)
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

	interface OnClickListener {
		fun onClick(pos: Int, checked: Boolean)
	}

	fun setOnClickListener(listener: OnClickListener) {
		onClickListener = listener
	}

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val imageView: ImageView = itemView.findViewById(R.id.imageView)
		val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
		val view: View = itemView
	}
}