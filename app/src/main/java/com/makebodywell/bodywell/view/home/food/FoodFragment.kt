package com.makebodywell.bodywell.view.home.food

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.FoodTextAdapter
import com.makebodywell.bodywell.adapter.PhotoViewAdapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentFoodBinding
import com.makebodywell.bodywell.model.DailyData
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.model.Image
import com.makebodywell.bodywell.model.Text
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.getFoodIntake
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

   private lateinit var callback: OnBackPressedCallback

   private var bundle = Bundle()

   private var calendarDate: LocalDate? = null

   private var dataManager: DataManager? = null
   private var adapter: PhotoViewAdapter? = null
   private var imageList: ArrayList<Image> = ArrayList()
   private var getDailyData = DailyData()
   private var sum = 0

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      initView()
      setupGoal()
      dailyView()

      return binding.root
   }

   private fun initView() {
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
               dataManager!!.updateFoodGoal(DailyData(foodGoal = et.text.toString().toInt(), regDate = calendarDate.toString()))
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

      binding.cvGoal.setOnClickListener {
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
      }

      binding.ivNext.setOnClickListener {
         calendarDate = calendarDate!!.plusDays(1)
         binding.tvDate.text = dateFormat(calendarDate)
         setupGoal()
         dailyView()
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
   }

   private fun setupGoal() {
      // 텍스트 초기화
      binding.tvGoal.text = "0 kcal"
      binding.tvRemain.text = "0 kcal"

      // 목표 초기화
      getDailyData = dataManager!!.getDailyData(calendarDate.toString())
      sum = getFoodIntake(requireActivity(), calendarDate.toString())

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
      val getFood = dataManager!!.getFood("breakfast", calendarDate.toString())

      // 이미지뷰 설정
      setupPhotoList("breakfast", calendarDate.toString())

      // 텍스트리스트 설정
      setupTextList(getFood)

      // 영양성분 설정
      setupNutrients(getFood)

      binding.clBtn1.setOnClickListener {
         binding.clBtn1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.foodBtn1))
         binding.clBtn2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.foodBtn2))
         binding.clBtn3.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.foodBtn3))
         binding.clBtn4.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.foodBtn4))

         binding.tvBtn1Title.setTextColor(Color.WHITE)
         binding.tvBtn1Desc.setTextColor(Color.WHITE)
         binding.tvBtn2Title.setTextColor(Color.BLACK)
         binding.tvBtn2Desc.setTextColor(Color.BLACK)
         binding.tvBtn3Title.setTextColor(Color.BLACK)
         binding.tvBtn3Desc.setTextColor(Color.BLACK)
         binding.tvBtn4Title.setTextColor(Color.BLACK)
         binding.tvBtn4Desc.setTextColor(Color.BLACK)

         setupPhotoList("breakfast", calendarDate.toString())
         setupTextList(getFood)
         setupNutrients(getFood)
      }

      binding.clBtn2.setOnClickListener {
         binding.clBtn1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.foodBtn2))
         binding.clBtn2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.foodBtn1))
         binding.clBtn3.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.foodBtn3))
         binding.clBtn4.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.foodBtn4))

         binding.tvBtn2Title.setTextColor(Color.WHITE)
         binding.tvBtn2Desc.setTextColor(Color.WHITE)
         binding.tvBtn1Title.setTextColor(Color.BLACK)
         binding.tvBtn1Desc.setTextColor(Color.BLACK)
         binding.tvBtn3Title.setTextColor(Color.BLACK)
         binding.tvBtn3Desc.setTextColor(Color.BLACK)
         binding.tvBtn4Title.setTextColor(Color.BLACK)
         binding.tvBtn4Desc.setTextColor(Color.BLACK)

         setupPhotoList("lunch", calendarDate.toString())
         val getFood = dataManager!!.getFood("lunch", calendarDate.toString())
         setupTextList(getFood)
         setupNutrients(getFood)
      }

      binding.clBtn3.setOnClickListener {
         binding.clBtn1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.foodBtn2))
         binding.clBtn2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.foodBtn3))
         binding.clBtn3.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.foodBtn1))
         binding.clBtn4.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.foodBtn4))

         binding.tvBtn3Title.setTextColor(Color.WHITE)
         binding.tvBtn3Desc.setTextColor(Color.WHITE)
         binding.tvBtn1Title.setTextColor(Color.BLACK)
         binding.tvBtn1Desc.setTextColor(Color.BLACK)
         binding.tvBtn2Title.setTextColor(Color.BLACK)
         binding.tvBtn2Desc.setTextColor(Color.BLACK)
         binding.tvBtn4Title.setTextColor(Color.BLACK)
         binding.tvBtn4Desc.setTextColor(Color.BLACK)

         setupPhotoList("dinner", calendarDate.toString())
         val getFood = dataManager!!.getFood("dinner", calendarDate.toString())
         setupTextList(getFood)
         setupNutrients(getFood)
      }

      binding.clBtn4.setOnClickListener {
         binding.clBtn1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.foodBtn2))
         binding.clBtn2.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.foodBtn3))
         binding.clBtn3.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.foodBtn4))
         binding.clBtn4.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.foodBtn1))

         binding.tvBtn4Title.setTextColor(Color.WHITE)
         binding.tvBtn4Desc.setTextColor(Color.WHITE)
         binding.tvBtn1Title.setTextColor(Color.BLACK)
         binding.tvBtn1Desc.setTextColor(Color.BLACK)
         binding.tvBtn2Title.setTextColor(Color.BLACK)
         binding.tvBtn2Desc.setTextColor(Color.BLACK)
         binding.tvBtn3Title.setTextColor(Color.BLACK)
         binding.tvBtn3Desc.setTextColor(Color.BLACK)

         setupPhotoList("snack", calendarDate.toString())
         val getFood = dataManager!!.getFood("snack", calendarDate.toString())
         setupTextList(getFood)
         setupNutrients(getFood)
      }
   }

   private fun setupPhotoList(type: String, date: String) {
      imageList.clear()
      adapter?.notifyDataSetChanged()

      imageList = dataManager!!.getImage(type, date)

      if(imageList.size > 0) {
         adapter = PhotoViewAdapter(imageList)

         binding.viewPager.adapter = adapter
         binding.viewPager.offscreenPageLimit = 5
         binding.viewPager.clipToPadding = false
         binding.viewPager.clipChildren = false
         binding.viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

         val transformer = CompositePageTransformer()
         val defaultTranslationX = 0.50f
         val defaultTranslationFactor = 1.2f
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
            val current = binding.viewPager.currentItem
            binding.viewPager.setCurrentItem(current-1, true)
         }

         binding.cvRight.setOnClickListener {
            val current = binding.viewPager.currentItem
            binding.viewPager.setCurrentItem(current+1, true)
         }
      }
   }

   private fun setupTextList(dataList: ArrayList<Food>) {
      val itemList = ArrayList<Text>()
      val divide = dataList.size / 3
      val minus1 = dataList.size - 1
      val minus2 = dataList.size - 2
      var num = 0

      if(dataList.size % 3 == 0) {
         for(i in 0 until divide) {
            itemList.add(Text(
               dataList[num].name, dataList[num].kcal!!.toInt() * dataList[num].amount, dataList[num].unit,
               dataList[num + 1].name, dataList[num + 1].kcal!!.toInt() * dataList[num + 1].amount, dataList[num + 1].unit,
               dataList[num + 2].name, dataList[num + 2].kcal!!.toInt() * dataList[num + 2].amount, dataList[num].unit)
            )
            num += 3
         }
      }else if(dataList.size % 3 == 1) {
         for(i in 0 until divide) {
            itemList.add(Text(
               dataList[num].name, dataList[num].kcal!!.toInt() * dataList[num].amount, dataList[num].unit,
               dataList[num + 1].name, dataList[num + 1].kcal!!.toInt() * dataList[num + 1].amount, dataList[num + 1].unit,
               dataList[num + 2].name, dataList[num + 2].kcal!!.toInt() * dataList[num + 2].amount, dataList[num].unit)
            )
            num += 3
         }
         itemList.add(Text(name1 = dataList[minus1].name, int1 = dataList[minus1].kcal!!.toInt() * dataList[minus1].amount, unit1 = dataList[minus1].unit))
      }else if(dataList.size % 3 == 2) {
         for(i in 0 until divide) {
            itemList.add(Text(
               dataList[num].name, dataList[num].kcal!!.toInt() * dataList[num].amount, dataList[num].unit,
               dataList[num + 1].name, dataList[num + 1].kcal!!.toInt() * dataList[num + 1].amount, dataList[num + 1].unit,
               dataList[num + 2].name, dataList[num + 2].kcal!!.toInt() * dataList[num + 2].amount, dataList[num].unit)
            )
            num += 3
         }
         itemList.add(Text(
            name1 = dataList[minus2].name, int1 = dataList[minus2].kcal!!.toInt() * dataList[minus2].amount, unit1 = dataList[minus2].unit,
            name2 = dataList[minus1].name, int2 = dataList[minus1].kcal!!.toInt() * dataList[minus2].amount, unit2 = dataList[minus1].unit))
      }

      binding.viewpager.adapter = FoodTextAdapter(itemList)
      binding.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
      binding.indicator.setViewPager(binding.viewpager)
   }

   private fun setupNutrients(dataList: ArrayList<Food>) {
      var carbohydrate = 0.0
      var protein = 0.0
      var fat = 0.0

      if(dataList.size > 0) {
         for(i in 0 until dataList.size) {
            carbohydrate += dataList[i].carbohydrate!!.toDouble()
            protein += dataList[i].protein!!.toDouble()
            fat += dataList[i].fat!!.toDouble()
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

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment1(requireActivity(), MainFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}