package com.makebodywell.bodywell.view.home.food

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.PhotoSlideAdapter2
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_FOOD
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_IMAGE
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentFoodEditBinding
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.model.Image
import com.makebodywell.bodywell.util.CalendarUtil
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.util.PermissionUtil
import com.makebodywell.bodywell.util.PermissionUtil.Companion.CAMERA_PERMISSION_1
import com.makebodywell.bodywell.util.PermissionUtil.Companion.CAMERA_PERMISSION_2
import com.makebodywell.bodywell.util.PermissionUtil.Companion.CAMERA_PERMISSION_3
import com.makebodywell.bodywell.util.PermissionUtil.Companion.CAMERA_REQUEST_CODE
import com.makebodywell.bodywell.util.PermissionUtil.Companion.STORAGE_REQUEST_CODE
import com.makebodywell.bodywell.util.PermissionUtil.Companion.cameraRequest
import com.makebodywell.bodywell.util.PermissionUtil.Companion.getImageUriWithAuthority
import com.makebodywell.bodywell.util.PermissionUtil.Companion.randomFileName
import com.makebodywell.bodywell.util.PermissionUtil.Companion.saveFile
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat

class FoodEditFragment : Fragment() {
   private var _binding: FragmentFoodEditBinding? = null
   private val binding get() = _binding!!

   private var bundle = Bundle()
   private var dataManager: DataManager? = null
   private var getFood = Food()
   private var imageList = ArrayList<Image>()
   private var dialog: Dialog? = null

   private var calendarDate = ""
   private var type = ""
   private var id = -1

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodEditBinding.inflate(layoutInflater)

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }

      dataManager = DataManager(activity)
      dataManager!!.open()

      calendarDate = arguments?.getString("calendarDate").toString()
      type = arguments?.getString("type").toString()
      id = arguments?.getString("id").toString().toInt()

      bundle.putString("calendarDate", calendarDate)
      bundle.putString("type", type)

      getFood = dataManager!!.getFood(id)
      var count = getFood.count

      binding.tvName.text = getFood.name

      dataTextView(count)

      binding.clBack.setOnClickListener {
         replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
      }

      binding.clPhoto.setOnClickListener {
         if (cameraRequest(requireActivity())) {
            dialog = Dialog(requireActivity())
            dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog!!.setContentView(R.layout.dialog_gallery)

            val clCamera = dialog!!.findViewById<ConstraintLayout>(R.id.clCamera)
            val clGallery = dialog!!.findViewById<ConstraintLayout>(R.id.clGallery)

            clCamera.setOnClickListener {
               val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
               startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }

            clGallery.setOnClickListener {
               val intent = Intent(Intent.ACTION_PICK)
               intent.type = MediaStore.Images.Media.CONTENT_TYPE
               startActivityForResult(intent, STORAGE_REQUEST_CODE)
            }

            dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.setGravity(Gravity.BOTTOM)
            dialog!!.show()
         }
      }

      binding.ivMinus.setOnClickListener {
         if(count > 1) {
            count--
            dataTextView(count)
         }
      }

      binding.ivPlus.setOnClickListener {
         count++
         dataTextView(count)
      }

      binding.cvSave.setOnClickListener {
         dataManager!!.deleteItem(TABLE_IMAGE, "dataId", id)

         for(i in 0 until imageList.size) {
            dataManager!!.insertImage(imageList[i])
         }

         dataManager!!.updateInt(TABLE_FOOD, "count", count, id)

         Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
         replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
      }

      val getData = dataManager!!.getImage(id)
      for(i in 0 until getData.size) {
         imageList.add(getData[i])
      }

      photoView(imageList)

      return binding.root
   }

   private fun dataTextView(count: Int) {
      binding.tvCount.text = count.toString()
      binding.tvAmount.text = (getFood.amount * count).toString()
      binding.tvKcal.text = (getFood.kcal * count).toString()
      binding.tvCar.text = String.format("%.1f", (getFood.carbohydrate * count))
      binding.tvProtein.text = String.format("%.1f", (getFood.protein * count))
      binding.tvFat.text = String.format("%.1f", (getFood.fat * count))
      binding.tvSalt.text = String.format("%.1f", (getFood.salt * count))
      binding.tvSugar.text = String.format("%.1f", (getFood.sugar * count))
   }

   private fun photoView(imageList: ArrayList<Image>) {
      if(imageList.size > 0) {
         binding.ivView.visibility = View.GONE
         binding.viewPager.visibility = View.VISIBLE

         val adapter = PhotoSlideAdapter2(requireActivity(), imageList)
         binding.viewPager.setPadding(0, 0, 0, 0)

         adapter.setOnLongClickListener(object : PhotoSlideAdapter2.OnLongClickListener {
            override fun onLongClick(pos: Int) {
               val dialog = AlertDialog.Builder(context)
                  .setMessage("삭제하시겠습니까?")
                  .setPositiveButton("확인") { _, _ ->
                     imageList.removeAt(pos)

                     if(imageList.size == 0) {
                        binding.ivView.visibility = View.VISIBLE
                        binding.viewPager.visibility = View.GONE
                     }

                     adapter.notifyDataSetChanged()
                     binding.viewPager.adapter = adapter

                     Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                  }
                  .setNegativeButton("취소", null)
                  .create()
               dialog.show()
            }
         })

         binding.viewPager.adapter = adapter
      }
   }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      if(resultCode == Activity.RESULT_OK){
         when(requestCode){
            CAMERA_REQUEST_CODE -> {
               if(data?.extras?.get("data") != null){
                  val img = data.extras?.get("data") as Bitmap
                  val uri = saveFile(requireActivity(), randomFileName(), "image/jpeg", img)
                  imageList.add(Image(imageUri = uri.toString(), type = type.toInt(), regDate = selectedDate.toString()))
                  photoView(imageList)

                  dialog!!.dismiss()
               }
            }
            STORAGE_REQUEST_CODE -> {
               val uri = data!!.data
               if(data.data!!.toString().contains("com.google.android.apps.photos.contentprovider")) {
                  val uriParse = getImageUriWithAuthority(requireActivity(), uri)
                  imageList.add(Image(imageUri = uriParse!!, type = type.toInt(), regDate = selectedDate.toString()))
                  photoView(imageList)
               }else {
                  imageList.add(Image(imageUri = uri.toString(), type = type.toInt(), regDate = selectedDate.toString()))
                  photoView(imageList)
               }

               dialog!!.dismiss()
            }
         }
      }
   }
}