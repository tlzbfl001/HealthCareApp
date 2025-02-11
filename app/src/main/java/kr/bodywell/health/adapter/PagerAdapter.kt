package kr.bodywell.health.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(
   private val fragmentList: ArrayList<Fragment>,
   container: FragmentActivity
): FragmentStateAdapter(container.supportFragmentManager, container.lifecycle) {
   override fun getItemCount(): Int = fragmentList.count()
   override fun createFragment(position: Int): Fragment = fragmentList[position]
}