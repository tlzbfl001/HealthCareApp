package kr.bodywell.android.view.home.food

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kr.bodywell.android.R
import kr.bodywell.android.adapter.FoodTextAdapter
import kr.bodywell.android.adapter.PhotoSlideAdapter2
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DAILY_GOAL
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentFoodBinding
import kr.bodywell.android.model.DailyGoal
import kr.bodywell.android.model.Food
import kr.bodywell.android.model.Image
import kr.bodywell.android.util.CalendarUtil.Companion.dateFormat
import kr.bodywell.android.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.android.util.CustomUtil.Companion.getFoodCalories
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment1
import kr.bodywell.android.view.home.MainFragment
import kr.bodywell.android.view.home.body.BodyFragment
import kr.bodywell.android.view.home.drug.DrugFragment
import kr.bodywell.android.view.home.exercise.ExerciseFragment
import kr.bodywell.android.view.home.sleep.SleepFragment
import kr.bodywell.android.view.home.water.WaterFragment
import kotlin.math.roundToInt

class FoodFragment : Fragment() {
   private var _binding: FragmentFoodBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private var getDailyGoal = DailyGoal()
   private val itemList1 = ArrayList<Food>()
   private val itemList2 = ArrayList<Food>()
   private val itemList3 = ArrayList<Food>()
   private val itemList4 = ArrayList<Food>()
   private var isExpand1 = false
   private var isExpand2 = false
   private var isExpand3 = false
   private var isExpand4 = false
   private var sum = 0

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment1(requireActivity(), MainFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodBinding.inflate(layoutInflater)

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }

      dataManager = DataManager(activity)
      dataManager.open()

      binding.tvDate.text = dateFormat(selectedDate)

      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

      val et = dialog.findViewById<EditText>(R.id.et)
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)

      btnSave.setOnClickListener {
         if(et.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "입력된 문자가 없습니다.", Toast.LENGTH_SHORT).show()
         }else {
            if(getDailyGoal.regDate == "") {
               dataManager.insertDailyGoal(DailyGoal(foodGoal = et.text.toString().toInt(), regDate = selectedDate.toString()))
            }else {
               dataManager.updateIntByDate(TABLE_DAILY_GOAL, "foodGoal", et.text.toString().toInt(), selectedDate.toString())
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

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), MainFragment())
      }

      binding.clPrev.setOnClickListener {
         selectedDate = selectedDate.minusDays(1)
         binding.tvDate.text = dateFormat(selectedDate)

         dailyGoal()
         photoView()
         listView()
      }

      binding.clNext.setOnClickListener {
         selectedDate = selectedDate.plusDays(1)
         binding.tvDate.text = dateFormat(selectedDate)

         dailyGoal()
         photoView()
         listView()
      }

      binding.cvWater.setOnClickListener {
         replaceFragment1(requireActivity(), WaterFragment())
      }

      binding.cvExercise.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseFragment())
      }

      binding.cvBody.setOnClickListener {
         replaceFragment1(requireActivity(), BodyFragment())
      }

      binding.cvSleep.setOnClickListener {
         replaceFragment1(requireActivity(), SleepFragment())
      }

      binding.cvDrug.setOnClickListener {
         replaceFragment1(requireActivity(), DrugFragment())
      }

      binding.clRecord.setOnClickListener {
         replaceFragment1(requireActivity(), FoodBreakfastFragment())
      }

      binding.clExpand1.setOnClickListener {
         if(itemList1.size > 0) {
            if (isExpand1) {
               binding.clView1.visibility = View.GONE
               binding.ivExpand1.setImageResource(R.drawable.arrow_down)
            } else {
               binding.clView1.visibility = View.VISIBLE
               binding.ivExpand1.setImageResource(R.drawable.arrow_up)
            }
            isExpand1 = !isExpand1
         }
      }

      binding.clExpand2.setOnClickListener {
         if(itemList2.size > 0) {
            if (isExpand2) {
               binding.clView2.visibility = View.GONE
               binding.ivExpand2.setImageResource(R.drawable.arrow_down)
            } else {
               binding.clView2.visibility = View.VISIBLE
               binding.ivExpand2.setImageResource(R.drawable.arrow_up)
            }
            isExpand2 = !isExpand2
         }
      }

      binding.clExpand3.setOnClickListener {
         if(itemList3.size > 0) {
            if (isExpand3) {
               binding.clView3.visibility = View.GONE
               binding.ivExpand3.setImageResource(R.drawable.arrow_down)
            } else {
               binding.clView3.visibility = View.VISIBLE
               binding.ivExpand3.setImageResource(R.drawable.arrow_up)
            }
            isExpand3 = !isExpand3
         }
      }

      binding.clExpand4.setOnClickListener {
         if(itemList4.size > 0) {
            if (isExpand4) {
               binding.clView4.visibility = View.GONE
               binding.ivExpand4.setImageResource(R.drawable.arrow_down)
            } else {
               binding.clView4.visibility = View.VISIBLE
               binding.ivExpand4.setImageResource(R.drawable.arrow_up)
            }
            isExpand4 = !isExpand4
         }
      }

      dailyGoal()
      photoView()
      listView()

      return binding.root
   }

   private fun dailyGoal() {
      // 목표 초기화
      binding.pbFood.setProgressStartColor(Color.TRANSPARENT)
      binding.pbFood.setProgressEndColor(Color.TRANSPARENT)
      binding.tvGoal.text = "0 kcal"
      binding.tvRemain.text = "0 kcal"

      getDailyGoal = dataManager.getDailyGoal(selectedDate.toString())
      sum = getFoodCalories(requireActivity(), selectedDate.toString()).int5

      if(sum > 0) {
         binding.pbFood.setProgressStartColor(Color.parseColor("#EE6685"))
         binding.pbFood.setProgressEndColor(Color.parseColor("#EE6685"))
         binding.pbFood.max = getDailyGoal.foodGoal
         binding.pbFood.progress = sum
      }

      binding.tvGoal.text = "${getDailyGoal.foodGoal} kcal"
      binding.tvIntake.text = "$sum kcal"

      val remain = getDailyGoal.foodGoal - sum
      if(remain > 0) {
         binding.tvRemain.text = "$remain kcal"
      }else {
         binding.tvRemain.text = "0 kcal"
      }
   }

   private fun photoView() {
      val imageList = ArrayList<Image>()
      binding.viewPager.adapter = null

      val getData1 = dataManager.getImage(1, selectedDate.toString())
      val getData2 = dataManager.getImage(2, selectedDate.toString())
      val getData3 = dataManager.getImage(3, selectedDate.toString())
      val getData4 = dataManager.getImage(4, selectedDate.toString())

      for(i in 0 until getData1.size) {
         imageList.add(Image(id = getData1[i].id, imageUri = Uri.parse(getData1[i].imageUri).toString()))
      }

      for(i in 0 until getData2.size) {
         imageList.add(Image(id = getData2[i].id, imageUri = Uri.parse(getData2[i].imageUri).toString()))
      }

      for(i in 0 until getData3.size) {
         imageList.add(Image(id = getData3[i].id, imageUri = Uri.parse(getData3[i].imageUri).toString()))
      }

      for(i in 0 until getData4.size) {
         imageList.add(Image(id = getData4[i].id, imageUri = Uri.parse(getData4[i].imageUri).toString()))
      }

      if(imageList.size > 0) {
         val adapter = PhotoSlideAdapter2(requireActivity(), imageList)
         binding.viewPager.adapter = adapter
         binding.viewPager.setPadding(0, 0, 0, 0)

         binding.clLeft.setOnClickListener {
            val current = binding.viewPager.currentItem
            if(current == 0) {
               binding.viewPager.setCurrentItem(0, true)
            }else {
               binding.viewPager.setCurrentItem(current-1, true)
            }
         }

         binding.clRight.setOnClickListener {
            val current = binding.viewPager.currentItem
            binding.viewPager.setCurrentItem(current+1, true)
         }
      }
   }

   private fun listView() {
      binding.clView1.visibility = View.GONE
      binding.ivExpand1.setImageResource(R.drawable.arrow_down)
      binding.clView2.visibility = View.GONE
      binding.ivExpand2.setImageResource(R.drawable.arrow_down)
      binding.clView3.visibility = View.GONE
      binding.ivExpand3.setImageResource(R.drawable.arrow_down)
      binding.clView4.visibility = View.GONE
      binding.ivExpand4.setImageResource(R.drawable.arrow_down)

      itemList1.clear()
      itemList2.clear()
      itemList3.clear()
      itemList4.clear()

      val getUser = dataManager.getUser()
      val getFood1 = dataManager.getDailyFood(1, selectedDate.toString())
      val getFood2 = dataManager.getDailyFood(2, selectedDate.toString())
      val getFood3 = dataManager.getDailyFood(3, selectedDate.toString())
      val getFood4 = dataManager.getDailyFood(4, selectedDate.toString())

      var kcal1 = 0
      var carbohydrate1 = 0.0
      var protein1 = 0.0
      var fat1 = 0.0
      for(i in 0 until getFood1.size) {
         kcal1 += getFood1[i].kcal * getFood1[i].count
         carbohydrate1 += getFood1[i].carbohydrate * getFood1[i].count
         protein1 += getFood1[i].protein * getFood1[i].count
         fat1 += getFood1[i].fat * getFood1[i].count

         itemList1.add(Food(id = getFood1[i].id, name = getFood1[i].name, unit = getFood1[i].unit, amount = getFood1[i].amount, count = getFood1[i].count,
               kcal = getFood1[i].kcal, carbohydrate = getFood1[i].carbohydrate, protein = getFood1[i].protein, fat = getFood1[i].fat,
               salt = getFood1[i].salt, sugar = getFood1[i].sugar, type = getFood1[i].type, regDate = getFood1[i].regDate)
         )
      }

      var kcal2 = 0
      var carbohydrate2 = 0.0
      var protein2 = 0.0
      var fat2 = 0.0
      for(i in 0 until getFood2.size) {
         kcal2 += getFood2[i].kcal * getFood2[i].count
         carbohydrate2 += getFood2[i].carbohydrate * getFood2[i].count
         protein2 += getFood2[i].protein * getFood2[i].count
         fat2 += getFood2[i].fat * getFood2[i].count

         itemList2.add(Food(id = getFood2[i].id, name = getFood2[i].name, unit = getFood2[i].unit, amount = getFood2[i].amount, count = getFood2[i].count,
               kcal = getFood2[i].kcal, carbohydrate = getFood2[i].carbohydrate, protein = getFood2[i].protein, fat = getFood2[i].fat,
               salt = getFood2[i].salt, sugar = getFood2[i].sugar, type = getFood2[i].type, regDate = getFood2[i].regDate)
         )
      }

      var kcal3 = 0
      var carbohydrate3 = 0.0
      var protein3 = 0.0
      var fat3 = 0.0
      for(i in 0 until getFood3.size) {
         kcal3 += getFood3[i].kcal * getFood3[i].count
         carbohydrate3 += getFood3[i].carbohydrate * getFood3[i].count
         protein3 += getFood3[i].protein * getFood3[i].count
         fat3 += getFood3[i].fat * getFood3[i].count

         itemList3.add(Food(id = getFood3[i].id, name = getFood3[i].name, unit = getFood3[i].unit, amount = getFood3[i].amount, count = getFood3[i].count,
               kcal = getFood3[i].kcal, carbohydrate = getFood3[i].carbohydrate, protein = getFood3[i].protein, fat = getFood3[i].fat,
               salt = getFood3[i].salt, sugar = getFood3[i].sugar, type = getFood3[i].type, regDate = getFood3[i].regDate)
         )
      }

      var kcal4 = 0
      var carbohydrate4 = 0.0
      var protein4 = 0.0
      var fat4 = 0.0
      for(i in 0 until getFood4.size) {
         kcal4 += getFood4[i].kcal * getFood4[i].count
         carbohydrate4 += getFood4[i].carbohydrate * getFood4[i].count
         protein4 += getFood4[i].protein * getFood4[i].count
         fat4 += getFood4[i].fat * getFood4[i].count

         itemList4.add(Food(id = getFood4[i].id, name = getFood4[i].name, unit = getFood4[i].unit, amount = getFood4[i].amount, count = getFood4[i].count,
               kcal = getFood4[i].kcal, carbohydrate = getFood4[i].carbohydrate, protein = getFood4[i].protein, fat = getFood4[i].fat,
               salt = getFood4[i].salt, sugar = getFood4[i].sugar, type = getFood4[i].type, regDate = getFood4[i].regDate)
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

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}