package kr.bodywell.android.view.home.food

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.adapter.PhotoSlideAdapter2
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentFoodDailyEditBinding
import kr.bodywell.android.model.Constant.BREAKFAST
import kr.bodywell.android.model.Constant.DIETS
import kr.bodywell.android.model.Constant.FILES
import kr.bodywell.android.model.FileItem
import kr.bodywell.android.model.Food
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.dateTimeToIso1
import kr.bodywell.android.util.CustomUtil.getDietFiles
import kr.bodywell.android.util.CustomUtil.getRotatedBitmap
import kr.bodywell.android.util.CustomUtil.getUUID
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment4
import kr.bodywell.android.util.CustomUtil.saveFile
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.util.PermissionUtil.CAMERA_PERMISSION_1
import kr.bodywell.android.util.PermissionUtil.CAMERA_PERMISSION_2
import kr.bodywell.android.util.PermissionUtil.checkCameraPermission
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class FoodDailyEditFragment : Fragment() {
   private var _binding: FragmentFoodDailyEditBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
   private lateinit var cLauncher: ActivityResultLauncher<Intent>
   private lateinit var dataManager: DataManager
   private var bundle = Bundle()
   private var dialog: Dialog? = null
   private var fileAbsolutePath: String? = null
   private var pictureFlag = 0
   private var getDiets = Food()
   private var getFiles = ArrayList<FileItem>()
   private var images = ArrayList<FileItem>()
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

      dataManager = DataManager(activity)
      dataManager.open()

      pLauncher = registerForActivityResult(
         ActivityResultContracts.RequestMultiplePermissions()
      ){}

      getDiets = arguments?.getParcelable(DIETS)!!
      type = arguments?.getString("type").toString()
      bundle.putString("type", type)

      count = getDiets.quantity
      binding.tvName.text = getDiets.name

      lifecycleScope.launch {
         getFiles = powerSync.getFiles("diet_id", getDiets.id) as ArrayList<FileItem>
         for(i in 0 until getFiles.size) {
            images.add(getFiles[i])
         }
      }

      binding.clBack.setOnClickListener {
         replaceFragment4(parentFragmentManager, FoodDetailFragment(), bundle)
      }

      binding.clUpload.setOnClickListener {
         if(checkCameraPermission(requireActivity())) {
            dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)
            val bottomSheetView = layoutInflater.inflate(R.layout.dialog_photo, null)
            val btnCamera = bottomSheetView.findViewById<ConstraintLayout>(R.id.btnCamera)
            val btnGallery = bottomSheetView.findViewById<ConstraintLayout>(R.id.btnGallery)

            btnCamera.setOnClickListener {
               val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

               val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)	// 이미지 경로 지정
               val file = File.createTempFile(
                  SimpleDateFormat("yyMMddhhmmSSS").format(Date()),
                  ".jpg",
                  storageDir
               ).apply {
                  fileAbsolutePath = absolutePath // 절대경로 변수에 저장
               }

               // 이미지가 저장될 파일의 Uri
               val contentUri = FileProvider.getUriForFile(requireActivity(), requireActivity().applicationContext.packageName + ".file_provider", file)

               intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
               cLauncher.launch(intent)
               pictureFlag = 1
            }

            btnGallery.setOnClickListener {
               val intent = Intent(Intent.ACTION_PICK)
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
      cLauncher = registerForActivityResult(contract) {
         if(it?.resultCode == Activity.RESULT_OK){
            if(pictureFlag == 1) { // 카메라
               val file1 = File(fileAbsolutePath) // 임시 파일 저장 경로
               val decode = ImageDecoder.createSource(requireActivity().contentResolver, Uri.fromFile(file1.absoluteFile))
               val bitmap = ImageDecoder.decodeBitmap(decode)
               val result = saveFile(requireActivity(), bitmap)
               if(result != "") {
                  val file2 = File(requireActivity().filesDir, result)
                  if(file2.length() in 1..1048575) { // 파일 크기 확인
                     images.add(FileItem(bitmap = bitmap))
                     viewPhotos()
                  }else {
                     Toast.makeText(context, "파일 크기가 허용되는 한도를 초과하여 파일을 저장할 수 없습니다.", Toast.LENGTH_SHORT).show()
                  }
                  file2.delete() // 임시 파일 저장 경로에서 파일 삭제
               }
               file1.delete() // 임시 파일 저장 경로에서 파일 삭제
            }else if(pictureFlag == 2) { // 갤러리
               val uri = it.data?.data
               if(uri != null) {
                  val bitmap = getRotatedBitmap(requireActivity(), uri) // 이미지 회전하기
                  val result = saveFile(requireActivity(), bitmap!!)
                  if(result != "") {
                     val file = File(requireActivity().filesDir, result)
                     if(file.length() in 1..1048575) { // 파일 크기 확인
                        images.add(FileItem(bitmap = bitmap))
                        viewPhotos()
                     }else {
                        Toast.makeText(context, "파일 크기가 허용되는 한도를 초과하여 파일을 저장할 수 없습니다.", Toast.LENGTH_SHORT).show()
                     }
                     file.delete()
                  }
               }
            }

            dialog!!.dismiss()
         }
      }

      binding.ivMinus.setOnClickListener {
         if(count > 1) count--
         setDailyView()
      }

      binding.ivPlus.setOnClickListener {
         if(count < 100) count++
         setDailyView()
      }

      binding.cvEdit.setOnClickListener {
         lifecycleScope.launch {
            val deleteList = ArrayList<String>()
            val addList = ArrayList<FileItem>()

            for(i in getFiles.indices) {
               var check = false
               for(j in 0 until images.size) {
                  if(images[j].name != "") {
                     if(getFiles[i].name == images[j].name) check = true
                     if(j == images.size - 1 && !check) deleteList.add(getFiles[i].name)
                  }
               }
            }

            for(i in 0 until images.size) {
               if(images[i].bitmap != null) {
                  val result = saveFile(requireActivity(), images[i].bitmap!!)
                  if(result != "") {
                     addList.add(FileItem(id = getUUID(), name = result, dietId = getDiets.id))
                  }
               }
            }

            val getFileSize = getDietFiles(selectedDate.toString()).size
            val fileCnt = getFileSize - deleteList.size + addList.size

            if(fileCnt > 20) {
               Toast.makeText(context, "사진은 하루에 20장까지 등록할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }else {
               if(getDiets.quantity != count) powerSync.updateDiet(Food(id = getDiets.id, quantity = count)) // 식단 정보 업데이트
               for(i in 0 until addList.size) powerSync.insertDietFile(addList[i]) // 사진 저장
               dataManager.updateFileTime(dateTimeToIso1(Calendar.getInstance()))

               for(i in 0 until deleteList.size) { // 사진 삭제
                  powerSync.deleteItem(FILES, "name", deleteList[i])
                  File(requireActivity().filesDir, deleteList[i]).delete()
               }

               Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
               replaceFragment4(parentFragmentManager, FoodDetailFragment(), bundle)
            }
         }
      }

      setDailyView()
      viewPhotos()

      return binding.root
   }

   private fun setDailyView() {
      binding.tvCount.text = count.toString()
      binding.tvAmount.text = (getDiets.volume * count).toString()
      binding.tvKcal.text = (getDiets.calorie * count).toString()
      binding.tvCar.text = String.format("%.1f", (getDiets.carbohydrate * count))
      binding.tvProtein.text = String.format("%.1f", (getDiets.protein * count))
      binding.tvFat.text = String.format("%.1f", (getDiets.fat * count))
   }

   private fun viewPhotos() {
      if(checkCameraPermission(requireActivity()) && images.size > 0) {
         binding.ivView.visibility = View.GONE
         binding.viewPager.visibility = View.VISIBLE

         val adapter = PhotoSlideAdapter2(requireActivity(), images)
         binding.viewPager.setPadding(0, 0, 0, 0)

         adapter.setOnLongClickListener(object : PhotoSlideAdapter2.OnLongClickListener {
            override fun onLongClick(pos: Int) {
               val dialog = AlertDialog.Builder(context, R.style.AlertDialogStyle)
                  .setTitle("사진 삭제")
                  .setMessage("정말 삭제하시겠습니까?")
                  .setPositiveButton("확인") { _, _ ->
                     images.removeAt(pos)

                     if(images.size == 0) {
                        binding.ivView.visibility = View.VISIBLE
                        binding.viewPager.visibility = View.GONE
                     }

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