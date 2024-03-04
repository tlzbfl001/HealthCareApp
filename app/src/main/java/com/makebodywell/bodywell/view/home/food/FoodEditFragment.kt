package com.makebodywell.bodywell.view.home.food

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.PhotoSlideAdapter2
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DAILY_FOOD
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_IMAGE
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_SEARCH
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentFoodEditBinding
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.model.Image
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.util.PermissionUtil.Companion.CAMERA_REQUEST_CODE
import com.makebodywell.bodywell.util.PermissionUtil.Companion.STORAGE_REQUEST_CODE
import com.makebodywell.bodywell.util.PermissionUtil.Companion.cameraRequest
import com.makebodywell.bodywell.util.PermissionUtil.Companion.getImageUriWithAuthority
import com.makebodywell.bodywell.util.PermissionUtil.Companion.randomFileName
import com.makebodywell.bodywell.util.PermissionUtil.Companion.saveFile
import java.time.LocalDate

class FoodEditFragment : Fragment() {
   private var _binding: FragmentFoodEditBinding? = null
   private val binding get() = _binding!!

   private var bundle = Bundle()
   private var dataManager: DataManager? = null
   private var getFood = Food()
   private var imageList = ArrayList<Image>()
   private var dialog: Dialog? = null
   private var type = "1"
   private var dataId = -1

   @SuppressLint("DiscouragedApi", "InternalInsetResource")
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

      type = arguments?.getString("type").toString()
//      dataId = arguments?.getString("dataId").toString().toInt()
      bundle.putString("type", type)

      getFood = dataManager!!.getFood(dataId)

      dataTextView()

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

      binding.cvSave.setOnClickListener {
         dataManager!!.deleteItem(TABLE_IMAGE, "dataId", dataId)

         for(i in 0 until imageList.size) {
            dataManager!!.insertImage(imageList[i])
         }

         val getDailyFood = dataManager!!.getDailyFood(type = type.toInt(), name = getFood.name, selectedDate.toString())
         if(getDailyFood.regDate == "") {
            dataManager!!.insertDailyFood(Food(type = type.toInt(), name = getFood.name, unit = getFood.unit, amount = getFood.amount,
               kcal = getFood.kcal, carbohydrate = getFood.carbohydrate, protein = getFood.protein, fat = getFood.fat, salt = getFood.salt,
               sugar = getFood.sugar, count = 1, regDate = LocalDate.now().toString()))
         }else {
            dataManager!!.updateInt(TABLE_DAILY_FOOD, "count", getDailyFood.count + 1, getDailyFood.id)
         }

//         dataManager!!.updateInt(TABLE_FOOD, "count", count, dataId)

         val getSearch = dataManager!!.getSearch("food", getFood.name)
         dataManager!!.updateInt(TABLE_SEARCH, "count", getSearch.count + 1, getSearch.id)

         when(type) {
            "1" -> replaceFragment1(requireActivity(), FoodBreakfastFragment())
            "2" -> replaceFragment1(requireActivity(), FoodLunchFragment())
            "3" -> replaceFragment1(requireActivity(), FoodDinnerFragment())
            "4" -> replaceFragment1(requireActivity(), FoodSnackFragment())
         }

         Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
      }

      val getData = dataManager!!.getImage(dataId)
      for(i in 0 until getData.size) {
         imageList.add(getData[i])
      }

      photoView()

      return binding.root
   }

   private fun dataTextView() {
      if(getFood.name != "") binding.tvName.text = getFood.name
      if(getFood.amount > 0) binding.tvAmount.text = getFood.kcal.toString()
      if(getFood.carbohydrate > 0.0) binding.tvCar.text = String.format("%.1f", getFood.carbohydrate)
      if(getFood.protein > 0.0) binding.tvProtein.text = String.format("%.1f", getFood.protein)
      if(getFood.fat > 0.0) binding.tvFat.text = String.format("%.1f", getFood.fat)
      if(getFood.salt > 0.0) binding.tvSalt.text = String.format("%.1f", getFood.salt)
      if(getFood.sugar > 0.0) binding.tvSugar.text = String.format("%.1f", getFood.sugar)
   }

   private fun photoView() {
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
                  imageList.add(Image(imageUri = uri.toString(), type = type.toInt(), dataId = dataId, regDate = selectedDate.toString()))
                  photoView()

                  dialog!!.dismiss()
               }
            }
            STORAGE_REQUEST_CODE -> {
               val uri = data!!.data

               if(data.data!!.toString().contains("com.google.android.apps.photos.contentprovider")) {
                  val uriParse = getImageUriWithAuthority(requireActivity(), uri)
                  imageList.add(Image(imageUri = uriParse!!, type = type.toInt(), dataId = dataId, regDate = selectedDate.toString()))
                  photoView()
               }else {
                  imageList.add(Image(imageUri = uri.toString(), type = type.toInt(), dataId = dataId, regDate = selectedDate.toString()))
                  photoView()
               }

               dialog!!.dismiss()
            }
         }
      }
   }
}