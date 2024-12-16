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
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
import com.github.f4b6a3.uuid.UuidCreator
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.adapter.PhotoSlideAdapter2
import kr.bodywell.android.databinding.FragmentFoodDailyEditBinding
import kr.bodywell.android.model.Constants.BREAKFAST
import kr.bodywell.android.model.Constants.DIETS
import kr.bodywell.android.model.Constants.FILES
import kr.bodywell.android.model.FileItem
import kr.bodywell.android.model.Food
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.getDietImages
import kr.bodywell.android.util.CustomUtil.getRotatedBitmap
import kr.bodywell.android.util.CustomUtil.powerSync
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
   private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
   private lateinit var cLauncher: ActivityResultLauncher<Intent>
   private var bundle = Bundle()
   private var dialog: Dialog? = null
   private var getDiets = Food()
   private var getFiles = ArrayList<FileItem>()
   private var imageList = ArrayList<FileItem>()
   private var fileAbsolutePath: String? = null
   private var bitmap: Bitmap? = null
   private var pictureFlag = 0
   private var type = BREAKFAST
   private var count = 1

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment4(parentFragmentManager, FoodDetailFragment(), bundle)
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

      getDiets = arguments?.getParcelable(DIETS)!!
      type = arguments?.getString("type").toString()
      bundle.putString("type", type)

      count = getDiets.quantity
      binding.tvName.text = getDiets.name

      lifecycleScope.launch {
         getFiles = powerSync.getFiles(getDiets.id) as ArrayList<FileItem>
         for(i in 0 until getFiles.size) imageList.add(getFiles[i])
      }

      binding.clBack.setOnClickListener {
         replaceFragment4(parentFragmentManager, FoodDetailFragment(), bundle)
      }

      binding.clPhoto.setOnClickListener {
         if(checkCameraPermission(requireActivity())) {
            dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)
            val bottomSheetView = layoutInflater.inflate(R.layout.dialog_get_photo, null)
            val clCamera = bottomSheetView.findViewById<ConstraintLayout>(R.id.clCamera)
            val clGallery = bottomSheetView.findViewById<ConstraintLayout>(R.id.clGallery)

            clCamera.setOnClickListener {
               val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // 카메라앱 호출을위한 Intent생성

               val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)	// 이미지 경로 지정
               val file = File.createTempFile(
                  SimpleDateFormat("yyMMddhhmmSSS").format(Date()),
                  ".jpg",
                  storageDir
               ).apply {
                  fileAbsolutePath = absolutePath // 절대경로 변수에 저장
               }

               // 이미지가 저장될 파일의 Uri(콘텐츠 경로 - DB경로)
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

      val contract = ActivityResultContracts.StartActivityForResult()
      cLauncher = registerForActivityResult(contract){
         if(pictureFlag == 1) { // 카메라
            val file = File(fileAbsolutePath) // 임시 파일 저장 경로
            val decode = ImageDecoder.createSource(requireActivity().contentResolver, Uri.fromFile(file.absoluteFile)) // 사진을 디코딩
            bitmap = ImageDecoder.decodeBitmap(decode) // 디코딩한 사진을 비트맵으로 변환
            imageList.add(FileItem(bitmap = bitmap, createdAt = selectedDate.toString()))
            photoView()
            file.delete() // 임시 파일 저장 경로에서 파일 삭제
         }else if(pictureFlag == 2) { // 갤러리
            val uri = it.data?.data // 선택한 이미지의 주소
            if(uri != null) { // 이미지파일 읽어와서 설정하기
               bitmap = getRotatedBitmap(requireActivity(), uri) // 이미지 회전하기
               imageList.add(FileItem(bitmap = bitmap, createdAt = selectedDate.toString()))
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
         lifecycleScope.launch {
            val deleteList = ArrayList<String>()
            val addList = ArrayList<FileItem>()

            for(i in getFiles.indices) {
               var check = false
               for(j in 0 until imageList.size) {
                  if(getFiles[i].name == imageList[j].name) check = true
                  break
               }

               if(!check) {
                  File(requireActivity().filesDir, getFiles[i].name).delete()
                  deleteList.add(getFiles[i].id)
               }
            }

            for(i in 0 until imageList.size) {
               if(imageList[i].bitmap != null) {
                  val result = saveImage(requireActivity(), imageList[i].bitmap!!)
                  if(result != "") {
                     val uuid = UuidCreator.getTimeOrderedEpoch()
                     addList.add(FileItem(id = uuid.toString(), name = result, dietId = getDiets.id))
                  }
               }
            }

            /** 파일 삭제 api 아직 안만들어짐. **/
            val prevImageCnt = getDietImages(selectedDate.toString()).size
            val imageCnt = prevImageCnt - deleteList.size + addList.size
            if(imageCnt > 20) {
               Toast.makeText(context, "사진 등록은 하루 최대 20장까지 할 수 있어요.", Toast.LENGTH_SHORT).show()
            }else {
               if(getDiets.quantity != count) powerSync.updateDiet(Food(id = getDiets.id, quantity = count)) // 식단 정보 업데이트
               for(i in 0 until deleteList.size) powerSync.deleteItem(FILES, "id", deleteList[i]) // 사진 삭제
               for(i in 0 until addList.size) powerSync.insertDietFile(addList[i]) // 사진 저장

               Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
               replaceFragment4(parentFragmentManager, FoodDetailFragment(), bundle)
            }
         }
      }

      photoView()
      settingData()

      return binding.root
   }

   private fun settingData() {
      binding.tvCount.text = count.toString()
      binding.tvAmount.text = (getDiets.volume * count).toString()
      binding.tvKcal.text = (getDiets.calorie * count).toString()
      binding.tvCar.text = String.format("%.1f", (getDiets.carbohydrate * count))
      binding.tvProtein.text = String.format("%.1f", (getDiets.protein * count))
      binding.tvFat.text = String.format("%.1f", (getDiets.fat * count))
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
                     lifecycleScope.launch {
                        powerSync.deleteItem(FILES, "id", imageList[pos].id)
                     }

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