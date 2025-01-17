package kr.bodywell.android.view.home.food

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentFoodDailyEditBinding
import kr.bodywell.android.model.Constant.BREAKFAST
import kr.bodywell.android.model.Constant.DIETS
import kr.bodywell.android.model.Food
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment4
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.util.PermissionUtil.CAMERA_PERMISSION_1
import kr.bodywell.android.util.PermissionUtil.CAMERA_PERMISSION_2
import kr.bodywell.android.util.PermissionUtil.checkCameraPermission

class FoodDailyEditFragment : Fragment() {
   private var _binding: FragmentFoodDailyEditBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
   private lateinit var dataManager: DataManager
   private var bundle = Bundle()
   private var getDiets = Food()
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
      bundle.putParcelable(DIETS, getDiets)
      bundle.putString("type", type)

      count = getDiets.quantity
      binding.tvName.text = getDiets.name

      binding.clBack.setOnClickListener {
         replaceFragment4(parentFragmentManager, FoodDetailFragment(), bundle)
      }

      binding.tvUpload.setOnClickListener {
         if(checkCameraPermission(requireActivity())) {
            replaceFragment4(parentFragmentManager, GalleryFragment(), bundle)
         }else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
               pLauncher.launch(CAMERA_PERMISSION_2)
            }else {
               pLauncher.launch(CAMERA_PERMISSION_1)
            }
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
            if(getDiets.quantity != count) powerSync.updateDiet(Food(id = getDiets.id, quantity = count)) // 식단 정보 업데이트
            Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
            replaceFragment4(parentFragmentManager, FoodDetailFragment(), bundle)
         }
      }

      setDailyView()

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
}