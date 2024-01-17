package com.makebodywell.bodywell.view.home.food

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentFoodEditBinding
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.model.Image
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.PermissionUtil
import java.time.LocalDate

class FoodEditFragment : Fragment() {
   private var _binding: FragmentFoodEditBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null
   private val getFood = Food()

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodEditBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      val getFood = dataManager!!.getFood(arguments?.getString("id")!!.toInt())

      binding.tvName.text = getFood.name
      binding.tvCount.text = getFood.unit.toString()
      binding.tvAmount.text = getFood.amount.toString()
      binding.tvKcal.text = getFood.kcal.toString()
      binding.tvCar.text = getFood.carbohydrate.toString()
      binding.tvProtein.text = getFood.protein.toString()
      binding.tvFat.text = getFood.fat.toString()
      binding.tvSalt.text = getFood.salt.toString()
      binding.tvSugar.text = getFood.sugar.toString()

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), FoodRecord1Fragment())
      }

      binding.ivPhoto.setOnClickListener {
         if (requestPermission()) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(intent, STORAGE_REQUEST_CODE)
         }
      }

      return binding.root
   }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      if(resultCode == Activity.RESULT_OK){
         when(requestCode){
            STORAGE_REQUEST_CODE -> {
               val uri = data?.data
               val image = Image(imageUri = uri.toString(), type = getFood.type.toString(), regDate = LocalDate.now().toString())
               dataManager?.insertImage(image)
            }
         }
      }
   }

   private fun requestPermission(): Boolean {
      var check = true
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
         for(permission in PermissionUtil.CAMERA_PERMISSION_3) {
            if (ContextCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(requireActivity(), arrayOf(*PermissionUtil.CAMERA_PERMISSION_3), REQUEST_CODE)
               check = false
            }
         }
      }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
         for(permission in PermissionUtil.CAMERA_PERMISSION_2) {
            if (ContextCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(requireActivity(), arrayOf(*PermissionUtil.CAMERA_PERMISSION_2), REQUEST_CODE)
               check = false
            }
         }
      }else {
         for(permission in PermissionUtil.CAMERA_PERMISSION_1) {
            if (ContextCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(requireActivity(), arrayOf(*PermissionUtil.CAMERA_PERMISSION_1), REQUEST_CODE)
               check = false
            }
         }
      }
      return check
   }

   companion object {
      private const val REQUEST_CODE = 1
      private const val STORAGE_REQUEST_CODE = 2
   }
}