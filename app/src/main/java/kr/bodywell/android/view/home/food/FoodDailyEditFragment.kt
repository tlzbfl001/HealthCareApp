package kr.bodywell.android.view.home.food

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import kr.bodywell.android.R
import kr.bodywell.android.adapter.PhotoSlideAdapter2
import kr.bodywell.android.database.DBHelper.Companion.DAILY_FOOD
import kr.bodywell.android.database.DBHelper.Companion.IMAGE
import kr.bodywell.android.database.DBHelper.Companion.IS_UPDATED
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentFoodDailyEditBinding
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.Food
import kr.bodywell.android.model.Image
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.deleteFile
import kr.bodywell.android.util.CustomUtil.replaceFragment4
import kr.bodywell.android.util.CustomUtil.saveImage
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.util.PermissionUtil.CAMERA_PERMISSION_1
import kr.bodywell.android.util.PermissionUtil.CAMERA_PERMISSION_2
import kr.bodywell.android.util.PermissionUtil.CAMERA_REQUEST_CODE
import kr.bodywell.android.util.PermissionUtil.STORAGE_REQUEST_CODE
import kr.bodywell.android.util.PermissionUtil.checkCameraPermission

class FoodDailyEditFragment : Fragment() {
   private var _binding: FragmentFoodDailyEditBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
   private var bundle = Bundle()
   private var getDailyFood = Food()
   private var imageList = ArrayList<Image>()
   private var dialog: Dialog? = null
   private var type = Constant.BREAKFAST.name
   private var dailyFoodId = 0
   private var count = 1

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment4(requireActivity(), FoodDetailFragment(), bundle)
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodDailyEditBinding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.mainLayout)

      pLauncher = registerForActivityResult(
         ActivityResultContracts.RequestMultiplePermissions()
      ){

      }

      dataManager = DataManager(activity)
      dataManager.open()

      dailyFoodId = arguments?.getString("dailyFoodId")!!.toInt()
      type = arguments?.getString("type").toString()
      bundle.putString("type", type)

      getDailyFood = dataManager.getDailyFood("id", dailyFoodId)

      binding.tvName.text = getDailyFood.name
      count = getDailyFood.count

      val getImage = dataManager.getImage(getDailyFood.name)
      for(i in 0 until getImage.size) imageList.add(getImage[i])

      binding.clBack.setOnClickListener {
         replaceFragment4(requireActivity(), FoodDetailFragment(), bundle)
      }

      binding.clPhoto.setOnClickListener {
         if(checkCameraPermission(requireActivity())) {
            dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)
            val bottomSheetView = layoutInflater.inflate(R.layout.dialog_camera, null)

            val clCamera = bottomSheetView.findViewById<ConstraintLayout>(R.id.clCamera)
            val clPhoto = bottomSheetView.findViewById<ConstraintLayout>(R.id.clPhoto)

            clCamera.setOnClickListener {
               val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // 카메라 사용을위한 Intent 생성
               startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }

            clPhoto.setOnClickListener {
               val intent = Intent(Intent.ACTION_PICK) // 갤러리에서 이미지를 선택하는 Intent 생성
               intent.type = "image/*"
               startActivityForResult(intent, STORAGE_REQUEST_CODE)
            }

            dialog!!.setContentView(bottomSheetView)
            dialog!!.show()
         }else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
               pLauncher.launch(CAMERA_PERMISSION_2)
            }else {
               pLauncher.launch(CAMERA_PERMISSION_1)
            }
         }
      }

      binding.ivMinus.setOnClickListener {
         if(count > 1) count--
         settingData()
      }

      binding.ivPlus.setOnClickListener {
         if(count < 100) count++
         settingData()
      }

      binding.cvEdit.setOnClickListener {
         for(i in 0 until getImage.size) {
            var check = false
            for(j in 0 until imageList.size) {
               if(getImage[i].imageUri == imageList[j].imageUri) check = true
            }

            if(!check) {
               deleteFile(requireActivity(), getImage[i].imageUri)
               dataManager.deleteItem(IMAGE, "id", getImage[i].id)
            }
         }

         for(i in 0 until imageList.size) {
            if(imageList[i].bitmap != null) {
               val result = saveImage(requireActivity(), imageList[i].bitmap!!)
               if(result != "") dataManager.insertImage(Image(type = type, dataName = imageList[i].dataName, imageUri = result, createdAt = selectedDate.toString()))
            }
         }

         dataManager.updateInt(DAILY_FOOD, "count", count, "id", dailyFoodId)
         dataManager.updateInt(DAILY_FOOD, IS_UPDATED, 1, "id", dailyFoodId)

         Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
         replaceFragment4(requireActivity(), FoodDetailFragment(), bundle)
      }

      photoView()
      settingData()

      return binding.root
   }

   private fun settingData() {
      binding.tvCount.text = count.toString()
      binding.tvAmount.text = (getDailyFood.amount * count).toString()
      binding.tvKcal.text = (getDailyFood.kcal * count).toString()
      binding.tvCar.text = String.format("%.1f", (getDailyFood.carbohydrate * count))
      binding.tvProtein.text = String.format("%.1f", (getDailyFood.protein * count))
      binding.tvFat.text = String.format("%.1f", (getDailyFood.fat * count))
      binding.tvSalt.text = String.format("%.1f", (getDailyFood.salt * count))
      binding.tvSugar.text = String.format("%.1f", (getDailyFood.sugar * count))
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
               if(data!!.extras?.get("data") != null){
                  val bitmap = data.extras?.get("data") as Bitmap
                  imageList.add(Image(type = type, dataName = getDailyFood.name, bitmap = bitmap, createdAt = selectedDate.toString()))
                  photoView()
               }
               dialog!!.dismiss()
            }
            STORAGE_REQUEST_CODE -> {
               val fileUri = data!!.data
               try{
                  val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, fileUri)
                  imageList.add(Image(type = type, dataName = getDailyFood.name, bitmap = bitmap,  createdAt = selectedDate.toString()))
                  photoView()
               }catch (e: Exception) {
                  e.printStackTrace()
               }
               dialog!!.dismiss()
            }
         }
      }
   }
}