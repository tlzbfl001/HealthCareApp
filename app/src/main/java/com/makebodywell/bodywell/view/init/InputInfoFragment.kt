package com.makebodywell.bodywell.view.init

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.UpdateUserProfileMutation
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_USER
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentInputInfoBinding
import com.makebodywell.bodywell.type.UpdateUserProfileInput
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceInputFragment
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceInputFragment2
import com.makebodywell.bodywell.util.MyApp
import com.makebodywell.bodywell.util.PermissionUtil.Companion.CAMERA_PERMISSION_1
import com.makebodywell.bodywell.util.PermissionUtil.Companion.CAMERA_PERMISSION_2
import com.makebodywell.bodywell.util.PermissionUtil.Companion.CAMERA_PERMISSION_3
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar

class InputInfoFragment : Fragment() {
   private var _binding: FragmentInputInfoBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null
   private var dialog: Dialog? = null
   private var uri:Uri? = null

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentInputInfoBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      val apolloClient = ApolloClient.Builder().serverUrl("https://api.bodywell.dev/graphql").build()

      val getUser = dataManager!!.getUser(MyApp.prefs.userId())
      Log.d(TAG, "InputInfoFragment user: $getUser")

      binding.ivProfile.setOnClickListener {
         dialog = Dialog(requireActivity())
         dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
         dialog!!.setContentView(R.layout.dialog_gallery)

         val cl1 = dialog!!.findViewById<ConstraintLayout>(R.id.cl1)
         val cl2 = dialog!!.findViewById<ConstraintLayout>(R.id.cl2)

         cl1.setOnClickListener {
            if (requestPermission()) {
               val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
               startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }
         }

         cl2.setOnClickListener {
            if (requestPermission()) {
               val intent = Intent(Intent.ACTION_PICK)
               intent.type = MediaStore.Images.Media.CONTENT_TYPE
               startActivityForResult(intent, STORAGE_REQUEST_CODE)
            }
         }

         dialog!!.show()
         dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
         dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         dialog!!.window!!.setGravity(Gravity.BOTTOM)
      }

      binding.tvBirthday.setOnClickListener {
         val datePickerCalendar = Calendar.getInstance()
         val year = datePickerCalendar.get(Calendar.YEAR)
         val month = datePickerCalendar.get(Calendar.MONTH)
         val day = datePickerCalendar.get(Calendar.DAY_OF_MONTH)

         val dpd = DatePickerDialog(requireContext(), R.style.MySpinnerDatePickerStyle,{ _, year, monthOfYear, dayOfMonth ->
               val month = monthOfYear + 1
               binding.tvBirthday.text = "$year-${String.format("%02d", month)}-${String.format("%02d", dayOfMonth)}"
            }, year, month, day
         )

         // 최대 날짜를 현재 시각으로
         dpd.datePicker.maxDate = System.currentTimeMillis() - 1000
         dpd.show()
      }

      binding.cvContinue.setOnClickListener {
         var name = if(getUser.name != "" && getUser.name != null) {
            getUser.name
         }else {
            "바디웰"
         }

         var birthday = if(getUser.birthday != "" && getUser.birthday != null) {
            getUser.birthday
         }else {
            "1990-01-01"
         }

         val profileImage = if(uri != null) {
            uri.toString()
         }else {
            ""
         }

         if(binding.etName.text.toString() != "") {
            name = binding.etName.text.toString()
         }

         if(binding.tvBirthday.text.toString() != "") {
            birthday = binding.tvBirthday.text.toString()
         }

         val getToken = dataManager!!.getToken(getUser.id)

         lifecycleScope.launch{
            val response = apolloClient.mutation(UpdateUserProfileMutation(
               userId = getUser.userId.toString(), UpdateUserProfileInput(birth = Optional.present(birthday), name = Optional.present(name))
            )).addHttpHeader(
               "Authorization",
               "Bearer ${getToken.accessToken}"
            ).execute()

            Log.d(TAG, "inputInfo updateUserProfile: ${response.data!!.updateUserProfile}")
            Log.d(TAG, "inputInfo uri: $uri")

            dataManager?.updateString(TABLE_USER, "name", name!!, getUser.id)
            dataManager?.updateString(TABLE_USER, "birthday", birthday!!, getUser.id)
            dataManager?.updateString(TABLE_USER, "profileImage", profileImage, getUser.id)

            replaceInputFragment(requireActivity(), InputBodyFragment())
         }
      }

      return binding.root
   }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)

      if(resultCode == Activity.RESULT_OK){
         when(requestCode){
            CAMERA_REQUEST_CODE -> {
               if(data?.extras?.get("data") != null){
                  val img = data.extras?.get("data") as Bitmap
                  uri = saveFile(randomFileName(), "image/jpeg", img)
                  binding.ivProfile.setImageURI(Uri.parse(uri.toString()))
               }else {
               }
            }
            STORAGE_REQUEST_CODE -> {
               uri = data?.data
               binding.ivProfile.setImageURI(Uri.parse(uri.toString()))
            }
         }
      }
   }

   private fun randomFileName(): String {
      return SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis())
   }

   private fun saveFile(fileName:String, mimeType:String, bitmap: Bitmap): Uri?{
      // MediaStore 에 파일명, mimeType 을 지정
      val cv = ContentValues()
      cv.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
      cv.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
      cv.put(MediaStore.Images.Media.IS_PENDING, 1)

      // MediaStore 에 파일을 저장
      val uri = requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
      try {
         if(uri != null){
            val descriptor = requireActivity().contentResolver.openFileDescriptor(uri, "w")
            val fos = FileOutputStream(descriptor?.fileDescriptor)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)

            fos.close()
            cv.clear()

            cv.put(MediaStore.Images.Media.IS_PENDING, 0)
            requireActivity().contentResolver.update(uri, cv, null, null)
         }
      } catch(e: FileNotFoundException) {
         e.printStackTrace()
      } catch (e: IOException) {
         e.printStackTrace()
      } catch (e: Exception) {
         e.printStackTrace()
      }

      return uri
   }

   private fun requestPermission(): Boolean {
      var check = true

      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
         for(permission in CAMERA_PERMISSION_3) {
            if (ContextCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(requireActivity(), arrayOf(*CAMERA_PERMISSION_3), REQUEST_CODE)
               check = false
            }
         }
      }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
         for(permission in CAMERA_PERMISSION_2) {
            if (ContextCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(requireActivity(), arrayOf(*CAMERA_PERMISSION_2), REQUEST_CODE)
               check = false
            }
         }
      }else {
         for(permission in CAMERA_PERMISSION_1) {
            if (ContextCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(requireActivity(), arrayOf(*CAMERA_PERMISSION_1), REQUEST_CODE)
               check = false
            }
         }
      }

      return check
   }

   companion object {
      private const val REQUEST_CODE = 1
      private const val CAMERA_REQUEST_CODE = 2
      private const val STORAGE_REQUEST_CODE = 3
   }
}