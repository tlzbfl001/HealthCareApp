package com.makebodywell.bodywell.view.home.food

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.FoodTextAdapter
import com.makebodywell.bodywell.adapter.PhotoSlideAdapter2
import com.makebodywell.bodywell.adapter.PhotoViewAdapter
import com.makebodywell.bodywell.database.DBHelper
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentFoodBinding
import com.makebodywell.bodywell.model.DailyData
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.model.Image
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.getFoodKcal
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.view.home.MainFragment
import com.makebodywell.bodywell.view.home.body.BodyFragment
import com.makebodywell.bodywell.view.home.drug.DrugFragment
import com.makebodywell.bodywell.view.home.exercise.ExerciseFragment
import com.makebodywell.bodywell.view.home.sleep.SleepFragment
import com.makebodywell.bodywell.view.home.water.WaterFragment
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.round

class FoodFragment : Fragment() {
   private var _binding: FragmentFoodBinding? = null
   private val binding get() = _binding!!

   private var bundle = Bundle()

   private var calendarDate: LocalDate? = null

   private var dataManager: DataManager? = null
   private var adapter: PhotoViewAdapter? = null
   private var imageList: ArrayList<Image> = ArrayList()
   private var getDailyData = DailyData()

   private var sum = 0
   private var isExpand = false

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      calendarDate = LocalDate.now()
      binding.tvDate.text = dateFormat(calendarDate)

      // 목표 설정
      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val et = dialog.findViewById<EditText>(R.id.et)
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)

      btnSave.setOnClickListener {
         if(et.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "전부 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            if(getDailyData.regDate == "") {
               dataManager!!.insertDailyData(DailyData(foodGoal = et.text.toString().toInt(), regDate = calendarDate.toString()))
            }else {
               dataManager!!.updateGoal("foodGoal", et.text.toString().toInt(), calendarDate.toString())
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

      binding.ivPrev.setOnClickListener {
         calendarDate = calendarDate!!.minusDays(1)
         binding.tvDate.text = dateFormat(calendarDate)
         setupGoal()
         dailyView()
         listView()
      }

      binding.ivNext.setOnClickListener {
         calendarDate = calendarDate!!.plusDays(1)
         binding.tvDate.text = dateFormat(calendarDate)
         setupGoal()
         dailyView()
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
         bundle.putString("calendarDate", calendarDate.toString())
         replaceFragment2(requireActivity(), FoodBreakfastFragment(), bundle)
      }

      binding.clExpand1.setOnClickListener {
         if (isExpand) {
            binding.clView1.visibility = View.GONE
            binding.ivExpand1.setImageResource(R.drawable.arrow_down)
         } else {
            binding.clView1.visibility = View.VISIBLE
            binding.ivExpand1.setImageResource(R.drawable.arrow_up)
         }
         isExpand = !isExpand
      }

      setupGoal()
      dailyView()

      return binding.root
   }

   private fun setupGoal() {
      // 텍스트 초기화
      binding.tvGoal.text = "0 kcal"
      binding.tvRemain.text = "0 kcal"

      // 목표 초기화
      getDailyData = dataManager!!.getDailyData(calendarDate.toString())
      sum = getFoodKcal(requireActivity(), calendarDate.toString()).int1

      if(getDailyData.foodGoal > 0 && sum > 0) {
         binding.pbFood.max = getDailyData.foodGoal
         binding.pbFood.progress = sum
      }else if (getDailyData.foodGoal == 0 && sum > 0) {
         binding.pbFood.max = sum
         binding.pbFood.progress = sum
      }

      binding.tvGoal.text = "${getDailyData.foodGoal} kcal"
      binding.tvIntake.text = "$sum kcal"

      val remain = getDailyData.foodGoal  - sum
      if(remain > 0) {
         binding.tvRemain.text = "$remain kcal"
      }else {
         binding.tvRemain.text = "0 kcal"
      }
   }

   private fun dailyView() {
      val getFood = dataManager!!.getFood(1, calendarDate.toString())

      // 이미지뷰 설정
      photoView()

      // 영양성분 설정
      nutritionView(getFood)

      // 리스트뷰 설정
      listView()
   }

   private fun photoView() {
      val imageList: ArrayList<Image> = ArrayList()

      val getData1 = dataManager!!.getImage(1, calendarDate.toString())
      val getData2 = dataManager!!.getImage(2, calendarDate.toString())
      val getData3 = dataManager!!.getImage(3, calendarDate.toString())
      val getData4 = dataManager!!.getImage(4, calendarDate.toString())

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

   private fun nutritionView(dataList: ArrayList<Food>) {
      var carbohydrate = 0.0
      var protein = 0.0
      var fat = 0.0

      if(dataList.size > 0) {
         for(i in 0 until dataList.size) {
            carbohydrate += dataList[i].carbohydrate.toDouble()
            protein += dataList[i].protein.toDouble()
            fat += dataList[i].fat.toDouble()
         }
      }

      val recommendedCar = "순탄수 " + (round(carbohydrate) * (50/100)) + "%"
      val recommendedPro = "단백질 " + round(protein) + "%"
      val recommendedFat = "지방 " + (round(fat) * (150/100)) + "%"

      binding.tvCalPct.text = recommendedCar
      binding.tvProteinPct.text = recommendedPro
      binding.tvFatPct.text = recommendedFat
      binding.tvCar.text = carbohydrate.toString() + "g"
      binding.tvProtein.text = protein.toString() + "g"
      binding.tvFat.text = fat.toString() + "g"
   }

   private fun listView() {
      val itemList1 = ArrayList<Food>()
      val itemList2 = ArrayList<Food>()
      val itemList3 = ArrayList<Food>()
      val itemList4 = ArrayList<Food>()

      val getFood1 = dataManager!!.getFood(1, calendarDate.toString())
      val getFood2 = dataManager!!.getFood(2, calendarDate.toString())
      val getFood3 = dataManager!!.getFood(3, calendarDate.toString())
      val getFood4 = dataManager!!.getFood(4, calendarDate.toString())

      Log.d(TAG, "setupList: $getFood1")

      var total1 = 0
      for(i in 0 until getFood1.size) {
         total1 += getFood1[i].kcal.toInt() * getFood1[i].count
         itemList1.add(
            Food(id = getFood1[i].id, name = getFood1[i].name, unit = getFood1[i].unit, amount = getFood1[i].amount, count = getFood1[i].count,
               kcal = getFood1[i].kcal, carbohydrate = getFood1[i].carbohydrate, protein = getFood1[i].protein, fat = getFood1[i].fat,
               salt = getFood1[i].salt, sugar = getFood1[i].sugar, type = getFood1[i].type, regDate = getFood1[i].regDate)
         )
      }

      var total2 = 0
      for(i in 0 until getFood2.size) {
         total2 += getFood2[i].kcal.toInt() * getFood2[i].count
         itemList2.add(
            Food(id = getFood2[i].id, name = getFood2[i].name, unit = getFood2[i].unit, amount = getFood2[i].amount, count = getFood2[i].count,
               kcal = getFood2[i].kcal, carbohydrate = getFood2[i].carbohydrate, protein = getFood2[i].protein, fat = getFood2[i].fat,
               salt = getFood2[i].salt, sugar = getFood2[i].sugar, type = getFood2[i].type, regDate = getFood2[i].regDate)
         )
      }

      var total3 = 0
      for(i in 0 until getFood3.size) {
         total3 += getFood3[i].kcal.toInt() * getFood3[i].count
         itemList3.add(
            Food(id = getFood3[i].id, name = getFood3[i].name, unit = getFood3[i].unit, amount = getFood3[i].amount, count = getFood3[i].count,
               kcal = getFood3[i].kcal, carbohydrate = getFood3[i].carbohydrate, protein = getFood3[i].protein, fat = getFood3[i].fat,
               salt = getFood3[i].salt, sugar = getFood3[i].sugar, type = getFood3[i].type, regDate = getFood3[i].regDate)
         )
      }

      var total4 = 0
      for(i in 0 until getFood4.size) {
         total4 += getFood4[i].kcal.toInt() * getFood4[i].count
         itemList4.add(
            Food(id = getFood4[i].id, name = getFood4[i].name, unit = getFood4[i].unit, amount = getFood4[i].amount, count = getFood4[i].count,
               kcal = getFood4[i].kcal, carbohydrate = getFood4[i].carbohydrate, protein = getFood4[i].protein, fat = getFood4[i].fat,
               salt = getFood4[i].salt, sugar = getFood4[i].sugar, type = getFood4[i].type, regDate = getFood4[i].regDate)
         )
      }

      binding.tvTotal1.text = "$total1 kcal"
      binding.tvTotal2.text = "$total2 kcal"
      binding.tvTotal3.text = "$total3 kcal"
      binding.tvTotal4.text = "$total4 kcal"

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