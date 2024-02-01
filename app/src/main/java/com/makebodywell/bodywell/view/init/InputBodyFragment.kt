package com.makebodywell.bodywell.view.init

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.makebodywell.bodywell.UpdateUserProfileMutation
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_USER
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentInputBodyBinding
import com.makebodywell.bodywell.type.Gender
import com.makebodywell.bodywell.type.UpdateUserProfileInput
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceLoginFragment1
import com.makebodywell.bodywell.util.MyApp
import kotlinx.coroutines.launch

class InputBodyFragment : Fragment() {
   private var _binding: FragmentInputBodyBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null
   private var gender = Gender.MALE

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentInputBodyBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      val apolloClient = ApolloClient.Builder().serverUrl("https://api.bodywell.dev/graphql").build()

      val getUser = dataManager!!.getUser(MyApp.prefs.getId())

      binding.ivBack.setOnClickListener {
         replaceLoginFragment1(requireActivity(), InputInfoFragment())
      }

      binding.tvSkip.setOnClickListener {
         replaceLoginFragment1(requireActivity(), InputGoalFragment())
      }

      binding.cvMan.setOnClickListener {
         binding.cvMan.setCardBackgroundColor(Color.parseColor("#9F98FF"))
         binding.ivMan.imageTintList = ColorStateList.valueOf(Color.WHITE)
         binding.tvMan.setTextColor(Color.WHITE)
         binding.cvWoman.setCardBackgroundColor(Color.parseColor("#EEEEEE"))
         binding.ivWoman.imageTintList = ColorStateList.valueOf(Color.parseColor("#aaaaaa"))
         binding.tvWoman.setTextColor(Color.BLACK)

         gender = Gender.MALE
      }

      binding.cvWoman.setOnClickListener {
         binding.cvMan.setCardBackgroundColor(Color.parseColor("#EEEEEE"))
         binding.ivMan.imageTintList = ColorStateList.valueOf(Color.parseColor("#aaaaaa"))
         binding.tvMan.setTextColor(Color.BLACK)
         binding.cvWoman.setCardBackgroundColor(Color.parseColor("#9F98FF"))
         binding.ivWoman.imageTintList = ColorStateList.valueOf(Color.WHITE)
         binding.tvWoman.setTextColor(Color.WHITE)

         gender = Gender.FEMALE
      }

      binding.cvContinue.setOnClickListener {
         val getToken = dataManager!!.getToken(getUser.id)
         var height = 0.0
         var weight = 0.0

         if(binding.etHeight.text.toString() != "") {
            height = binding.etHeight.text.toString().toDouble()
         }

         if(binding.etWeight.text.toString() != "") {
            weight = binding.etWeight.text.toString().toDouble()
         }

         lifecycleScope.launch{
            val response = apolloClient.mutation(UpdateUserProfileMutation(
               userId = getUser.userId.toString(), UpdateUserProfileInput(gender = Optional.present(gender), height = Optional.present(height), weight = Optional.present(weight))
            )).addHttpHeader(
               "Authorization",
               "Bearer ${getToken.accessToken}"
            ).execute()

            dataManager?.updateString(TABLE_USER, "gender", gender.toString(), getUser.id)
            dataManager?.updateDouble(TABLE_USER, "height", height, getUser.id)
            dataManager?.updateDouble(TABLE_USER, "weight", weight, getUser.id)

            replaceLoginFragment1(requireActivity(), InputGoalFragment())
         }
      }

      return binding.root
   }
}