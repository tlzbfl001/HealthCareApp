package com.makebodywell.bodywell.view.home.food

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import com.makebodywell.bodywell.adapter.FoodIntakeAdapter
import com.makebodywell.bodywell.adapter.PhotoViewAdapter
import com.makebodywell.bodywell.database.DBHelper
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DAILY_FOOD
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_IMAGE
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentFoodDinnerBinding
import com.makebodywell.bodywell.model.Image
import com.makebodywell.bodywell.util.CalendarUtil
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.view.home.MainActivity
import java.util.stream.Collectors
import kotlin.math.abs

class FoodDinnerFragment : Fragment(), MainActivity.OnBackPressedListener {
    private var _binding: FragmentFoodDinnerBinding? = null
    val binding get() = _binding!!

    private var dataManager: DataManager? = null
    private var photoAdapter: PhotoViewAdapter? = null
    private var intakeAdapter: FoodIntakeAdapter? = null
    private var imageData: ArrayList<Image>? = null
    private var type = 3

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).setOnBackPressedListener(this)
    }

    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodDinnerBinding.inflate(layoutInflater)

        requireActivity().window?.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
            navigationBarColor = Color.BLACK

            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
            binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
        }

        dataManager = DataManager(activity)
        dataManager!!.open()

        binding.clBack.setOnClickListener {
            replaceFragment1(requireActivity(), FoodFragment())
        }

        binding.cvInput.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("type", "$type")
            replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
        }

        binding.tvBreakfast.setOnClickListener {
            replaceFragment1(requireActivity(), FoodBreakfastFragment())
        }

        binding.tvLunch.setOnClickListener {
            replaceFragment1(requireActivity(), FoodLunchFragment())
        }

        binding.tvSnack.setOnClickListener {
            replaceFragment1(requireActivity(), FoodSnackFragment())
        }

        photoView()
        listView()

        return binding.root
    }

    private fun photoView() {
        imageData = dataManager!!.getImage(type, selectedDate.toString())

        if(imageData!!.size > 0) {
            photoAdapter = PhotoViewAdapter(imageData!!)

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
        val dataList = dataManager!!.getDailyFood(type, selectedDate.toString())

        if(dataList.size != 0) {
            // 섭취한 식단 설정
            intakeAdapter = FoodIntakeAdapter(requireActivity(), dataList, type)
            binding.rv.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

            intakeAdapter!!.setOnItemClickListener(object : FoodIntakeAdapter.OnItemClickListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onItemClick(pos: Int) {
                    val dialog = AlertDialog.Builder(context)
                        .setMessage("정말 삭제하시겠습니까?")
                        .setPositiveButton("확인") { _, _ ->
                            if (imageData!!.size > 0) {
                                imageData!!.stream().filter {
                                    x -> x.dataId == dataList[pos].id
                                }.collect(Collectors.toList()).forEach { x ->
                                    imageData!!.remove(x)
                                }
                            }

                            dataManager!!.deleteItem(TABLE_DAILY_FOOD, "id", dataList[pos].id)
                            dataManager!!.deleteItem(TABLE_IMAGE, "dataId", dataList[pos].id)

                            dataList.removeAt(pos)

                            intakeAdapter!!.notifyDataSetChanged()
                            photoAdapter!!.notifyDataSetChanged()

                            Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("취소", null)
                        .create()
                    dialog.show()
                }
            })

            binding.rv.adapter = intakeAdapter
        }
    }

    override fun onBackPressed() {
        val activity = activity as MainActivity?
        activity!!.setOnBackPressedListener(null)
        replaceFragment1(requireActivity(), FoodFragment())
    }
}