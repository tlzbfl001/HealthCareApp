package com.makebodywell.bodywell.view.note

<<<<<<< HEAD
=======
import android.content.Context
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
<<<<<<< HEAD
=======
import androidx.activity.OnBackPressedCallback
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentNoteWriteBinding
import com.makebodywell.bodywell.model.Text
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2

class NoteWriteFragment : Fragment() {
   private var _binding: FragmentNoteWriteBinding? = null
   private val binding get() = _binding!!

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
         if(getNote.string3 == null) {
            dataManager!!.insertNote(Text(string1 = binding.etTitle.text.toString(), string2 = binding.etContent.text.toString(), string3 = selectedDate.toString()))
            Toast.makeText(activity, "저장되었습니다.", Toast.LENGTH_SHORT).show()
         }else {
            dataManager!!.updateNote(Text(string1 = binding.etTitle.text.toString(), string2 = binding.etContent.text.toString(), string3 = selectedDate.toString()))
            Toast.makeText(activity, "수정되었습니다.", Toast.LENGTH_SHORT).show()
         }
         replaceFragment2(requireActivity(), NoteFragment(), bundle)
      }
   }

   private fun settingData() {
      binding.tvCalTitle.text = dateFormat(selectedDate)
      binding.tvNoteDate.text = dateFormat(selectedDate)

      if(getNote.string3 != null) {
         binding.etTitle.setText(getNote.string1)
         binding.etContent.setText(getNote.string2)
      }else {
         binding.etTitle.hint = "제목."
         binding.etContent.hint = "내용입력"
      }
   }
}