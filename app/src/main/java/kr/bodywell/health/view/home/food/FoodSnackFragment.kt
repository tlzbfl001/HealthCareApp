package kr.bodywell.health.view.home.food

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
import kr.bodywell.health.R
import kr.bodywell.health.adapter.FoodIntakeAdapter
import kr.bodywell.health.adapter.ImageSlideAdapter
import kr.bodywell.health.databinding.FragmentFoodSnackBinding
import kr.bodywell.health.model.Constant
import kr.bodywell.health.model.Constant.DIETS
import kr.bodywell.health.model.Constant.SNACK
import kr.bodywell.health.model.FileItem
import kr.bodywell.health.model.Food
import kr.bodywell.health.util.CalendarUtil.selectedDate
import kr.bodywell.health.util.MyApp.Companion.powerSync
import kr.bodywell.health.util.PermissionUtil
import java.io.File
import java.util.stream.Collectors

class FoodSnackFragment : Fragment() {
    private var _binding: FragmentFoodSnackBinding? = null
    val binding get() = _binding!!

    private var photoAdapter: ImageSlideAdapter? = null
    private var getDiets = ArrayList<Food>()
    private var images = ArrayList<FileItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodSnackBinding.inflate(layoutInflater)

        // 섭취한 식단 설정
        listView()

        // 식단 이미지 뷰
        if(PermissionUtil.checkMediaPermission(requireActivity())) {
            imageView()
        }

        return binding.root
    }

    private fun listView() {
        lifecycleScope.launch {
            getDiets = powerSync.getDiets(SNACK, selectedDate.toString()) as ArrayList<Food>
            for(i in 0 until getDiets.size) powerSync.deleteDiet(SNACK, getDiets[i].name, selectedDate.toString(), getDiets[i].id)
        }

        if(getDiets.isNotEmpty()) {
            val intakeAdapter = FoodIntakeAdapter(parentFragmentManager, getDiets, SNACK)
            binding.rv.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

            intakeAdapter.setOnItemClickListener(object : FoodIntakeAdapter.OnItemClickListener {
                override fun onItemClick(pos: Int) {
                    val dialog = AlertDialog.Builder(context, R.style.AlertDialogStyle)
                        .setTitle("음식 삭제")
                        .setMessage("정말 삭제하시겠습니까?")
                        .setPositiveButton("확인") { _, _ ->
                            if(images.size > 0) {
                                images.stream().filter { x -> x.name == getDiets[pos].name }.collect(Collectors.toList()).forEach { x ->
                                    images.remove(x)
                                    File(requireActivity().filesDir, x.name).delete()
                                }
                            }

                            runBlocking {
                                val getFiles = powerSync.getFiles("diet_id", getDiets[pos].id)
                                for(element in getFiles) powerSync.deleteItem(Constant.FILES, "id", element.id)
                                powerSync.deleteItem(DIETS, "id", getDiets[pos].id)
                            }

                            getDiets.removeAt(pos)
                            imageView()
                            binding.rv.adapter = intakeAdapter

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

    private fun imageView() {
        binding.viewPager.adapter = null
        images.clear()

        lifecycleScope.launch {
            for(i in getDiets.indices) {
                val getFiles = powerSync.getFiles("diet_id", getDiets[i].id)
                for(j in getFiles.indices) {
                    val imgPath = requireActivity().filesDir.toString() + "/" + getFiles[j].name
                    val file = File(imgPath)
                    if(file.exists()) images.add(getFiles[j])
                }
            }

            if(images.size > 0) {
                photoAdapter = ImageSlideAdapter(requireActivity(), images)
                binding.viewPager.adapter = photoAdapter
                binding.viewPager.setPadding(0, 0, 0, 0)

                binding.clLeft.setOnClickListener {
                    val current = binding.viewPager.currentItem
                    if(current == 0) binding.viewPager.setCurrentItem(0, true) else binding.viewPager.setCurrentItem(current-1, true)
                }

                binding.clRight.setOnClickListener {
                    val current = binding.viewPager.currentItem
                    binding.viewPager.setCurrentItem(current + 1, true)
                }
            }
        }
    }
}