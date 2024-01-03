package com.makebodywell.bodywell.adapter

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.model.Image

class GalleryAdapter(
    private val context: Context,
    private val itemList: ArrayList<Image>
) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    private var dataManager: DataManager? = null

    init {
        dataManager = DataManager(context)
        dataManager!!.open()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageURI(Uri.parse(itemList[position].imageUri))

        holder.imageView.setOnLongClickListener {
            val dialog = AlertDialog.Builder(context)
                .setMessage("삭제하시겠습니까?")
                .setPositiveButton("확인") { _, _ ->
                    dataManager!!.deleteImage(itemList[position].id)

                    itemList.removeAt(position)
                    notifyDataSetChanged()
                }
                .setNegativeButton("취소", null)
                .create()
            dialog.show()
            true
        }
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
}