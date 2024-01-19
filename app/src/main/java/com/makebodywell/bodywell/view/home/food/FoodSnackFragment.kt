package com.makebodywell.bodywell.view.home.food

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.net.Uri
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import com.makebodywell.bodywell.adapter.FoodIntakeAdapter
import com.makebodywell.bodywell.adapter.PhotoViewAdapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentFoodSnackBinding
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import kotlin.math.abs

class FoodSnackFragment : Fragment() {
    private var _binding: FragmentFoodSnackBinding? = null
    val binding get() = _binding!!

    private var bundle = Bundle()

    private var calendarDate = ""
    private var type = 4

    private var dataManager: DataManager? = null
    private var photoAdapter: PhotoViewAdapter? = null
    private var foodRecordAdapter: FoodIntakeAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodSnackBinding.inflate(layoutInflater)

        dataManager = DataManager(activity)
        dataManager!!.open()

        calendarDate = arguments?.getString("calendarDate").toString()
        bundle.putString("calendarDate", calendarDate)
        bundle.putString("type", "$type")

        binding.clBack.setOnClickListener {
            replaceFragment1(requireActivity(), FoodFragment())
        }

        binding.tvInput.setOnClickListener {
            replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
        }

        binding.tvBreakfast.setOnClickListener {
            replaceFragment2(requireActivity(), FoodBreakfastFragment(), bundle)
        }

        binding.tvLunch.setOnClickListener {
            replaceFragment2(requireActivity(), FoodLunchFragment(), bundle)
        }

        binding.tvDinner.setOnClickListener {
            replaceFragment2(requireActivity(), FoodDinnerFragment(), bundle)
        }

        binding.cvAdd.setOnClickListener {
            val getFoodData = foodRecordAdapter!!.getFoodData()
            dataManager!!.updateFood(Food(id = getFoodData.id, count = getFoodData.count))

            Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            replaceFragment1(requireActivity(), FoodFragment())
        }

        photoView()
        listView()

        return binding.root
    }

    private fun photoView() {
        val imageList: ArrayList<Uri> = ArrayList()

        val getFoodImage = dataManager!!.getImage(type, calendarDate)
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
            val defaultTranslationFactor = 1.17f
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
                binding.viewPager.setCurrentItem(binding.viewPager.currentItem - 1, true)
            }

            binding.cvRight.setOnClickListener {
                binding.viewPager.setCurrentItem(binding.viewPager.currentItem + 1, true)
            }
        }
    }

    private fun listView() {
        val itemList = ArrayList<Food>()
        val dataList = dataManager!!.getFood(type, calendarDate)

        if(dataList.size != 0) {
            for (i in 0 until dataList.size) {
                itemList.add(Food(id = dataList[i].id, name = dataList[i].name, unit = dataList[i].unit, amount = dataList[i].amount, count = dataList[i].count,
                    kcal = dataList[i].kcal, carbohydrate = dataList[i].carbohydrate, protein = dataList[i].protein, fat = dataList[i].fat))
            }

            // 섭취한 식단 설정
            foodRecordAdapter = FoodIntakeAdapter(requireActivity(), itemList)
            binding.rv.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            binding.rv.adapter = foodRecordAdapter
        }
    }
}