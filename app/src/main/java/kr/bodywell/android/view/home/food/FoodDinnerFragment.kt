package kr.bodywell.android.view.home.food

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.bodywell.android.R
import kr.bodywell.android.adapter.FoodIntakeAdapter
import kr.bodywell.android.adapter.PhotoSlideAdapter2
import kr.bodywell.android.databinding.FragmentFoodDinnerBinding
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.Food
import kr.bodywell.android.model.Image
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.powerSync
import java.io.File
import java.util.stream.Collectors

class FoodDinnerFragment : Fragment() {
    private var _binding: FragmentFoodDinnerBinding? = null
    val binding get() = _binding!!

    private var photoAdapter: PhotoSlideAdapter2? = null
    private var intakeAdapter: FoodIntakeAdapter? = null
    private var imageList = ArrayList<Image>()
    private var type = Constant.DINNER.name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodDinnerBinding.inflate(layoutInflater)

        imageView() // 식단 이미지 뷰
        listView() // 섭취한 식단 설정

        return binding.root
    }

    private fun imageView() {
        /*binding.viewPager.adapter = null

        val getImage = dataManager.getImage(type, selectedDate.toString())

        for(i in 0 until getImage.size) imageList.add(Image(id = getImage[i].id, dataName = getImage[i].dataName, imageName = getImage[i].imageName))

        if(imageList.size > 0) {
            photoAdapter = PhotoSlideAdapter2(requireActivity(), imageList)
            binding.viewPager.adapter = photoAdapter
            binding.viewPager.setPadding(0, 0, 0, 0)

            binding.clLeft.setOnClickListener {
                val current = binding.viewPager.currentItem
                if(current == 0) binding.viewPager.setCurrentItem(0, true) else binding.viewPager.setCurrentItem(current-1, true)
            }

            binding.clRight.setOnClickListener {
                val current = binding.viewPager.currentItem
                binding.viewPager.setCurrentItem(current+1, true)
            }
        }*/
    }

    private fun listView() {
        lifecycleScope.launch {
            val itemList = powerSync.getAllDiet(type, selectedDate.toString()) as ArrayList<Food>
            for(i in 0 until itemList.size) powerSync.deleteDuplicates("diets", "name", itemList[i].name, itemList[i].id)

            if(itemList.isNotEmpty()) {
                binding.rv.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
                intakeAdapter = FoodIntakeAdapter(requireActivity(), itemList, type)

                intakeAdapter!!.setOnItemClickListener(object : FoodIntakeAdapter.OnItemClickListener {
                    override fun onItemClick(pos: Int) {
                        val dialog = AlertDialog.Builder(context, R.style.AlertDialogStyle)
                            .setTitle("음식 삭제")
                            .setMessage("정말 삭제하시겠습니까?")
                            .setPositiveButton("확인") { _, _ ->
                                if(imageList.size > 0) {
                                    imageList.stream().filter { x -> x.dataName == itemList[pos].name }
                                        .collect(Collectors.toList()).forEach { x ->
                                            imageList.remove(x)
                                            File(requireActivity().filesDir, x.imageName).delete()
                                        }
                                }

                                runBlocking {
                                    powerSync.deleteItem("diets", "id", itemList[pos].id)
                                }

                                itemList.removeAt(pos)
                                binding.viewPager.adapter = photoAdapter
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
}