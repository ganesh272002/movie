package com.example.movieseries.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2
    override fun createFragment(position: Int) = when (position) {
        0 -> MovieFragment()
        else -> SeriesFragment()
    }
}