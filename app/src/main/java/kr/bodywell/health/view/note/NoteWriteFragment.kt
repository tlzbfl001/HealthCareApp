package kr.bodywell.health.view.note

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.github.f4b6a3.uuid.UuidCreator
import kotlinx.coroutines.launch
import kr.bodywell.health.R
import kr.bodywell.health.databinding.FragmentNoteWriteBinding
import kr.bodywell.health.model.Constant.ANGRY
import kr.bodywell.health.model.Constant.EXCITED
import kr.bodywell.health.model.Constant.HAPPY
import kr.bodywell.health.model.Constant.NOTES
import kr.bodywell.health.model.Constant.PEACEFUL
import kr.bodywell.health.model.Constant.SAD
import kr.bodywell.health.model.Note
import kr.bodywell.health.util.CalendarUtil.dateFormat
import kr.bodywell.health.util.CalendarUtil.selectedDate
import kr.bodywell.health.util.CustomUtil.hideKeyboard
import kr.bodywell.health.util.CustomUtil.replaceFragment2
import kr.bodywell.health.util.CustomUtil.replaceFragment4
import kr.bodywell.health.util.MyApp.Companion.powerSync
import java.time.LocalDateTime

class NoteWriteFragment : Fragment() {
   private var _binding: FragmentNoteWriteBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private var getNote = Note()
   private var bundle = Bundle()
   private var emotion = HAPPY

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment4(requireActivity().supportFragmentManager, NoteFragment(), bundle)
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentNoteWriteBinding.inflate(layoutInflater)

      requireActivity().window?.apply {
         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }

      bundle.putString("data", NOTES)

      binding.mainLayout.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

      binding.linear.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

      binding.clBack.setOnClickListener {
         replaceFragment4(requireActivity().supportFragmentManager, NoteFragment(), bundle)
      }

      binding.clPrev.setOnClickListener {
         selectedDate = selectedDate.minusDays(1)
         setDailyView()
      }

      binding.clNext.setOnClickListener {
         selectedDate = selectedDate.plusDays(1)
         setDailyView()
      }

      binding.ivFace1.setOnClickListener {
         binding.ivFace.setImageResource(R.drawable.face1)
         emotion = HAPPY
      }

      binding.ivFace2.setOnClickListener {
         binding.ivFace.setImageResource(R.drawable.face2)
         emotion = PEACEFUL
      }

      binding.ivFace3.setOnClickListener {
         binding.ivFace.setImageResource(R.drawable.face3)
         emotion = EXCITED
      }

      binding.ivFace4.setOnClickListener {
         binding.ivFace.setImageResource(R.drawable.face4)
         emotion = SAD
      }

      binding.ivFace5.setOnClickListener {
         binding.ivFace.setImageResource(R.drawable.face5)
         emotion = ANGRY
      }

      binding.cvSave.setOnClickListener {
         if(binding.etTitle.text.toString().contains("'") || binding.etContent.text.toString().contains("'")) {
            Toast.makeText(activity, "특수문자 '는 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
         }else if(binding.etTitle.text.toString().trim() == "") {
            Toast.makeText(activity, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else if(binding.etContent.text.toString().trim() == "") {
            Toast.makeText(activity, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            lifecycleScope.launch {
               getNote = powerSync.getNote(selectedDate.toString())

               if(getNote.id == "") {
                  val uuid = UuidCreator.getTimeOrderedEpoch()
                  powerSync.insertNote(Note(id = uuid.toString(), title = binding.etTitle.text.toString(), content = binding.etContent.text.toString(),
                     emotion = emotion, date = selectedDate.toString(), createdAt = LocalDateTime.now().toString(), updatedAt = LocalDateTime.now().toString()))
                  Toast.makeText(activity, "저장되었습니다.", Toast.LENGTH_SHORT).show()
               }else {
                  powerSync.updateNote(Note(title = binding.etTitle.text.toString(), content = binding.etContent.text.toString(), emotion = emotion, date = selectedDate.toString()))
                  Toast.makeText(activity, "수정되었습니다.", Toast.LENGTH_SHORT).show()
               }
            }

            replaceFragment2(requireActivity().supportFragmentManager, NoteFragment(), bundle)
         }
      }

      setDailyView()

      return binding.root
   }

   private fun setDailyView() {
      binding.tvCalTitle.text = dateFormat(selectedDate)

      lifecycleScope.launch {
         getNote = powerSync.getNote(selectedDate.toString())

         if(getNote.title != "") {
            binding.etTitle.setText(getNote.title)
            binding.etContent.setText(getNote.content)
            when(getNote.emotion) {
               HAPPY -> binding.ivFace.setImageResource(R.drawable.face1)
               PEACEFUL -> binding.ivFace.setImageResource(R.drawable.face2)
               EXCITED -> binding.ivFace.setImageResource(R.drawable.face3)
               SAD -> binding.ivFace.setImageResource(R.drawable.face4)
               ANGRY -> binding.ivFace.setImageResource(R.drawable.face5)
            }
         }else {
            binding.etTitle.setText("")
            binding.etContent.setText("")
            binding.etTitle.hint = "제목."
            binding.etContent.hint = "내용입력"
            binding.ivFace.setImageResource(R.drawable.face1)
         }
      }
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}