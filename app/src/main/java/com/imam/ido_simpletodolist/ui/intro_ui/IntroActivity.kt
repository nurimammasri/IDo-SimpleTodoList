package com.imam.ido_simpletodolist.ui.intro_ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
import com.imam.ido_simpletodolist.R
import com.imam.ido_simpletodolist.ui.main.MainActivity
import java.util.*

class IntroActivity : AppCompatActivity() {
    lateinit var footer: LinearLayout
    lateinit var header: ImageView
    lateinit var relativeLayout: RelativeLayout
    lateinit var bgHeader: IntArray
    lateinit var bgFooter: IntArray
    lateinit var bgIntro: IntArray
    lateinit var sliderAdapter: SliderAdapter
    lateinit var desc: TextView
    private lateinit var fromBottom: Animation
    private lateinit var fromTop: Animation
    private lateinit var fadeIn: Animation
    private lateinit var tabIndicator: TabLayout
    private lateinit var btnGetStarted: Button
    private lateinit var skip: Button
    private lateinit var boom: LottieAnimationView
    private lateinit var btnNext: ImageButton
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intro_view_pager)

        //untuk membuat layar full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
        }


        //when this activity is about to be launch we need to check if its opened before or not
        if (restorePrefData()) {
            val introKota = Intent(applicationContext, MainActivity::class.java)
            startActivity(introKota)
            finish()
        }

        /*deklasrasi di header dan footer*/
        footer = findViewById<View>(R.id.footer) as LinearLayout
        header = findViewById<View>(R.id.toolbar) as ImageView
        relativeLayout = findViewById(R.id.relative_layout)

        //Deklarasi Animation dan set Animation
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.frombottom)
        fromTop = AnimationUtils.loadAnimation(this, R.anim.fromtop)
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)


        //ini untuk button next
        btnNext = findViewById(R.id.btn_next)
        // ini tab indikator buble
        tabIndicator = findViewById(R.id.tab_indikator)
        //ini Get Started
        btnGetStarted = findViewById(R.id.btn_getStar)
        btnGetStarted.visibility = View.INVISIBLE
        //ini Skip
        skip = findViewById(R.id.skip)
        //ini Animation Lottie Boom
        boom = findViewById(R.id.boom_lottie)
        boom.visibility = View.INVISIBLE
        header.animation = fromTop
        footer.animation = fromBottom
        //Ini desc
        desc = findViewById(R.id.description)


        //fill list screen
        val mList: MutableList<SliderItem> = ArrayList<SliderItem>()
        mList.add(SliderItem("Todo List", R.drawable.slider1))
        mList.add(SliderItem("Create Your Todo List", R.drawable.slider2))
        mList.add(SliderItem("All Your Schedule", R.drawable.slider3))

        // setup view pager
        val screenPager = findViewById<ViewPager>(R.id.view_pager)
        sliderAdapter = SliderAdapter(this, mList)
        screenPager.adapter = sliderAdapter

        //setup tablayout dengan tab indikator
        tabIndicator.setupWithViewPager(screenPager)


        //untuk mengatur behaviour button next
        btnNext.setOnClickListener{
            position = screenPager.currentItem
            if (position < mList.size) {
                position++
                screenPager.currentItem = position
            }
            if (position == mList.size - 1) {
                //ketika mencapai screen terakhr
                loadLastScreen()
            }
        }

        //Array background Per Component
        val backgroundFooter = intArrayOf(
            R.drawable.footer_intro1,
            R.drawable.footer_intro2,
            R.drawable.footer_intro3
        )
        bgFooter = backgroundFooter
        val backgroundHeader = intArrayOf(
            R.drawable.header_intro1,
            R.drawable.header_intro2,
            R.drawable.header_intro3
        )
        bgHeader = backgroundHeader
        val backgroundIntro = intArrayOf(
            R.drawable.bg_intro1,
            R.drawable.bg_intro2,
            R.drawable.bg_intro3
        )
        bgIntro = backgroundIntro
        val description = arrayOf(
            "Have a lot of schedules and confused to arrange them?\nMake your todo list!",
            "Set the schedule for your todo list as you wish",
            "Finally! Your schedule is well organized\nwith IDo- SimpleTodoList"
        )

        //tablayout add change listener
        tabIndicator.addOnTabSelectedListener(object : BaseOnTabSelectedListener<TabLayout.Tab> {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.position == mList.size - 1) {
                    loadLastScreen()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        screenPager.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position < sliderAdapter.count - 1 && position < bgFooter.size - 1 && position < bgHeader.size - 1 && position < bgIntro.size - 1) {
                    relativeLayout.setBackgroundResource(backgroundIntro[position])
                    header.setImageResource(backgroundHeader[position])
                    footer.setBackgroundResource(backgroundFooter[position])
                    desc.text = description[position]
                } else {
                    relativeLayout.setBackgroundResource(backgroundIntro[backgroundIntro.size - 1])
                    header.setImageResource(backgroundHeader[backgroundHeader.size - 1])
                    footer.setBackgroundResource(backgroundFooter[backgroundFooter.size - 1])
                    desc.text = description[description.size - 1]
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        //Skip Button Click Listener
        skip.setOnClickListener{
            val intent = Intent(this@IntroActivity, MainActivity::class.java)
            startActivity(intent)
            savePrefData()
            finish()
        }

        //Get Started Button Click Listener
        btnGetStarted.setOnClickListener{
            val intent = Intent(this@IntroActivity, MainActivity::class.java)
            startActivity(intent)

            savePrefData()
            finish()
        }
    }

    private fun restorePrefData(): Boolean {
        val pref = applicationContext.getSharedPreferences(
            "myPrefs",
            Context.MODE_PRIVATE
        )
        return pref.getBoolean("isIntroOpened", false)
    }

    private fun savePrefData() {
        val pref = applicationContext.getSharedPreferences(
            "myPrefs",
            Context.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putBoolean("isIntroOpened", true)
        editor.apply()
    }

    private fun loadLastScreen() {
        btnGetStarted.visibility = View.VISIBLE
        btnNext.visibility = View.INVISIBLE
        skip.visibility = View.INVISIBLE
        tabIndicator.visibility = View.INVISIBLE
        boom.visibility = View.VISIBLE
        btnGetStarted.animation = fadeIn
        boom.animation = fadeIn
    }
}