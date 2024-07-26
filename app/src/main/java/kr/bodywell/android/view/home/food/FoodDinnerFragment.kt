package kr.bodywell.android.view.home.food

import android.app.AlertDialog
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
import kr.bodywell.android.R
import kr.bodywell.android.adapter.FoodIntakeAdapter
import kr.bodywell.android.adapter.PhotoViewAdapter
import kr.bodywell.android.database.DBHelper.Companion.DAILY_FOOD
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentFoodDinnerBinding
import kr.bodywell.android.model.Image
import kr.bodywell.android.model.Unused
import kr.bodywell.android.util.CalendarUtil.Companion.selectedDate
import kotlin.math.abs

class FoodDinnerFragment : Fragment() {
    private var _binding: FragmentFoodDinnerBinding? = null
    val binding get() = _binding!!

    private lateinit var dataManager: DataManager
    private var photoAdapter: PhotoViewAdapter? = null
    private var intakeAdapter: FoodIntakeAdapter? = null
    private var imageData = ArrayList<Image>()
    private var type = "DINNER"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodDinnerBinding.inflate(layoutInflater)

        dataManager = DataManager(activity)
        dataManager.open()

//        imageView()
        listView()

        return binding.root
    }

    private fun imageView() {
        imageData = dataManager.getImage(type, selectedDate.toString())

        if(imageData.size > 0) {
            photoAdapter = PhotoViewAdapter(imageData)

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
        val dataList = dataManager.getDailyFood(type, selectedDate.toString())

        if(dataList.size != 0) {
            // 섭취한 식단 설정
            intakeAdapter = FoodIntakeAdapter(requireActivity(), dataList, type)
            binding.rv.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

            intakeAdapter!!.setOnItemClickListener(object : FoodIntakeAdapter.OnItemClickListener {
                override fun onItemClick(pos: Int) {
                    val dialog = AlertDialog.Builder(context, R.style.AlertDialogStyle)
                        .setTitle("음식 삭제")
                        .setMessage("정말 삭제하시겠습니까?")
                        .setPositiveButton("확인") { _, _ ->
                            dataManager.deleteItem(DAILY_FOOD, "id", dataList[pos].id)
                            /*dataManager.deleteItem(TABLE_IMAGE, "dataId", dataList[pos].id)

                            if (imageData.size > 0) {
                                imageData.stream().filter {
                                    x -> x.dataId == dataList[pos].id
                                }.collect(Collectors.toList()).forEach { x ->
                                    imageData.remove(x)
                                }
                                photoAdapter!!.notifyDataSetChanged()
                            }*/

                            if(dataList[pos].uid != "") dataManager.insertUnused(Unused(type = DAILY_FOOD, value = dataList[pos].uid, createdAt = selectedDate.toString()))

                            dataList.removeAt(pos)
                            intakeAdapter!!.notifyDataSetChanged()

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
}