package kr.bodywell.android.view.home.food

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
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
import androidx.core.content.FileProvider
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
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.replaceFragment4
import kr.bodywell.android.util.CustomUtil.saveImage
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.util.PermissionUtil.CAMERA_PERMISSION_1
import kr.bodywell.android.util.PermissionUtil.CAMERA_PERMISSION_2
import kr.bodywell.android.util.PermissionUtil.checkCameraPermission
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class FoodDailyEditFragment : Fragment() {
   private var _binding: FragmentFoodDailyEditBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
   private lateinit var cLauncher: ActivityResultLauncher<Intent>
   private var fileAbsolutePath: String? = null
   private var bitmap: Bitmap? = null
   private var pictureFlag = 0
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
      ){}

      dataManager = DataManager(activity)
      dataManager.open()

      dailyFoodId = arguments?.getString("dailyFoodId")!!.toInt()
      type = arguments?.getString("type").toString()
      bundle.putString("type", type)

      getDailyFood = dataManager.getDailyFood("id", dailyFoodId)

      binding.tvName.text = getDailyFood.name
      count = getDailyFood.count

      val getImage = dataManager.getImage(type, getDailyFood.name, selectedDate.toString())
      for(i in 0 until getImage.size) imageList.add(getImage[i])

      binding.clBack.setOnClickListener {
         replaceFragment4(requireActivity(), FoodDetailFragment(), bundle)
      }

      binding.clPhoto.setOnClickListener {
         if(checkCameraPermission(requireActivity())) {
            dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)
            val bottomSheetView = layoutInflater.inflate(R.layout.dialog_camera, null)

            val clCamera = bottomSheetView.findViewById<ConstraintLayout>(R.id.clCamera)
            val clGallery = bottomSheetView.findViewById<ConstraintLayout>(R.id.clGallery)

            clCamera.setOnClickListener {
               val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // 카메라앱 호출을위한 Intent생성

               val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)	// 이미지 경로 지정
               val file = File.createTempFile(
                  SimpleDateFormat("yyMMddhhmmSSS").format(Date()),
                  ".png",
                  storageDir
               ).apply {
                  fileAbsolutePath = absolutePath // 절대경로 변수에 저장
               }

               // 사진이 저장될 경로를 관리할 Uri 객체를 생성한다.
               val contentUri = FileProvider.getUriForFile(requireActivity(), requireActivity().applicationContext.packageName + ".file_provider", file)

               intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
               cLauncher.launch(intent)
               pictureFlag = 1
            }

            clGallery.setOnClickListener {
               val intent = Intent(Intent.ACTION_PICK) // 갤러리에서 이미지를 선택하는 Intent 생성
               intent.type = "image/*"
               cLauncher.launch(intent)
               pictureFlag = 2
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

      val contract1=ActivityResultContracts.StartActivityForResult()
      cLauncher = registerForActivityResult(contract1){
         if(pictureFlag == 1) { // 카메라
            val file = File(fileAbsolutePath)
            val decode = ImageDecoder.createSource(requireActivity().contentResolver, Uri.fromFile(file.absoluteFile)) // 카메라에서 찍은 사진을 디코딩
            bitmap = ImageDecoder.decodeBitmap(decode) // 디코딩한 사진을 비트맵으로 변환
            imageList.add(Image(type = type, dataName = getDailyFood.name, bitmap = bitmap, createdAt = selectedDate.toString()))
            photoView()
            file.delete()
         }else if(pictureFlag == 2) { // 갤러리
            val uri = it.data?.data // 선택한 이미지의 주소
            if(uri != null) { // 이미지파일 읽어와서 설정하기
               bitmap = CustomUtil.getRotatedBitmap(requireActivity(), it.data?.data!!) // 이미지 회전하기
               imageList.add(Image(type = type, dataName = getDailyFood.name, bitmap = bitmap, createdAt = selectedDate.toString()))
               photoView()
            }
         }
         dialog!!.dismiss()
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
               if(getImage[i].imageName == imageList[j].imageName) check = true
            }

            if(!check) {
               File(requireActivity().filesDir, getImage[i].imageName).delete()
               dataManager.deleteItem(IMAGE, "id", getImage[i].id)
            }
         }

         for(i in 0 until imageList.size) {
            if(imageList[i].bitmap != null) {
               val result = saveImage(requireActivity(), imageList[i].bitmap!!)
               if(result != "") dataManager.insertImage(Image(type = type, dataName = imageList[i].dataName, imageName = result, createdAt = selectedDate.toString()))
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
}