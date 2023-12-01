package com.makebodywell.bodywell.view.note

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentNoteWriteBinding
import com.makebodywell.bodywell.model.Text
import com.makebodywell.bodywell.util.CalendarUtil
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import java.time.LocalDate

class NoteWriteFragment : Fragment() {
   private var _binding: FragmentNoteWriteBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback

   private var bundle = Bundle()

   private var dataManager: DataManager? = null
   private var getNote = Text()

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentNoteWriteBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      initView()

      return binding.root
   }

   private fun initView() {
      getNote = dataManager!!.getNote(selectedDate.toString())

      bundle.putString("data", "noteData")

      settingData()

      binding.ivBack.setOnClickListener {
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

      binding.tvSave.setOnClickListener {
         if(getNote.name3 == null) {
            dataManager!!.insertNote(Text(name1 = binding.etTitle.text.toString(), name2 = binding.etContent.text.toString(), name3 = selectedDate.toString()))
            Toast.makeText(activity, "저장되었습니다.", Toast.LENGTH_SHORT).show()
         }else {
            dataManager!!.updateNote(Text(name1 = binding.etTitle.text.toString(), name2 = binding.etContent.text.toString(), name3 = selectedDate.toString()))
            Toast.makeText(activity, "수정되었습니다.", Toast.LENGTH_SHORT).show()
         }
         replaceFragment2(requireActivity(), NoteFragment(), bundle)
      }
   }

   private fun settingData() {
      binding.tvCalTitle.text = dateFormat(selectedDate)
      binding.tvNoteDate.text = dateFormat(selectedDate)

      if(getNote.name3 != null) {
         binding.etTitle.setText(getNote.name1)
         binding.etContent.setText(getNote.name2)
      }else {
         binding.etTitle.hint = "제목."
         binding.etContent.hint = "내용입력"
      }
   }

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment2(requireActivity(), NoteFragment(), bundle)
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}