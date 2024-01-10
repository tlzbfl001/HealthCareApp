package com.makebodywell.bodywell.view.home.exercise

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.makebodywell.bodywell.adapter.ExerciseInputAdapter1
import com.makebodywell.bodywell.adapter.ExerciseInputAdapter2
import com.makebodywell.bodywell.database.DBHelper
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_EXERCISE_DELETE
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_EXERCISE_ITEM
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentExerciseInputBinding
import com.makebodywell.bodywell.model.Exercise
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import java.time.LocalDate

class ExerciseInputFragment : Fragment() {
   private var _binding: FragmentExerciseInputBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null

   private var adapter1: ExerciseInputAdapter1? = null
   private var adapter2: ExerciseInputAdapter2? = null

   private val initialCategoryList = arrayListOf("유산소", "무산소", "근력운동", "스포츠", "일상생활", "복합운동")
   private val initialPartList = arrayListOf("전신", "상체", "하체", "팔", "다리", "복근")
   private val categoryList = ArrayList<String>()
   private val partList = ArrayList<String>()
   private val categoryAddList = ArrayList<String>()
   private val partAddList = ArrayList<String>()
   private val categoryDelList = ArrayList<String>()
   private val partDelList = ArrayList<String>()
   private var step = ""

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentExerciseInputBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      initView()

      // 데이터 목록 설정
      setupCategory()
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
         step = "힘들게"
      }

      binding.tvNormal.setOnClickListener {
         binding.tvHard.background = requireActivity().getDrawable(R.drawable.rec_5_border_gray)
         binding.tvHard.setTextColor(Color.BLACK)
         binding.tvNormal.background = requireActivity().getDrawable(R.drawable.rec_5_purple)
         binding.tvNormal.setTextColor(Color.WHITE)
         binding.tvEasy.background = requireActivity().getDrawable(R.drawable.rec_5_border_gray)
         binding.tvEasy.setTextColor(Color.BLACK)
         step = "적당히"
      }

      binding.tvEasy.setOnClickListener {
         binding.tvHard.background = requireActivity().getDrawable(R.drawable.rec_5_border_gray)
         binding.tvHard.setTextColor(Color.BLACK)
         binding.tvNormal.background = requireActivity().getDrawable(R.drawable.rec_5_border_gray)
         binding.tvNormal.setTextColor(Color.BLACK)
         binding.tvEasy.background = requireActivity().getDrawable(R.drawable.rec_5_purple)
         binding.tvEasy.setTextColor(Color.WHITE)
         step = "쉬엄쉬엄"
      }

      binding.tvAdd.setOnClickListener {
         if(binding.etName.text.toString().trim() == "" || binding.etWorkoutTime.text.toString().trim() == "" || binding.etCalories.text.toString().trim() == "" ||
            adapter1?.selected == "" || adapter2!!.selected == "" || step == "") {
            Toast.makeText(requireActivity(), "전부 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            // 새로추가된 카테고리 아이템 DB 에 저장
            for(i in 0 until categoryAddList.size) {
               dataManager!!.insertExercise(TABLE_EXERCISE_ITEM, "category", categoryAddList[i])
            }

            for(i in 0 until partAddList.size) {
               dataManager!!.insertExercise(TABLE_EXERCISE_ITEM, "part", partAddList[i])
            }

            // 삭제된 아이템 삭제테이블에 저장 or 아이템테이블에서 삭제
            for(i in 0 until categoryDelList.size) {
               if(categoryDelList[i] == "유산소" || categoryDelList[i] == "무산소" || categoryDelList[i] == "근력운동" ||
                  categoryDelList[i] == "스포츠" || categoryDelList[i] == "일상생활" || categoryDelList[i] == "복합운동") {
                  dataManager!!.insertExercise(TABLE_EXERCISE_DELETE, "category", categoryDelList[i])
               }else {
                  dataManager!!.deleteExerciseItem("category", categoryDelList[i])
               }
            }

            for(i in 0 until partDelList.size) {
               if(partDelList[i] == "전신" || partDelList[i] == "상체" || partDelList[i] == "하체" ||
                  partDelList[i] == "팔" || partDelList[i] == "다리" || partDelList[i] == "복근") {
                  dataManager!!.insertExercise(TABLE_EXERCISE_DELETE, "part", partDelList[i])
               }else {
                  dataManager!!.deleteExerciseItem("part", partDelList[i])
               }
            }

            // 입력결과 DB 에 저장
            dataManager!!.insertExercise(Exercise(category = adapter1!!.selected, name = binding.etName.text.toString(),
               workoutTime = binding.etWorkoutTime.text.toString().toInt(), calories = binding.etCalories.text.toString().toInt(), regDate = LocalDate.now().toString()))

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
      adapter1 = ExerciseInputAdapter1(requireActivity(), categoryList)
      binding.recyclerView1.adapter = adapter1

      adapter1!!.setItemClickListener(object: ExerciseInputAdapter1.OnItemClickListener{
         override fun onClick(v: View, position: Int) {
            categoryDelList.add(categoryList[position])
            categoryList.removeAt(position)
         }
      })

      // 리스트에 데이터 추가
      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_exercise_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val ivX = dialog.findViewById<ImageView>(R.id.ivX)
      val et = dialog.findViewById<EditText>(R.id.et)
      val tvConfirm = dialog.findViewById<TextView>(R.id.tvConfirm)

      ivX.setOnClickListener {
         dialog.dismiss()
      }

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
            adapter1?.notifyDataSetChanged()
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
      adapter2 = ExerciseInputAdapter2(requireActivity(),partList)
      binding.recyclerView2.adapter = adapter2

      adapter2!!.setItemClickListener(object: ExerciseInputAdapter2.OnItemClickListener{
         override fun onClick(v: View, position: Int) {
            partDelList.add(partList[position])
            partList.removeAt(position)
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
            adapter2?.notifyDataSetChanged()
         }

         dialog.dismiss()
      }

      binding.clAddPart.setOnClickListener {
         dialog.show()
      }
   }
}