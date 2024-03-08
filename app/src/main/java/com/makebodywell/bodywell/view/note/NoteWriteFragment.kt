package com.makebodywell.bodywell.view.note

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentNoteWriteBinding
import com.makebodywell.bodywell.model.Note
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.hideKeyboard
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.view.home.MainActivity
import com.makebodywell.bodywell.view.home.food.FoodRecord1Fragment

class NoteWriteFragment : Fragment(), MainActivity.OnBackPressedListener {
   private var _binding: FragmentNoteWriteBinding? = null
   private val binding get() = _binding!!

   private var bundle = Bundle()
   private var dataManager: DataManager? = null
   private var status = 1

   override fun onAttach(context: Context) {
      super.onAttach(context)
      (context as MainActivity).setOnBackPressedListener(this)
   }

   @SuppressLint("DiscouragedApi", "InternalInsetResource", "ClickableViewAccessibility")
   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentNoteWriteBinding.inflate(layoutInflater)

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }

      dataManager = DataManager(activity)
      dataManager!!.open()

      val getNote = dataManager!!.getNote(selectedDate.toString())
      bundle.putString("data", "note")

      binding.mainLayout.setOnTouchListener { view, motionEvent ->
         hideKeyboard(requireActivity())
         true
      }

      binding.clBack.setOnClickListener {
         replaceFragment2(requireActivity(), NoteFragment(), bundle)
      }

      binding.clPrev.setOnClickListener {
         selectedDate = selectedDate.minusDays(1)
         settingData()
      }

      binding.clNext.setOnClickListener {
         selectedDate = selectedDate.plusDays(1)
         settingData()
      }

      binding.ivFace1.setOnClickListener {
         binding.ivFace.setImageResource(R.drawable.face1)
         status = 1
      }

      binding.ivFace2.setOnClickListener {
         binding.ivFace.setImageResource(R.drawable.face2)
         status = 2
      }

      binding.ivFace3.setOnClickListener {
         binding.ivFace.setImageResource(R.drawable.face3)
         status = 3
      }

      binding.ivFace4.setOnClickListener {
         binding.ivFace.setImageResource(R.drawable.face4)
         status = 4
      }

      binding.ivFace5.setOnClickListener {
         binding.ivFace.setImageResource(R.drawable.face5)
         status = 5
      }

      binding.cvSave.setOnClickListener {
         if(binding.etTitle.text.toString().contains("'") || binding.etContent.text.toString().contains("'")) {
            Toast.makeText(activity, "특수문자 '는 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
         }else {
            if(getNote.regDate == "") {
               dataManager!!.insertNote(Note(title = binding.etTitle.text.toString(), content = binding.etContent.text.toString(),
                  status = status, regDate = selectedDate.toString()))
               Toast.makeText(activity, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            }else {
               dataManager!!.updateNote(Note(title = binding.etTitle.text.toString(), content = binding.etContent.text.toString(),
                  status = status, regDate = selectedDate.toString()))
               Toast.makeText(activity, "수정되었습니다.", Toast.LENGTH_SHORT).show()
            }

            replaceFragment2(requireActivity(), NoteFragment(), bundle)
         }
      }

      settingData()

      return binding.root
   }

   private fun settingData() {
      binding.tvCalTitle.text = dateFormat(selectedDate)

      val getNote = dataManager!!.getNote(selectedDate.toString())
      if(getNote.regDate != "") {
         binding.etTitle.setText(getNote.title)
         binding.etContent.setText(getNote.content)
         when(getNote.status) {
            1 -> binding.ivFace.setImageResource(R.drawable.face1)
            2 -> binding.ivFace.setImageResource(R.drawable.face2)
            3 -> binding.ivFace.setImageResource(R.drawable.face3)
            4 -> binding.ivFace.setImageResource(R.drawable.face4)
            5 -> binding.ivFace.setImageResource(R.drawable.face5)
         }
      }else {
         binding.etTitle.setText("")
         binding.etContent.setText("")
         binding.etTitle.hint = "제목."
         binding.etContent.hint = "내용입력"
         binding.ivFace.setImageResource(R.drawable.face1)
      }
   }

   override fun onBackPressed() {
      val activity = activity as MainActivity?
      activity!!.setOnBackPressedListener(null)
      replaceFragment2(requireActivity(), NoteFragment(), bundle)
   }
}