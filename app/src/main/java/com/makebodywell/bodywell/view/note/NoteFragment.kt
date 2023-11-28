package com.makebodywell.bodywell.view.note

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.makebodywell.bodywell.databinding.FragmentNoteBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1

class NoteFragment : Fragment() {
   private var _binding: FragmentNoteBinding? = null
   private val binding get() = _binding!!

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentNoteBinding.inflate(layoutInflater)

      binding.ivWrite.setOnClickListener {
         replaceFragment1(requireActivity(), NoteWriteFragment())
      }

      return binding.root
   }
}