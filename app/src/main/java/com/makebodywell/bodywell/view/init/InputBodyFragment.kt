package com.makebodywell.bodywell.view.init

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.makebodywell.bodywell.UpdateUserProfileMutation
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_USER
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentInputBodyBinding
import com.makebodywell.bodywell.model.User
import com.makebodywell.bodywell.type.Gender
import com.makebodywell.bodywell.type.UpdateUserProfileInput
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceInputFragment
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceInputFragment2
import kotlinx.coroutines.launch

class InputBodyFragment : Fragment() {
   private var _binding: FragmentInputBodyBinding? = null
   private val binding get() = _binding!!

   private val bundle = Bundle()
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

      val user = arguments?.getParcelable<User>("user")!!
      Log.d(TAG, "user: $user")

      binding.tvSkip.setOnClickListener {
         replaceInputFragment(requireActivity(), InputGoalFragment())
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
         var height = 0.0
         var weight = 0.0

         if(binding.etHeight.text.toString() != "") {
            height = binding.etHeight.text.toString().toDouble()
         }

         if(binding.etWeight.text.toString() != "") {
            weight = binding.etWeight.text.toString().toDouble()
         }

         val getToken = dataManager!!.getToken(user.id)

         lifecycleScope.launch{
            val response = apolloClient.mutation(UpdateUserProfileMutation(
               userId = user.userId.toString(), UpdateUserProfileInput(
                  gender = Optional.present(gender), height = Optional.present(height), weight = Optional.present(weight)
            ))).addHttpHeader(
               "Authorization",
               "Bearer ${getToken.accessToken}"
            ).execute()

            Log.d(TAG, "updateUserProfile: ${response.data?.updateUserProfile}")

            // 회원 정보 DB 에 저장
            if(response.data!!.updateUserProfile.success) {
               dataManager?.updateDouble(TABLE_USER, "height", height, user.id)
               dataManager?.updateDouble(TABLE_USER, "weight", weight, user.id)

               val getUser = dataManager!!.getUser()
               bundle.putParcelable("user", getUser)
               replaceInputFragment2(requireActivity(), InputBodyFragment(), bundle)
            }else {
               Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
            }
         }

         replaceInputFragment(requireActivity(), InputGoalFragment())
      }

      return binding.root
   }
}