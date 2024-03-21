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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.PhotoSlideAdapter2
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DAILY_FOOD
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_IMAGE
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentFoodDailyEditBinding
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.model.Image
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.PermissionUtil.Companion.CAMERA_REQUEST_CODE
import com.makebodywell.bodywell.util.PermissionUtil.Companion.STORAGE_REQUEST_CODE
import com.makebodywell.bodywell.util.PermissionUtil.Companion.cameraRequest
import com.makebodywell.bodywell.util.PermissionUtil.Companion.getImageUriWithAuthority
import com.makebodywell.bodywell.util.PermissionUtil.Companion.saveFile
import com.makebodywell.bodywell.view.home.MainActivity

class FoodDailyEditFragment : Fragment(), MainActivity.OnBackPressedListener {
   private var _binding: FragmentFoodDailyEditBinding? = null
   private val binding get() = _binding!!

   private var bundle = Bundle()
   private var dataManager: DataManager? = null
   private var getFood = Food()
   private var imageList = ArrayList<Image>()
   private var dialog: Dialog? = null
   private var type = "1"
   private var dataId = -1
   private var count = 1

   @SuppressLint("DiscouragedApi", "InternalInsetResource")
   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodDailyEditBinding.inflate(layoutInflater)

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }

      (context as MainActivity).setOnBackPressedListener(this)

      dataManager = DataManager(activity)
      dataManager!!.open()

      type = arguments?.getString("type").toString()
      dataId = arguments?.getString("dataId").toString().toInt()
      bundle.putString("type", type)

      getFood = dataManager!!.getDailyFood(dataId)

      binding.tvName.text = getFood.name
      count = getFood.count

      val getImage = dataManager!!.getImage(dataId)
      for(i in 0 until getImage.size) {
         imageList.add(getImage[i])
      }

      binding.clBack.setOnClickListener {
         when(type) {
            "1" -> replaceFragment1(requireActivity(), FoodBreakfastFragment())
            "2" -> replaceFragment1(requireActivity(), FoodLunchFragment())
            "3" -> replaceFragment1(requireActivity(), FoodDinnerFragment())
            "4" -> replaceFragment1(requireActivity(), FoodSnackFragment())
         }
      }

      binding.clPhoto.setOnClickListener {
         dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)
         val bottomSheetView = layoutInflater.inflate(R.layout.dialog_camera, null)

         val clCamera = bottomSheetView.findViewById<ConstraintLayout>(R.id.clCamera)
         val clPhoto = bottomSheetView.findViewById<ConstraintLayout>(R.id.clPhoto)

         clCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
         }

         clPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(intent, STORAGE_REQUEST_CODE)
         }

         dialog!!.setContentView(bottomSheetView)
         dialog!!.show()
      }

      binding.ivMinus.setOnClickListener {
         if(count > 1) {
            count--
         }
         dataTextView()
      }

      binding.ivPlus.setOnClickListener {
         if(count < 100) {
            count++
         }
         dataTextView()
      }

      binding.cvSave.setOnClickListener {
         dataManager!!.deleteItem(TABLE_IMAGE, "dataId", dataId)

         for(i in 0 until imageList.size) {
            dataManager!!.insertImage(imageList[i])
         }

         dataManager!!.updateInt(TABLE_DAILY_FOOD, "count", count, dataId)

         when(type) {
            "1" -> replaceFragment1(requireActivity(), FoodBreakfastFragment())
            "2" -> replaceFragment1(requireActivity(), FoodLunchFragment())
            "3" -> replaceFragment1(requireActivity(), FoodDinnerFragment())
            "4" -> replaceFragment1(requireActivity(), FoodSnackFragment())
         }

         Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
      }

      photoView()
      dataTextView()

      return binding.root
   }

   private fun dataTextView() {
      binding.tvCount.text = count.toString()
      binding.tvAmount.text = (getFood.amount * count).toString()
      binding.tvKcal.text = (getFood.kcal * count).toString()
      binding.tvCar.text = String.format("%.1f", (getFood.carbohydrate * count))
      binding.tvProtein.text = String.format("%.1f", (getFood.protein * count))
      binding.tvFat.text = String.format("%.1f", (getFood.fat * count))
      binding.tvSalt.text = String.format("%.1f", (getFood.salt * count))
      binding.tvSugar.text = String.format("%.1f", (getFood.sugar * count))
   }

   private fun photoView() {
      if(imageList.size > 0) {
         binding.ivView.visibility = View.GONE
         binding.viewPager.visibility = View.VISIBLE

         val adapter = PhotoSlideAdapter2(requireActivity(), imageList)
         binding.viewPager.setPadding(0, 0, 0, 0)

         adapter.setOnLongClickListener(object : PhotoSlideAdapter2.OnLongClickListener {
            override fun onLongClick(pos: Int) {
               val dialog = AlertDialog.Builder(context, R.style.AlertDialogStyle)
                  .setTitle("사진 삭제")
                  .setMessage("정말 삭제하시겠습니까?")
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
                  val uri = saveFile(requireActivity(), "image/jpeg", img)
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

   override fun onBackPressed() {
      val activity = activity as MainActivity?
      activity!!.setOnBackPressedListener(null)

      when(type) {
         "1" -> replaceFragment1(requireActivity(), FoodBreakfastFragment())
         "2" -> replaceFragment1(requireActivity(), FoodLunchFragment())
         "3" -> replaceFragment1(requireActivity(), FoodDinnerFragment())
         "4" -> replaceFragment1(requireActivity(), FoodSnackFragment())
      }
   }
}