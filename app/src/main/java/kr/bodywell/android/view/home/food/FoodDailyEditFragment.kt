package kr.bodywell.android.view.home.food

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import kr.bodywell.android.R
import kr.bodywell.android.adapter.PhotoSlideAdapter2
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DAILY_FOOD
import kr.bodywell.android.database.DBHelper.Companion.TABLE_IMAGE
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentFoodDailyEditBinding
import kr.bodywell.android.model.Food
import kr.bodywell.android.model.Image
import kr.bodywell.android.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment1
import kr.bodywell.android.util.PermissionUtil.Companion.CAMERA_REQUEST_CODE
import kr.bodywell.android.util.PermissionUtil.Companion.STORAGE_REQUEST_CODE
import kr.bodywell.android.util.PermissionUtil.Companion.saveFile

class FoodDailyEditFragment : Fragment() {
   private var _binding: FragmentFoodDailyEditBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private var bundle = Bundle()
   private var getDailyFood = Food()
   private var imageList = ArrayList<Image>()
   private var dialog: Dialog? = null
   private var type = "BREAKFAST"
   private var dailyFoodId = 0
   private var count = 1

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment()
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

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

      dataManager = DataManager(activity)
      dataManager.open()

      dailyFoodId = arguments?.getString("dailyFoodId")!!.toInt()
      type = arguments?.getString("type").toString()
      bundle.putString("type", type)

      getDailyFood = dataManager.getDailyFood("id", dailyFoodId)

      binding.tvName.text = getDailyFood.name
      count = getDailyFood.count

      val getImage = dataManager.getImage(dailyFoodId)

      for(i in 0 until getImage.size) {
         imageList.add(getImage[i])
      }

      binding.clBack.setOnClickListener {
         replaceFragment()
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
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
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

      binding.cvEdit.setOnClickListener {
         dataManager.deleteItem(TABLE_IMAGE, "dataId", dailyFoodId, "type", type)

         for(i in 0 until imageList.size) {
            dataManager.insertImage(imageList[i])
         }

         dataManager.updateDailyFood(Food(id = dailyFoodId, amount = getDailyFood.amount * count, kcal = getDailyFood.kcal * count,
            carbohydrate = getDailyFood.carbohydrate * count, protein = getDailyFood.protein * count, fat = getDailyFood.fat * count,
            salt = getDailyFood.salt * count, sugar = getDailyFood.sugar * count, count = count))

         Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()

         replaceFragment()
      }

      photoView()
      dataTextView()

      return binding.root
   }

   private fun dataTextView() {
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
               if(data?.extras?.get("data") != null){
                  val img = data.extras?.get("data") as Bitmap
                  val uri = saveFile(requireActivity(), "image/jpeg", img)
                  
                  imageList.add(Image(type = type, dataId = dailyFoodId, imageUri = uri.toString(), regDate = selectedDate.toString()))
                  photoView()

                  dialog!!.dismiss()
               }
            }
            STORAGE_REQUEST_CODE -> {
               val uri = data!!.data
               val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
               requireActivity().contentResolver.takePersistableUriPermission(uri!!, takeFlags)

               imageList.add(Image(type = type, dataId = dailyFoodId, imageUri = uri.toString(), regDate = selectedDate.toString()))
               photoView()

               dialog!!.dismiss()
            }
         }
      }
   }

   private fun replaceFragment() {
      when(type) {
         "BREAKFAST" -> replaceFragment1(requireActivity(), FoodBreakfastFragment())
         "LUNCH" -> replaceFragment1(requireActivity(), FoodLunchFragment())
         "DINNER" -> replaceFragment1(requireActivity(), FoodDinnerFragment())
         else -> replaceFragment1(requireActivity(), FoodSnackFragment())
      }
   }
}