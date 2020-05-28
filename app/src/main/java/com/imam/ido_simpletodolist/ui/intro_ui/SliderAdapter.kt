package com.imam.ido_simpletodolist.ui.intro_ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.imam.ido_simpletodolist.R

class SliderAdapter(private var mContext: Context, private var mListScreen: List<SliderItem>) : PagerAdapter() {
    override fun getCount(): Int {
        return mListScreen.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layoutScreen: View = inflater.inflate(R.layout.intro, container, false)
        val slideImageView =
            layoutScreen.findViewById<View>(R.id.iconslider) as ImageView
        val slideHeading =
            layoutScreen.findViewById<View>(R.id.headings) as TextView
        slideImageView.setImageResource(mListScreen[position].screenImg)
        slideHeading.text = mListScreen[position].title
        container.addView(layoutScreen)
        return layoutScreen
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

}