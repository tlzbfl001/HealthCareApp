package kr.bodywell.test.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.test.R
import kr.bodywell.test.model.Image

class PhotoViewAdapter (
   private val context: Context,
   private val itemList: ArrayList<Image>
) : RecyclerView.Adapter<PhotoViewAdapter.ViewHolder>() {
   class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val imageView: ImageView = itemView.findViewById(R.id.imageView)
   }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo_view, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val imgPath = context.filesDir.toString() + "/" + itemList[position].imageUri // 내부 저장소에 저장되어 있는 이미지 경로
      val bm = BitmapFactory.decodeFile(imgPath)
      holder.imageView.setImageBitmap(bm)
   }

   override fun getItemCount(): Int {
      return itemList.size
   }
}