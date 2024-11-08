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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.bodywell.android.R
import kr.bodywell.android.adapter.FoodTextAdapter
import kr.bodywell.android.adapter.PhotoSlideAdapter2
import kr.bodywell.android.databinding.FragmentFoodBinding
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.Food
import kr.bodywell.android.model.Goal
import kr.bodywell.android.model.Image
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.getFoodCalories
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.view.MainViewModel
import java.time.LocalDate
import kotlin.math.roundToInt

class FoodFragment : Fragment() {
   private var _binding: FragmentFoodBinding? = null
   private val binding get() = _binding!!

   private val viewModel: MainViewModel by activityViewModels()
   private var getGoal = Goal()
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

      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

      val et = dialog.findViewById<EditText>(R.id.et)
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)

      btnSave.setOnClickListener {
         if(et.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "목표를 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            runBlocking {
               if(getGoal.date == "") {
                  powerSync.insertGoal(Goal(kcalOfDiet = et.text.toString().toInt(), date = selectedDate.toString()))
                  getGoal = powerSync.getGoal(selectedDate.toString())
               }else {
                  powerSync.updateStr("goals", "kcal_of_diet", et.text.toString(), getGoal.id)
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

      runBlocking {
         getGoal = powerSync.getGoal(selectedDate.toString())
      }

      sum = getFoodCalories(requireActivity(), selectedDate.toString()).int5
      binding.tvGoal.text = "${getGoal.kcalOfDiet} kcal"
      binding.tvIntake.text = "$sum kcal"

      if(sum > 0) {
         binding.pbFood.setProgressStartColor(resources.getColor(R.color.food))
         binding.pbFood.setProgressEndColor(resources.getColor(R.color.food))
         binding.pbFood.max = getGoal.kcalOfDiet
         binding.pbFood.progress = sum
      }

      val remain = getGoal.kcalOfDiet - sum
      if(remain > 0) binding.tvRemain.text = "$remain kcal" else binding.tvRemain.text = "0 kcal"

      // 갤러리 초기화
      val imageList = ArrayList<Image>()
      binding.viewPager.adapter = null

      /*val getData1 = dataManager.getImage(Constant.BREAKFAST.name, selectedDate.toString())
      val getData2 = dataManager.getImage(Constant.LUNCH.name, selectedDate.toString())
      val getData3 = dataManager.getImage(Constant.DINNER.name, selectedDate.toString())
      val getData4 = dataManager.getImage(Constant.SNACK.name, selectedDate.toString())

      for(i in 0 until getData1.size) imageList.add(Image(id = getData1[i].id, imageName = getData1[i].imageName))
      for(i in 0 until getData2.size) imageList.add(Image(id = getData2[i].id, imageName = getData2[i].imageName))
      for(i in 0 until getData3.size) imageList.add(Image(id = getData3[i].id, imageName = getData3[i].imageName))
      for(i in 0 until getData4.size) imageList.add(Image(id = getData4[i].id, imageName = getData4[i].imageName))*/

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

      lifecycleScope.launch {
         val dietList1 = powerSync.getDiets(Constant.BREAKFAST.name, selectedDate.toString())
         val dietList2 = powerSync.getDiets(Constant.LUNCH.name, selectedDate.toString())
         val dietList3 = powerSync.getDiets(Constant.DINNER.name, selectedDate.toString())
         val dietList4 = powerSync.getDiets(Constant.SNACK.name, selectedDate.toString())

         var kcal1 = 0
         var carbohydrate1 = 0.0
         var protein1 = 0.0
         var fat1 = 0.0
         for(i in dietList1.indices) {
            kcal1 += dietList1[i].calorie * dietList1[i].quantity
            carbohydrate1 += dietList1[i].carbohydrate * dietList1[i].quantity
            protein1 += dietList1[i].protein * dietList1[i].quantity
            fat1 += dietList1[i].fat * dietList1[i].quantity
            itemList1.add(Food(id = dietList1[i].id, name = dietList1[i].name, calorie = dietList1[i].calorie,
               volume = dietList1[i].volume, volumeUnit = dietList1[i].volumeUnit, quantity = dietList1[i].quantity))
         }

         var kcal2 = 0
         var carbohydrate2 = 0.0
         var protein2 = 0.0
         var fat2 = 0.0
         for(i in dietList2.indices) {
            kcal2 += dietList2[i].calorie * dietList2[i].quantity
            carbohydrate2 += dietList2[i].carbohydrate * dietList2[i].quantity
            protein2 += dietList2[i].protein * dietList2[i].quantity
            fat2 += dietList2[i].fat * dietList2[i].quantity
            itemList2.add(Food(id = dietList2[i].id, name = dietList2[i].name, calorie = dietList2[i].calorie,
               volume = dietList2[i].volume, volumeUnit = dietList2[i].volumeUnit, quantity = dietList2[i].quantity))
         }

         var kcal3 = 0
         var carbohydrate3 = 0.0
         var protein3 = 0.0
         var fat3 = 0.0
         for(i in dietList3.indices) {
            kcal3 += dietList3[i].calorie * dietList3[i].quantity
            carbohydrate3 += dietList3[i].carbohydrate * dietList3[i].quantity
            protein3 += dietList3[i].protein * dietList3[i].quantity
            fat3 += dietList3[i].fat * dietList3[i].quantity
            itemList3.add(Food(id = dietList3[i].id, name = dietList3[i].name, calorie = dietList3[i].calorie,
               volume = dietList3[i].volume, volumeUnit = dietList3[i].volumeUnit, quantity = dietList3[i].quantity))
         }

         var kcal4 = 0
         var carbohydrate4 = 0.0
         var protein4 = 0.0
         var fat4 = 0.0
         for(i in dietList4.indices) {
            kcal4 += dietList4[i].calorie * dietList4[i].quantity
            carbohydrate4 += dietList4[i].carbohydrate * dietList4[i].quantity
            protein4 += dietList4[i].protein * dietList4[i].quantity
            fat4 += dietList4[i].fat * dietList4[i].quantity
            itemList4.add(Food(id = dietList4[i].id, name = dietList4[i].name, calorie = dietList4[i].calorie,
               volume = dietList4[i].volume, volumeUnit = dietList4[i].volumeUnit, quantity = dietList4[i].quantity))
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

         binding.rv1.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
         binding.rv1.adapter = FoodTextAdapter(itemList1)

         binding.rv2.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
         binding.rv2.adapter = FoodTextAdapter(itemList2)

         binding.rv3.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
         binding.rv3.adapter = FoodTextAdapter(itemList3)

         binding.rv4.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
         binding.rv4.adapter = FoodTextAdapter(itemList4)
      }
   }
}