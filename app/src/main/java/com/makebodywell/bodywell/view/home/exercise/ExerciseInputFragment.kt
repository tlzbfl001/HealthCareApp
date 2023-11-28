package com.makebodywell.bodywell.view.home.exercise

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.GridLayoutManager
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.ExerciseCategoryAdapter
import com.makebodywell.bodywell.adapter.ExercisePartAdapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentExerciseInputBinding
import com.makebodywell.bodywell.model.Exercise
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import java.time.LocalDate

class ExerciseInputFragment : Fragment() {
   private var _binding: FragmentExerciseInputBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback

   private var dataManager: DataManager? = null

   private var exerciseCategoryAdapter: ExerciseCategoryAdapter? = null
   private var exercisePartAdapter: ExercisePartAdapter? = null

   private val initialCategoryList = arrayListOf("유산소", "무산소", "근력운동", "스포츠", "일상생활", "복합운동")
   private val initialPartList = arrayListOf("전신", "상체", "하체", "팔", "다리", "복근")
   private val categoryList = ArrayList<String>()
   private val partList = ArrayList<String>()
   private val categoryAddList = ArrayList<String>()
   private val partAddList = ArrayList<String>()
   private val categoryDelList = ArrayList<String>()
   private val partDelList = ArrayList<String>()

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentExerciseInputBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      initView()

      // 분류 데이터목록 설정
      setupCategory()

      // 부위 데이터목록 설정
      setupPart()

      return binding.root
   }

   private fun initView() {
      binding.ivBack.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseListFragment())
      }

      binding.tvHard.setOnClickListener {
         binding.tvHard.background = requireActivity().getDrawable(R.drawable.rec_5_purple)
         binding.tvHard.setTextColor(Color.WHITE)
         binding.tvNormal.background = requireActivity().getDrawable(R.drawable.rec_5_border_gray)
         binding.tvNormal.setTextColor(Color.BLACK)
         binding.tvEasy.background = requireActivity().getDrawable(R.drawable.rec_5_border_gray)
         binding.tvEasy.setTextColor(Color.BLACK)
      }

      binding.tvNormal.setOnClickListener {
         binding.tvHard.background = requireActivity().getDrawable(R.drawable.rec_5_border_gray)
         binding.tvHard.setTextColor(Color.BLACK)
         binding.tvNormal.background = requireActivity().getDrawable(R.drawable.rec_5_purple)
         binding.tvNormal.setTextColor(Color.WHITE)
         binding.tvEasy.background = requireActivity().getDrawable(R.drawable.rec_5_border_gray)
         binding.tvEasy.setTextColor(Color.BLACK)
      }

      binding.tvEasy.setOnClickListener {
         binding.tvHard.background = requireActivity().getDrawable(R.drawable.rec_5_border_gray)
         binding.tvHard.setTextColor(Color.BLACK)
         binding.tvNormal.background = requireActivity().getDrawable(R.drawable.rec_5_border_gray)
         binding.tvNormal.setTextColor(Color.BLACK)
         binding.tvEasy.background = requireActivity().getDrawable(R.drawable.rec_5_purple)
         binding.tvEasy.setTextColor(Color.WHITE)
      }

      binding.tvAdd.setOnClickListener {
         if(binding.etName.text.toString().trim() == "" || binding.etWorkoutTime.text.toString().trim() == "" || binding.etCalories.text.toString().trim() == "" ||
            exerciseCategoryAdapter!!.categorySelected == "" || exercisePartAdapter!!.partSelected == "") {
            Toast.makeText(requireActivity(), "전부 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            // 새로추가된 카테고리 아이템 DB 에 저장
            for(i in 0 until categoryAddList.size) {
               dataManager!!.insertExerciseItem("category", categoryAddList[i])
            }

            for(i in 0 until partAddList.size) {
               dataManager!!.insertExerciseItem("part", partAddList[i])
            }

            // 카테고리에서 삭제된 아이템 삭제테이블에 저장 or 아이템테이블에서 삭제
            for(i in 0 until categoryDelList.size) {
               if(categoryDelList[i] == "유산소" || categoryDelList[i] == "무산소" || categoryDelList[i] == "근력운동" ||
                  categoryDelList[i] == "스포츠" || categoryDelList[i] == "일상생활" || categoryDelList[i] == "복합운동") {
                  dataManager!!.insertExerciseDelete("category", categoryDelList[i])
               }else {
                  dataManager!!.deleteExerciseItem("category", categoryDelList[i])
               }
            }

            for(i in 0 until partDelList.size) {
               if(partDelList[i] == "전신" || partDelList[i] == "상체" || partDelList[i] == "하체" ||
                  partDelList[i] == "팔" || partDelList[i] == "다리" || partDelList[i] == "복근") {
                  dataManager!!.insertExerciseDelete("part", partDelList[i])
               }else {
                  dataManager!!.deleteExerciseItem("part", partDelList[i])
               }
            }

            // 입력결과 DB 에 저장
            dataManager!!.insertExercise(Exercise(category = exerciseCategoryAdapter!!.categorySelected, name = binding.etName.text.toString(),
               workoutTime = binding.etWorkoutTime.text.toString(), calories = binding.etCalories.text.toString().toInt(), regDate = LocalDate.now().toString()))

            Toast.makeText(requireActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
            replaceFragment1(requireActivity(), ExerciseListFragment())
         }
      }
   }

   private fun setupCategory() {
      // 데이터 가져오기
      val getExerciseDelete = dataManager!!.getExerciseDelete("category")
      if(getExerciseDelete.size > 0) {
         for (i in 0 until initialCategoryList.size) {
            var check = true
            for (j in 0 until getExerciseDelete.size) {
               if(initialCategoryList[i] == getExerciseDelete[j]) {
                  check = false
               }
               if(j == (getExerciseDelete.size - 1) && check) {
                  categoryList.add(initialCategoryList[i])
               }
            }
         }
      }else {
         for(i in 0 until initialCategoryList.size) {
            categoryList.add(initialCategoryList[i])
         }
      }

      val getExerciseItem = dataManager!!.getExerciseItem("category")
      if(getExerciseItem.size > 0) {
         for (i in 0 until getExerciseItem.size) {
            categoryList.add(getExerciseItem[i])
         }
      }

      binding.recyclerView1.layoutManager = GridLayoutManager(activity, 3)
      exerciseCategoryAdapter = ExerciseCategoryAdapter(requireActivity(), categoryList)
      binding.recyclerView1.adapter = exerciseCategoryAdapter

      exerciseCategoryAdapter!!.setItemClickListener(object: ExerciseCategoryAdapter.OnItemClickListener{
         override fun onClick(v: View, position: Int) {
            categoryDelList.add(categoryList[position])
            categoryList.removeAt(position)
            exerciseCategoryAdapter?.notifyDataSetChanged()
         }
      })

      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_exercise_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val ivX = dialog.findViewById<ImageView>(R.id.ivX)
      val et = dialog.findViewById<EditText>(R.id.et)
      val tvConfirm = dialog.findViewById<TextView>(R.id.tvConfirm)

      ivX.setOnClickListener {
         dialog.dismiss()
      }

      // 리스트에 데이터 추가
      tvConfirm.setOnClickListener {
         var check = true
         for(i in 0 until categoryList.size) {
            if(categoryList[i] == et.text.toString().trim()) {
               Toast.makeText(requireActivity(), "데이터가 이미 존재합니다.", Toast.LENGTH_SHORT).show()
               check = false
            }
         }

         if(check) {
            categoryList.add(et.text.toString())
            categoryAddList.add(et.text.toString())
            exerciseCategoryAdapter?.notifyDataSetChanged()
         }

         dialog.dismiss()
      }

      binding.clAddType.setOnClickListener {
         dialog.show()
      }
   }

   private fun setupPart() {
      // 데이터 가져오기
      val getExerciseDelete = dataManager!!.getExerciseDelete("part")
      if(getExerciseDelete.size > 0) {
         for (i in 0 until initialPartList.size) {
            var check = true
            for (j in 0 until getExerciseDelete.size) {
               if(initialPartList[i] == getExerciseDelete[j]) {
                  check = false
               }
               if(j == (getExerciseDelete.size - 1) && check) {
                  partList.add(initialPartList[i])
               }
            }
         }
      }else {
         for(i in 0 until initialPartList.size) {
            partList.add(initialPartList[i])
         }
      }

      val getExerciseItem = dataManager!!.getExerciseItem("part")
      if(getExerciseItem.size > 0) {
         for (i in 0 until getExerciseItem.size) {
            partList.add(getExerciseItem[i])
         }
      }

      binding.recyclerView2.layoutManager = GridLayoutManager(activity, 3)
      exercisePartAdapter = ExercisePartAdapter(requireActivity(),partList)
      binding.recyclerView2.adapter = exercisePartAdapter

      exercisePartAdapter!!.setItemClickListener(object: ExercisePartAdapter.OnItemClickListener{
         override fun onClick(v: View, position: Int) {
            partDelList.add(partList[position])
            partList.removeAt(position)
            exercisePartAdapter?.notifyDataSetChanged()
         }
      })

      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_exercise_input)
      dialog.setCancelable(false)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val ivX = dialog.findViewById<ImageView>(R.id.ivX)
      val et = dialog.findViewById<EditText>(R.id.et)
      val tvConfirm = dialog.findViewById<TextView>(R.id.tvConfirm)

      ivX.setOnClickListener {
         dialog.dismiss()
      }

      // 리스트에 데이터 추가
      tvConfirm.setOnClickListener {
         var check = true
         for(i in 0 until partList.size) {
            if(partList[i] == et.text.toString().trim()) {
               Toast.makeText(requireActivity(), "데이터가 이미 존재합니다.", Toast.LENGTH_SHORT).show()
               check = false
            }
         }

         if(check) {
            partList.add(et.text.toString())
            partAddList.add(et.text.toString())
            exercisePartAdapter?.notifyDataSetChanged()
         }

         dialog.dismiss()
      }

      binding.clAddPart.setOnClickListener {
         dialog.show()
      }
   }

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment1(requireActivity(), ExerciseListFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}