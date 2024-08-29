package kr.bodywell.android.view.home.food

import android.annotation.SuppressLint
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
import kr.bodywell.android.adapter.PhotoSlideAdapter2
import kr.bodywell.android.adapter.PhotoViewAdapter
import kr.bodywell.android.database.DBHelper.Companion.DAILY_FOOD
import kr.bodywell.android.database.DBHelper.Companion.IMAGE
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentFoodSnackBinding
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.Image
import kr.bodywell.android.model.Unused
import kr.bodywell.android.util.CalendarUtil.selectedDate
import java.util.stream.Collectors
import kotlin.math.abs

class FoodSnackFragment : Fragment() {
    private var _binding: FragmentFoodSnackBinding? = null
    val binding get() = _binding!!

    private lateinit var dataManager: DataManager
    private var photoAdapter: PhotoSlideAdapter2? = null
    private var imageList = ArrayList<Image>()
    private var type = Constant.SNACK.name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodSnackBinding.inflate(layoutInflater)

        dataManager = DataManager(activity)
        dataManager.open()

        imageView()
        listView() // 섭취 식단

        return binding.root
    }

    private fun imageView() {
        binding.viewPager.adapter = null

        val getImage = dataManager.getImage(type, selectedDate.toString())

        for(i in 0 until getImage.size) imageList.add(Image(id = getImage[i].id, imageUri = getImage[i].imageUri))

        if(imageList.size > 0) {
            val adapter = PhotoSlideAdapter2(requireActivity(), imageList)
            binding.viewPager.adapter = adapter
            binding.viewPager.setPadding(0, 0, 0, 0)

            binding.clLeft.setOnClickListener {
                val current = binding.viewPager.currentItem
                if(current == 0) binding.viewPager.setCurrentItem(0, true) else binding.viewPager.setCurrentItem(current-1, true)
            }

            binding.clRight.setOnClickListener {
                val current = binding.viewPager.currentItem
                binding.viewPager.setCurrentItem(current+1, true)
            }
        }
    }

    private fun listView() {
        val dataList = dataManager.getDailyFood(type, selectedDate.toString())

        if(dataList.size != 0) {
            // 섭취한 식단 설정
            val intakeAdapter = FoodIntakeAdapter(requireActivity(), dataList, type)
            binding.rv.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

            intakeAdapter.setOnItemClickListener(object : FoodIntakeAdapter.OnItemClickListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onItemClick(pos: Int) {
                    val dialog = AlertDialog.Builder(context, R.style.AlertDialogStyle)
                        .setTitle("음식 삭제")
                        .setMessage("정말 삭제하시겠습니까?")
                        .setPositiveButton("확인") { _, _ ->
                            dataManager.deleteItem(DAILY_FOOD, "id", dataList[pos].id)
                            dataManager.deleteItem(IMAGE, "dataId", dataList[pos].id)

                            if (imageList.size > 0) {
                                imageList.stream().filter { x -> x.dataId == dataList[pos].id
                                }.collect(Collectors.toList()).forEach { x ->
                                    imageList.remove(x)
                                }
                                photoAdapter!!.notifyDataSetChanged()
                            }

                            if(dataList[pos].uid != "") dataManager.insertUnused(Unused(type = DAILY_FOOD, value = dataList[pos].uid, createdAt = selectedDate.toString()))
                            dataList.removeAt(pos)
                            intakeAdapter.notifyDataSetChanged()

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