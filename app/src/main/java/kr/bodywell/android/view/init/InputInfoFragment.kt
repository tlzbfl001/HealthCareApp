package kr.bodywell.android.view.init

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import kr.bodywell.android.R
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentInputInfoBinding
import kr.bodywell.android.util.CustomUtil.Companion.filterText
import kr.bodywell.android.util.CustomUtil.Companion.hideKeyboard
import kr.bodywell.android.util.PermissionUtil.Companion.CAMERA_REQUEST_CODE
import kr.bodywell.android.util.PermissionUtil.Companion.STORAGE_REQUEST_CODE
import kr.bodywell.android.util.PermissionUtil.Companion.saveFile
import java.time.LocalDate
import kotlin.system.exitProcess

class InputInfoFragment : Fragment() {
   private var _binding: FragmentInputInfoBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private var dialog: Dialog? = null
   private var pressedTime: Long = 0
   private var image: String? = ""

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

      dataManager = DataManager(activity)
      dataManager.open()

      binding.mainLayout.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

//      binding.ivProfile.setOnClickListener {
//         if(cameraRequest(requireActivity())) {
//            dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)
//            val bottomSheetView = layoutInflater.inflate(R.layout.dialog_camera, null)
//
//            val clCamera = bottomSheetView.findViewById<ConstraintLayout>(R.id.clCamera)
//            val clPhoto = bottomSheetView.findViewById<ConstraintLayout>(R.id.clPhoto)
//
//            clCamera.setOnClickListener {
//               val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//               startActivityForResult(intent, CAMERA_REQUEST_CODE)
//            }
//
//            clPhoto.setOnClickListener {
//               val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//               intent.type = "image/*"
//               startActivityForResult(intent, STORAGE_REQUEST_CODE)
//            }
//
//            dialog!!.setContentView(bottomSheetView)
//            dialog!!.show()
//         }
//      }

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
         val image = if(image == "") "" else image

         if(binding.etName.text.length in 1..1) {
            Toast.makeText(context, "음식이름은 최소 2자 ~ 최대 15자 이내로 입력하여야합니다.", Toast.LENGTH_SHORT).show()
         }else if(binding.etName.text.length in 1..1 && !filterText(binding.etName.text.toString())) {
            Toast.makeText(context, "특수문자는 입력 불가합니다.", Toast.LENGTH_SHORT).show()
         }else {
            dataManager.updateUserStr("name", name)
            dataManager.updateUserStr("birthday", birthday)
            dataManager.updateUserStr("image", image!!)

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
                  val img = data.extras?.get("data") as Bitmap
                  val uri = saveFile(requireActivity(), "image/jpeg", img)

                  image = uri.toString()

                  binding.ivProfile.setImageURI(Uri.parse(image))

                  dialog!!.dismiss()
               }
            }
            STORAGE_REQUEST_CODE -> {
               val uri = data!!.data

               val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
               requireActivity().contentResolver.takePersistableUriPermission(uri!!, takeFlags) // 영구 권한 얻기

               image = uri.toString()

               binding.ivProfile.setImageURI(Uri.parse(image))

               dialog!!.dismiss()
            }
         }
      }
   }
}