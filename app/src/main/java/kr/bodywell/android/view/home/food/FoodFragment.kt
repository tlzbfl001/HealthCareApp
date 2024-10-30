package kr.bodywell.android.view.home.food

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kr.bodywell.android.R
import kr.bodywell.android.adapter.FoodTextAdapter
import kr.bodywell.android.adapter.PhotoSlideAdapter2
import kr.bodywell.android.database.DBHelper.Companion.IS_UPDATED
import kr.bodywell.android.database.DBHelper.Companion.FOOD
import kr.bodywell.android.database.DBHelper.Companion.GOAL
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentFoodBinding
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.Food
import kr.bodywell.android.model.Goal
import kr.bodywell.android.model.Image
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.getFoodCalories
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.view.MainViewModel
import java.time.LocalDate
import kotlin.math.roundToInt

class FoodFragment : Fragment() {
   private var _binding: FragmentFoodBinding? = null
   private val binding get() = _binding!!

   private val viewModel: MainViewModel by activityViewModels()
   private lateinit var dataManager: DataManager
   private var dailyGoal = Goal()
   private val itemList1 = ArrayList<Food>()
   private val itemList2 = ArrayList<Food>()
   private val itemList3 = ArrayList<Food>()
   private val itemList4 = ArrayList<Food>()
   private var isExpand1 = false
   private var isExpand2 = false
   private var isExpand3 = false
   private var isExpand4 = false
   private var sum = 0

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager.open()

      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

      val et = dialog.findViewById<EditText>(R.id.et)
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)

      btnSave.setOnClickListener {
         if(et.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "목표를 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            if(dailyGoal.createdAt == "") {
               dataManager.insertGoal(Goal(food = et.text.toString().toInt(), createdAt = selectedDate.toString()))
               dailyGoal = dataManager.getGoal(selectedDate.toString())
            }else {
               dataManager.updateInt(GOAL, FOOD, et.text.toString().toInt(), selectedDate.toString())
               dataManager.updateInt(GOAL, IS_UPDATED, 1, "id", dailyGoal.id)
            }

            binding.pbFood.max = et.text.toString().toInt()
            binding.tvGoal.text = "${et.text} kcal"

            val remain = et.text.toString().toInt() - sum
            if(remain > 0) {
               binding.tvRemain.text = "$remain kcal"
            }else {
               binding.tvRemain.text = "0 kcal"
            }

            dialog.dismiss()
         }
      }

      binding.clGoal.setOnClickListener {
         dialog.show()
      }

      binding.clRecord.setOnClickListener {
         replaceFragment1(requireActivity(), FoodDetailFragment())
      }

      binding.clExpand1.setOnClickListener {
         if(itemList1.size > 0) {
            if(isExpand1) {
               binding.clView1.visibility = View.GONE
               binding.ivExpand1.setImageResource(R.drawable.arrow_down)
            }else {
               binding.clView1.visibility = View.VISIBLE
               binding.ivExpand1.setImageResource(R.drawable.arrow_up)
            }
            isExpand1 = !isExpand1
         }else {
            Toast.makeText(requireActivity(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show()
         }
      }

      binding.clExpand2.setOnClickListener {
         if(itemList2.size > 0) {
            if(isExpand2) {
               binding.clView2.visibility = View.GONE
               binding.ivExpand2.setImageResource(R.drawable.arrow_down)
            }else {
               binding.clView2.visibility = View.VISIBLE
               binding.ivExpand2.setImageResource(R.drawable.arrow_up)
            }
            isExpand2 = !isExpand2
         }else {
            Toast.makeText(requireActivity(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show()
         }
      }

      binding.clExpand3.setOnClickListener {
         if(itemList3.size > 0) {
            if(isExpand3) {
               binding.clView3.visibility = View.GONE
               binding.ivExpand3.setImageResource(R.drawable.arrow_down)
            }else {
               binding.clView3.visibility = View.VISIBLE
               binding.ivExpand3.setImageResource(R.drawable.arrow_up)
            }
            isExpand3 = !isExpand3
         }else {
            Toast.makeText(requireActivity(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show()
         }
      }

      binding.clExpand4.setOnClickListener {
         if(itemList4.size > 0) {
            if(isExpand4) {
               binding.clView4.visibility = View.GONE
               binding.ivExpand4.setImageResource(R.drawable.arrow_down)
            }else {
               binding.clView4.visibility = View.VISIBLE
               binding.ivExpand4.setImageResource(R.drawable.arrow_up)
            }
            isExpand4 = !isExpand4
         }else {
            Toast.makeText(requireActivity(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show()
         }
      }

      viewModel.dateVM.observe(viewLifecycleOwner, Observer<LocalDate> {
         dailyView()
         listView()
      })

      dailyView()
      listView()

      return binding.root
   }

   private fun dailyView() {
      // 목표 초기화
      binding.pbFood.setProgressStartColor(Color.TRANSPARENT)
      binding.pbFood.setProgressEndColor(Color.TRANSPARENT)
      binding.tvGoal.text = "0 kcal"
      binding.tvRemain.text = "0 kcal"
      dailyGoal = dataManager.getGoal(selectedDate.toString())
      sum = getFoodCalories(requireActivity(), selectedDate.toString()).int5
      binding.tvGoal.text = "${dailyGoal.food} kcal"
      binding.tvIntake.text = "$sum kcal"

      if(sum > 0) {
         binding.pbFood.setProgressStartColor(resources.getColor(R.color.food))
         binding.pbFood.setProgressEndColor(resources.getColor(R.color.food))
         binding.pbFood.max = dailyGoal.food
         binding.pbFood.progress = sum
      }

      val remain = dailyGoal.food - sum
      if(remain > 0) binding.tvRemain.text = "$remain kcal" else binding.tvRemain.text = "0 kcal"

      // 갤러리 초기화
      val imageList = ArrayList<Image>()
      binding.viewPager.adapter = null

      val getData1 = dataManager.getImage(Constant.BREAKFAST.name, selectedDate.toString())
      val getData2 = dataManager.getImage(Constant.LUNCH.name, selectedDate.toString())
      val getData3 = dataManager.getImage(Constant.DINNER.name, selectedDate.toString())
      val getData4 = dataManager.getImage(Constant.SNACK.name, selectedDate.toString())

      for(i in 0 until getData1.size) imageList.add(Image(id = getData1[i].id, imageName = getData1[i].imageName))
      for(i in 0 until getData2.size) imageList.add(Image(id = getData2[i].id, imageName = getData2[i].imageName))
      for(i in 0 until getData3.size) imageList.add(Image(id = getData3[i].id, imageName = getData3[i].imageName))
      for(i in 0 until getData4.size) imageList.add(Image(id = getData4[i].id, imageName = getData4[i].imageName))

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
      itemList1.clear()
      itemList2.clear()
      itemList3.clear()
      itemList4.clear()
      binding.clView1.visibility = View.GONE
      binding.ivExpand1.setImageResource(R.drawable.arrow_down)
      binding.clView2.visibility = View.GONE
      binding.ivExpand2.setImageResource(R.drawable.arrow_down)
      binding.clView3.visibility = View.GONE
      binding.ivExpand3.setImageResource(R.drawable.arrow_down)
      binding.clView4.visibility = View.GONE
      binding.ivExpand4.setImageResource(R.drawable.arrow_down)

      val getDailyFood1 = dataManager.getDailyFood(Constant.BREAKFAST.name, selectedDate.toString())
      val getDailyFood2 = dataManager.getDailyFood(Constant.LUNCH.name, selectedDate.toString())
      val getDailyFood3 = dataManager.getDailyFood(Constant.DINNER.name, selectedDate.toString())
      val getDailyFood4 = dataManager.getDailyFood(Constant.SNACK.name, selectedDate.toString())

      var kcal1 = 0
      var carbohydrate1 = 0.0
      var protein1 = 0.0
      var fat1 = 0.0
      for(i in 0 until getDailyFood1.size) {
         kcal1 += getDailyFood1[i].calorie * getDailyFood1[i].count
         carbohydrate1 += getDailyFood1[i].carbohydrate * getDailyFood1[i].count
         protein1 += getDailyFood1[i].protein * getDailyFood1[i].count
         fat1 += getDailyFood1[i].fat * getDailyFood1[i].count

         itemList1.add(Food(id = getDailyFood1[i].id, name = getDailyFood1[i].name, unit = getDailyFood1[i].unit, amount = getDailyFood1[i].amount,
            count = getDailyFood1[i].count, calorie = getDailyFood1[i].calorie, carbohydrate = getDailyFood1[i].carbohydrate, protein = getDailyFood1[i].protein,
            fat = getDailyFood1[i].fat, salt = getDailyFood1[i].salt, sugar = getDailyFood1[i].sugar, type = getDailyFood1[i].type, createdAt = getDailyFood1[i].createdAt)
         )
      }

      var kcal2 = 0
      var carbohydrate2 = 0.0
      var protein2 = 0.0
      var fat2 = 0.0
      for(i in 0 until getDailyFood2.size) {
         kcal2 += getDailyFood2[i].calorie * getDailyFood2[i].count
         carbohydrate2 += getDailyFood2[i].carbohydrate * getDailyFood2[i].count
         protein2 += getDailyFood2[i].protein * getDailyFood2[i].count
         fat2 += getDailyFood2[i].fat * getDailyFood2[i].count

         itemList2.add(Food(id = getDailyFood2[i].id, name = getDailyFood2[i].name, unit = getDailyFood2[i].unit, amount = getDailyFood2[i].amount,
            count = getDailyFood2[i].count, calorie = getDailyFood2[i].calorie, carbohydrate = getDailyFood2[i].carbohydrate, protein = getDailyFood2[i].protein,
            fat = getDailyFood2[i].fat, salt = getDailyFood2[i].salt, sugar = getDailyFood2[i].sugar, type = getDailyFood2[i].type, createdAt = getDailyFood2[i].createdAt)
         )
      }

      var kcal3 = 0
      var carbohydrate3 = 0.0
      var protein3 = 0.0
      var fat3 = 0.0
      for(i in 0 until getDailyFood3.size) {
         kcal3 += getDailyFood3[i].calorie * getDailyFood3[i].count
         carbohydrate3 += getDailyFood3[i].carbohydrate * getDailyFood3[i].count
         protein3 += getDailyFood3[i].protein * getDailyFood3[i].count
         fat3 += getDailyFood3[i].fat * getDailyFood3[i].count

         itemList3.add(Food(id = getDailyFood3[i].id, name = getDailyFood3[i].name, unit = getDailyFood3[i].unit, amount = getDailyFood3[i].amount,
            count = getDailyFood3[i].count, calorie = getDailyFood3[i].calorie, carbohydrate = getDailyFood3[i].carbohydrate, protein = getDailyFood3[i].protein,
            fat = getDailyFood3[i].fat, salt = getDailyFood3[i].salt, sugar = getDailyFood3[i].sugar, type = getDailyFood3[i].type, createdAt = getDailyFood3[i].createdAt)
         )
      }

      var kcal4 = 0
      var carbohydrate4 = 0.0
      var protein4 = 0.0
      var fat4 = 0.0
      for(i in 0 until getDailyFood4.size) {
         kcal4 += getDailyFood4[i].calorie * getDailyFood4[i].count
         carbohydrate4 += getDailyFood4[i].carbohydrate * getDailyFood4[i].count
         protein4 += getDailyFood4[i].protein * getDailyFood4[i].count
         fat4 += getDailyFood4[i].fat * getDailyFood4[i].count

         itemList4.add(Food(id = getDailyFood4[i].id, name = getDailyFood4[i].name, unit = getDailyFood4[i].unit, amount = getDailyFood4[i].amount,
            count = getDailyFood4[i].count, calorie = getDailyFood4[i].calorie, carbohydrate = getDailyFood4[i].carbohydrate, protein = getDailyFood4[i].protein,
            fat = getDailyFood4[i].fat, salt = getDailyFood4[i].salt, sugar = getDailyFood4[i].sugar, type = getDailyFood4[i].type, createdAt = getDailyFood4[i].createdAt)
         )
      }

      val totalCar = carbohydrate1 + carbohydrate2 + carbohydrate3 + carbohydrate4
      val totalPro = protein1 + protein2 + protein3 + protein4
      val totalFat = fat1 + fat2 + fat3 + fat4
      val recommendCar = totalCar / 324 * 100
      val recommendPro = totalPro / 55 * 100
      val recommendFat = totalFat / 54 * 100

      binding.tvCalPct.text = "순탄수 " + recommendCar.roundToInt() + "%"
      binding.tvProteinPct.text = "단백질 " + recommendPro.roundToInt() + "%"
      binding.tvFatPct.text = "지방 " + recommendFat.roundToInt() + "%"
      binding.tvCar.text = String.format("%.1f", totalCar) + "g"
      binding.tvProtein.text = String.format("%.1f", totalPro) + "g"
      binding.tvFat.text = String.format("%.1f", totalFat) + "g"

      binding.tvTotal1.text = "$kcal1 kcal"
      binding.tvTotal2.text = "$kcal2 kcal"
      binding.tvTotal3.text = "$kcal3 kcal"
      binding.tvTotal4.text = "$kcal4 kcal"

      val adapter1 = FoodTextAdapter(itemList1)
      binding.rv1.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.rv1.adapter = adapter1

      val adapter2 = FoodTextAdapter(itemList2)
      binding.rv2.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.rv2.adapter = adapter2

      val adapter3 = FoodTextAdapter(itemList3)
      binding.rv3.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.rv3.adapter = adapter3

      val adapter4 = FoodTextAdapter(itemList4)
      binding.rv4.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.rv4.adapter = adapter4
   }
}