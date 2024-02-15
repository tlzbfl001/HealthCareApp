package com.makebodywell.bodywell.view.note

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentNoteWriteBinding
import com.makebodywell.bodywell.model.Item
import com.makebodywell.bodywell.model.Note
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.util.MyApp

class NoteWriteFragment : Fragment() {
   private var _binding: FragmentNoteWriteBinding? = null
   private val binding get() = _binding!!

   private var bundle = Bundle()

   private var dataManager: DataManager? = null

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

      val getNote = dataManager!!.getNote(MyApp.prefs.getId(), selectedDate.toString())

      bundle.putString("data", "note")

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

      binding.cvSave.setOnClickListener {
         if(getNote.string3 == "") {
            dataManager!!.insertNote(Note(userId = MyApp.prefs.getId(), title = binding.etTitle.text.toString(), content = binding.etContent.text.toString(), regDate = selectedDate.toString()))
            Toast.makeText(activity, "저장되었습니다.", Toast.LENGTH_SHORT).show()
         }else {
            dataManager!!.updateNote(Note(userId = MyApp.prefs.getId(), title = binding.etTitle.text.toString(), content = binding.etContent.text.toString(), regDate = selectedDate.toString()))
            Toast.makeText(activity, "수정되었습니다.", Toast.LENGTH_SHORT).show()
         }
         replaceFragment2(requireActivity(), NoteFragment(), bundle)
      }

      settingData()

      return binding.root
   }

   private fun settingData() {
      binding.tvCalTitle.text = dateFormat(selectedDate)

      val getNote = dataManager!!.getNote(MyApp.prefs.getId(), selectedDate.toString())
      if(getNote.int1 != 0) {
         binding.etTitle.setText(getNote.string1)
         binding.etContent.setText(getNote.string2)
      }else {
         binding.etTitle.setText("")
         binding.etContent.setText("")
         binding.etTitle.hint = "제목."
         binding.etContent.hint = "내용입력"
      }
   }
}