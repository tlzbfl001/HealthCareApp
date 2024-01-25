package com.makebodywell.bodywell.view.home.food

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.makebodywell.bodywell.adapter.PhotoSlideAdapter2
import com.makebodywell.bodywell.database.DBHelper
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_FOOD_IMAGE
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentFoodEditBinding
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.model.FoodImage
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.util.PermissionUtil.Companion.CAMERA_PERMISSION_1
import com.makebodywell.bodywell.util.PermissionUtil.Companion.CAMERA_PERMISSION_2
import com.makebodywell.bodywell.util.PermissionUtil.Companion.CAMERA_PERMISSION_3

class FoodEditFragment : Fragment() {
   private var _binding: FragmentFoodEditBinding? = null
   private val binding get() = _binding!!

   private var bundle = Bundle()

   private var dataManager: DataManager? = null
   private var getFood = Food()
   private var imageList = ArrayList<FoodImage>()

   private var calendarDate = ""
   private var id = ""
   private var type = ""

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodEditBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      calendarDate = arguments?.getString("calendarDate").toString()
      type = arguments?.getString("type").toString()
      id = arguments?.getString("id").toString()
      bundle.putString("calendarDate", calendarDate)
      bundle.putString("type", type)

      getFood = dataManager!!.getFood(arguments?.getString("id")!!.toInt())
      var count = getFood.count

      binding.tvName.text = getFood.name
      binding.tvCount.text = count.toString()
      binding.tvAmount.text = getFood.amount
      binding.tvKcal.text = getFood.kcal
      binding.tvCar.text = getFood.carbohydrate
      binding.tvProtein.text = getFood.protein
      binding.tvFat.text = getFood.fat
      binding.tvSalt.text = getFood.salt
      binding.tvSugar.text = getFood.sugar

      binding.clBack.setOnClickListener {
         replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
      }

      binding.clPhoto.setOnClickListener {
         if (requestPermission()) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(intent, STORAGE_REQUEST_CODE)
         }
      }

      binding.ivMinus.setOnClickListener {
         if(count > 0) {
            count--
            binding.tvCount.text = count.toString()
         }
      }

      binding.ivPlus.setOnClickListener {
         count++
         binding.tvCount.text = count.toString()
      }

      binding.cvSave.setOnClickListener {
         dataManager!!.deleteItem(TABLE_FOOD_IMAGE, "dataId", id.toInt())
         for(i in 0 until imageList.size) {
            dataManager?.insertFoodImage(imageList[i])
         }

         Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
         replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
      }

      val getData = dataManager!!.getImage(id.toInt())
      for(i in 0 until getData.size) {
         imageList.add(getData[i])
      }

      setupPhotoView(imageList)

      return binding.root
   }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      if(resultCode == Activity.RESULT_OK){
         when(requestCode){
            STORAGE_REQUEST_CODE -> {
               val uri = data?.data
               val image = FoodImage(imageUri = uri.toString(), type = type.toInt(), dataId = getFood.id, regDate = calendarDate)
               imageList.add(image)
               setupPhotoView(imageList)
            }
         }
      }
   }

   private fun setupPhotoView(imageList: ArrayList<FoodImage>) {
      if(imageList.size > 0) {
         binding.ivView.visibility = View.GONE
         binding.viewPager.visibility = View.VISIBLE

         val adapter = PhotoSlideAdapter2(requireActivity(), imageList)
         binding.viewPager.setPadding(0, 0, 0, 0)

         adapter.setOnLongClickListener(object : PhotoSlideAdapter2.OnLongClickListener {
            override fun onLongClick(pos: Int) {
               val dialog = AlertDialog.Builder(context)
                  .setMessage("삭제하시겠습니까?")
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
      private const val STORAGE_REQUEST_CODE = 2
   }
}