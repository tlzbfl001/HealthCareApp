package com.makebodywell.bodywell.view.home.food

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import com.makebodywell.bodywell.adapter.FoodIntakeAdapter
import com.makebodywell.bodywell.adapter.FoodRecord1Adapter
import com.makebodywell.bodywell.adapter.PhotoViewAdapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentFoodLunchBinding
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.util.PermissionUtil
import kotlin.math.abs

class FoodLunchFragment : Fragment() {
    private var _binding: FragmentFoodLunchBinding? = null
    val binding get() = _binding!!

    private var bundle = Bundle()

    private var calendarDate = ""

    private var dataManager: DataManager? = null
    private var photoAdapter: PhotoViewAdapter? = null
    private var foodRecordAdapter: FoodIntakeAdapter? = null
    private var foodFrequentlyAdapter: FoodRecord1Adapter? = null
    private var dataList = ArrayList<Food>()
    private var itemList = ArrayList<Food>()

    private val permissionRequestCode = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodLunchBinding.inflate(layoutInflater)

        dataManager = DataManager(activity)
        dataManager!!.open()

        initView()
        setupPhotoView()
        setupList()

        return binding.root
    }

    private fun initView() {
        calendarDate = arguments?.getString("calendarDate").toString()
        bundle.putString("calendarDate", calendarDate)
        bundle.putString("type", "2")

        binding.clBack.setOnClickListener {
            replaceFragment1(requireActivity(), FoodFragment())
        }

        binding.cvBreakfast.setOnClickListener {
            replaceFragment2(requireActivity(), FoodBreakfastFragment(), bundle)
        }

        binding.cvDinner.setOnClickListener {
            replaceFragment2(requireActivity(), FoodDinnerFragment(), bundle)
        }

        binding.cvSnack.setOnClickListener {
            replaceFragment2(requireActivity(), FoodSnackFragment(), bundle)
        }

        binding.clGallery.setOnClickListener {
            val result = requestPermission()
            if(result) {
                replaceFragment2(requireActivity(), GalleryFragment(), bundle)
            }
        }

        binding.tvRecordNum.setOnClickListener {
            replaceFragment2(requireActivity(), FoodRecordListFragment(), bundle)
        }

        binding.tvSearch.setOnClickListener {
            replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
        }

        binding.tvBtn1.setOnClickListener {
            replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
        }

        binding.tvBtn2.setOnClickListener {
            replaceFragment2(requireActivity(), FoodRecord2Fragment(), bundle)
        }

        binding.tvBtn3.setOnClickListener {
            replaceFragment2(requireActivity(), FoodInputFragment(), bundle)
        }

        binding.tvAdd.setOnClickListener {
            val getFoodData = foodRecordAdapter!!.getFoodData()
            dataManager!!.updateFoodAmount(Food(id = getFoodData.id, amount = getFoodData.amount))

            Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            replaceFragment1(requireActivity(), FoodFragment())
        }
    }

    private fun setupPhotoView() {
        val imageList: ArrayList<Uri> = ArrayList()

        val getFoodImage = dataManager!!.getImage(2, calendarDate)
        for(i in 0 until getFoodImage.size) {
            imageList.add(Uri.parse(getFoodImage[i].imageUri))
        }

        if(getFoodImage.size > 0) {
            photoAdapter = PhotoViewAdapter(getFoodImage)

            binding.viewPager.adapter = photoAdapter
            binding.viewPager.offscreenPageLimit = 5
            binding.viewPager.clipToPadding = false
            binding.viewPager.clipChildren = false
            binding.viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

            val transformer = CompositePageTransformer()
            val defaultTranslationX = 0.50f
            val defaultTranslationFactor = 1.2f
            val scaleFactor = 0.14f
            val defaultScale = 1f

            transformer.addTransformer{ view: View, position: Float ->
                view.apply {
                    ViewCompat.setElevation(view, -abs(position))
                    val scaleFactor1 = scaleFactor * position + defaultScale
                    val scaleFactor2 = -scaleFactor * position + defaultScale
                    when {
                        position < -2 -> {
                            translationX = width * position
                        }
                        position < 0f -> {
                            scaleX = scaleFactor1
                            scaleY = scaleFactor1
                            translationX = -(width / defaultTranslationFactor) * position
                        }
                        position == 0f -> {
                            translationX = defaultTranslationX
                            scaleX = defaultScale
                            scaleY = defaultScale
                        }
                        position > 0 && position <= 2 -> {
                            scaleX = scaleFactor2
                            scaleY = scaleFactor2
                            translationX = -(width / defaultTranslationFactor) * position
                        }
                        position > 2 -> {
                            translationX = 0f
                        }
                    }
                }
            }
            binding.viewPager.setPageTransformer(transformer)

            binding.cvLeft.setOnClickListener {
                var current = binding.viewPager.currentItem
                binding.viewPager.setCurrentItem(current-1, true)
            }
            binding.cvRight.setOnClickListener {
                var current = binding.viewPager.currentItem
                binding.viewPager.setCurrentItem(current+1, true)
            }
        }
    }

    private fun setupList() {
        dataList = dataManager!!.getFood(2, calendarDate)

        if(dataList.size != 0) {
            binding.clList.visibility = View.VISIBLE
            binding.view.visibility = View.VISIBLE

            for (i in 0 until dataList.size) {
                itemList.add(Food(id = dataList[i].id, name = dataList[i].name, unit = dataList[i].unit, amount = dataList[i].amount,
                    kcal = dataList[i].kcal, carbohydrate = dataList[i].carbohydrate, protein = dataList[i].protein, fat = dataList[i].fat))
            }

            foodRecordAdapter = FoodIntakeAdapter(requireActivity(), itemList, 2)
            binding.recyclerView1.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            binding.recyclerView1.adapter = foodRecordAdapter

            foodFrequentlyAdapter = FoodRecord1Adapter(itemList)
            binding.recyclerView2.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            binding.recyclerView2.adapter = foodFrequentlyAdapter
        }
    }

    private fun requestPermission(): Boolean {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            for(permission in PermissionUtil.cameraPermissions3) {
                if (ContextCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(*PermissionUtil.cameraPermissions3), permissionRequestCode)
                    return false
                }
            }
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            for(permission in PermissionUtil.cameraPermissions2) {
                if (ContextCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(*PermissionUtil.cameraPermissions2), permissionRequestCode)
                    return false
                }
            }
        }else {
            for(permission in PermissionUtil.cameraPermissions1) {
                if (ContextCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(*PermissionUtil.cameraPermissions1), permissionRequestCode)
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == permissionRequestCode && grantResults.isNotEmpty()) {
            var result = true
            for (element in grantResults) {
                if (element == -1) {
                    result = false
                }
            }
            if(!result) {
                val alertDialog: androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(requireActivity())
                alertDialog.setTitle("권한 설정")
                alertDialog.setMessage("권한을 허가하지 않으셨습니다.\n[설정]에서 권한을 허가해주세요.")
                alertDialog.setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, _ ->
                    val intent: Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                        Uri.parse("package:" + requireActivity().packageName)
                    )
                    startActivity(intent)
                    dialogInterface.cancel()
                })
                alertDialog.setNegativeButton("취소", DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.cancel()
                })
                alertDialog.show()
            }
        }
    }
}