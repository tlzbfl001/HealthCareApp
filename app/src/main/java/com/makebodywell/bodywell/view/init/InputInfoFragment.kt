package com.makebodywell.bodywell.view.init

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_USER
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentInputInfoBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.filterAlphaNumSpace
import com.makebodywell.bodywell.util.CustomUtil.Companion.hideKeyboard
import com.makebodywell.bodywell.util.PermissionUtil.Companion.CAMERA_REQUEST_CODE
import com.makebodywell.bodywell.util.PermissionUtil.Companion.STORAGE_REQUEST_CODE
import com.makebodywell.bodywell.util.PermissionUtil.Companion.cameraRequest
import com.makebodywell.bodywell.util.PermissionUtil.Companion.saveFile
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

      val getUser = dataManager.getUser()

      binding.mainLayout.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

      binding.ivProfile.setOnClickListener {
         if(cameraRequest(requireActivity())) {
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
      }

      binding.etName.filters = arrayOf(filterAlphaNumSpace, InputFilter.LengthFilter(15))

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
         val name = if(binding.etName.text.toString() != "") {
            binding.etName.text.toString()
         }else if(getUser.name != "") {
            getUser.name
         }else {
            "바디웰"
         }

         val birthday = if(binding.tvBirthday.text.toString() != "") {
            binding.tvBirthday.text.toString()
         }else if(getUser.birthday != "") {
            getUser.birthday
         }else {
            "1990-01-01"
         }

         val profileImage = if(image != "") image else ""

         if(binding.etName.text.length < 2) {
            Toast.makeText(context, "이름은 최소 2자 ~ 최대 15자 이내로 입력하여야합니다.", Toast.LENGTH_SHORT).show()
         }else {
            dataManager.updateUserStr(TABLE_USER, "name", name!!)
            dataManager.updateUserStr(TABLE_USER, "birthday", birthday!!)
            dataManager.updateUserStr(TABLE_USER, "profileImage", profileImage!!)

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