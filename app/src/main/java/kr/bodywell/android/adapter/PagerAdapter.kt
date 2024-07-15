package kr.bodywell.android.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter

class PagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {
   private val fragmentList = ArrayList<Fragment>()
   private val fragmentTitle = ArrayList<String>()

   override fun getCount(): Int = fragmentList.size
   override fun getItem(position: Int): Fragment = fragmentList[position]
   override fun getPageTitle(position: Int): CharSequence = fragmentTitle[position]
   fun add(fragment: Fragment, title: String) {
      fragmentList.add(fragment)
      fragmentTitle.add(title)
   }
}