package kr.bodywell.android.view.init

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import kr.bodywell.android.R
import kr.bodywell.android.database.DBHelper
import kr.bodywell.android.database.DBHelper.Companion.IMAGE
import kr.bodywell.android.database.DBHelper.Companion.USER
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentInputInfoBinding
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.deleteFile
import kr.bodywell.android.util.CustomUtil.filterText
import kr.bodywell.android.util.CustomUtil.hideKeyboard
import kr.bodywell.android.util.CustomUtil.saveImage
import kr.bodywell.android.util.PermissionUtil
import kr.bodywell.android.util.PermissionUtil.CAMERA_REQUEST_CODE
import kr.bodywell.android.util.PermissionUtil.STORAGE_REQUEST_CODE
import kr.bodywell.android.util.PermissionUtil.saveFile
import java.time.LocalDate
import kotlin.system.exitProcess

class InputInfoFragment : Fragment() {
   private var _binding: FragmentInputInfoBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
   private var dialog: Dialog? = null
   private var pressedTime: Long = 0
   private var bitmap: Bitmap? = null

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            pressedTime = if(pressedTime == 0L) {
               Toast.makeText(requireActivity(), "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
               System.currentTimeMillis()
            }else {
               val seconds = (System.currentTimeMillis() - pressedTime).toInt()
               if(seconds > 2000) {
                  Toast.makeText(requireActivity(), "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                  0
               }else {
                  requireActivity().finishAffinity()
                  System.runFinalization()
                  exitProcess(0)
               }
            }
         }
      }

      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentInputInfoBinding.inflate(layoutInflater)

      pLauncher = registerForActivityResult(
         ActivityResultContracts.RequestMultiplePermissions()
      ){

      }

      dataManager = DataManager(activity)
      dataManager.open()

      binding.mainLayout.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

      binding.ivProfile.setOnClickListener {
         if(PermissionUtil.checkCameraPermission(requireActivity())) {
            dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)
            val bottomSheetView = layoutInflater.inflate(R.layout.dialog_camera, null)

            val clCamera = bottomSheetView.findViewById<ConstraintLayout>(R.id.clCamera)
            val clPhoto = bottomSheetView.findViewById<ConstraintLayout>(R.id.clPhoto)

            clCamera.setOnClickListener {
               val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
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
               pLauncher.launch(PermissionUtil.CAMERA_PERMISSION_2)
            }else {
               pLauncher.launch(PermissionUtil.CAMERA_PERMISSION_1)
            }
         }
      }

      binding.tvBirthday.setOnClickListener {
         val dialog = Dialog(requireActivity())
         dialog.setContentView(R.layout.dialog_date_picker)
         dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

         val datePicker = dialog.findViewById<DatePicker>(R.id.datePicker)
         val tvConfirm = dialog.findViewById<TextView>(R.id.tvConfirm)
         val tvCancel = dialog.findViewById<TextView>(R.id.tvCancel)

         tvConfirm.setOnClickListener {
            binding.tvBirthday.text = "${datePicker.year}-${String.format("%02d", datePicker.month + 1)}-${String.format("%02d", datePicker.dayOfMonth)}"
            dialog.dismiss()
         }

         tvCancel.setOnClickListener {
            dialog.dismiss()
         }

         datePicker.maxDate = System.currentTimeMillis() - 1000

         dialog.show()
      }

      binding.cvContinue.setOnClickListener {
         val name = if(binding.etName.text.toString() == "") "바디웰" else binding.etName.text.toString()
         val birthday = if(binding.tvBirthday.text.toString() == "") LocalDate.now().toString() else binding.tvBirthday.text.toString()

         if(binding.etName.text.length in 1..1) {
            Toast.makeText(context, "이름은 최소 2자 ~ 최대 15자 이내로 입력하여야합니다.", Toast.LENGTH_SHORT).show()
         }else if(binding.etName.text.length in 1..1 && !filterText(binding.etName.text.toString())) {
            Toast.makeText(context, "특수문자는 입력 불가합니다.", Toast.LENGTH_SHORT).show()
         }else {
            dataManager.updateUserStr(USER, "name", name, "id")
            dataManager.updateUserStr(USER, "birthday", birthday, "id")

            if(bitmap != null) {
               val data = dataManager.getUser().profileImage
               deleteFile(requireActivity(), data!!)
               val result = saveImage(requireActivity(), bitmap!!) // 선택한 이미지를 저장하는 메서드 호출
               if(result != "") dataManager.updateUserStr(USER, "profileImage", result, "id")
            }

            requireActivity().supportFragmentManager.beginTransaction().apply {
               replace(R.id.inputFrame, InputBodyFragment())
               commit()
            }
         }
      }

      return binding.root
   }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      if(resultCode == Activity.RESULT_OK){
         when(requestCode){
            CAMERA_REQUEST_CODE -> {
               if(data!!.extras?.get("data") != null){
                  bitmap = data.extras?.get("data") as Bitmap
                  binding.ivProfile.setImageBitmap(bitmap)
               }
               dialog!!.dismiss()
            }
            STORAGE_REQUEST_CODE -> {
               val fileUri = data!!.data
               try{
                  bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, fileUri)
                  binding.ivProfile.setImageBitmap(bitmap)
               }catch (e: Exception) {
                  e.printStackTrace()
               }
               dialog!!.dismiss()
            }
         }
      }
   }
}