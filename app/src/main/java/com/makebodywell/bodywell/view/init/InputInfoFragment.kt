package com.makebodywell.bodywell.view.init

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_USER
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentInputInfoBinding
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.hideKeyboard
import com.makebodywell.bodywell.util.PermissionUtil.Companion.CAMERA_REQUEST_CODE
import com.makebodywell.bodywell.util.PermissionUtil.Companion.STORAGE_REQUEST_CODE
import com.makebodywell.bodywell.util.PermissionUtil.Companion.cameraRequest
import com.makebodywell.bodywell.util.PermissionUtil.Companion.getImageUriWithAuthority
import com.makebodywell.bodywell.util.PermissionUtil.Companion.randomFileName
import com.makebodywell.bodywell.util.PermissionUtil.Companion.saveFile
import java.util.Calendar

class InputInfoFragment : Fragment() {
   private var _binding: FragmentInputInfoBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null
   private var dialog: Dialog? = null
   private var image: String? = ""

   @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentInputInfoBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      val getUser = dataManager!!.getUser()

      binding.mainLayout.setOnTouchListener { view, motionEvent ->
         hideKeyboard(requireActivity())
         true
      }

      binding.ivProfile.setOnClickListener {
         if(cameraRequest(requireActivity())) {
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

      binding.tvBirthday.setOnClickListener {
         val datePickerCalendar = Calendar.getInstance()
         val calendarYear = datePickerCalendar.get(Calendar.YEAR)
         val calendarMonth = datePickerCalendar.get(Calendar.MONTH)
         val calendarDay = datePickerCalendar.get(Calendar.DAY_OF_MONTH)

         val dpd = DatePickerDialog(requireContext(), R.style.MySpinnerDatePickerStyle,{ _, year, monthOfYear, dayOfMonth ->
               binding.tvBirthday.text = "$year-${String.format("%02d", monthOfYear + 1)}-${String.format("%02d", dayOfMonth)}"
            }, calendarYear, calendarMonth, calendarDay
         )

         // 최대 날짜를 현재 시각으로
         dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
         dpd.show()
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

         dataManager?.updateUserStr(TABLE_USER, "name", name!!)
         dataManager?.updateUserStr(TABLE_USER, "birthday", birthday!!)
         dataManager?.updateUserStr(TABLE_USER, "profileImage", profileImage!!)

         requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.inputFrame, InputBodyFragment())
            commit()
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
                  val uri = saveFile(requireActivity(), randomFileName(), "image/jpeg", img)
                  image = uri.toString()

                  binding.ivProfile.setImageURI(Uri.parse(uri.toString()))

                  dialog!!.dismiss()
               }
            }
            STORAGE_REQUEST_CODE -> {
               val uri = data!!.data
               image = if(data.data!!.toString().contains("com.google.android.apps.photos.contentprovider")) {
                  getImageUriWithAuthority(requireActivity(), uri)
               }else {
                  uri.toString()
               }

               binding.ivProfile.setImageURI(Uri.parse(uri.toString()))

               dialog!!.dismiss()
            }
         }
      }
   }
}