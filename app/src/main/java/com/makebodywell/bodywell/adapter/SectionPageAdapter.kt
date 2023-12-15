package com.makebodywell.bodywell.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SectionPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
   private val fragmentList = ArrayList<Fragment>()
   private val fragmentTitleList = ArrayList<String>()

   fun addFragment(fragment: Fragment, title: String) {
      fragmentList.add(fragment)
      fragmentTitleList.add(title)
   }

   override fun getPageTitle(position: Int): CharSequence? {
      return fragmentTitleList[position]
   }

   override fun getCount(): Int {
      return fragmentList.size
   }

   override fun getItem(position: Int): Fragment {
      return fragmentList[position]
   }
}