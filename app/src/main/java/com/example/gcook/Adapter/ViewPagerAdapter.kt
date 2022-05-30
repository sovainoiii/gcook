package com.example.gcook.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.gcook.UI.Home.Fragment.TabLayout.FavotiteFragment
import com.example.gcook.UI.Home.Fragment.TabLayout.MyFoodFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {
                MyFoodFragment()
            }
            1 -> {
                FavotiteFragment()
            }
            else -> {
                MyFoodFragment()
            }
        }
    }
}