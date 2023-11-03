package com.example.lostnfound

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import java.util.*

class LostFoundItemPagerActivity : AppCompatActivity() {
    companion object {
        private const val EXTRA_ITEM_ID = "com.example.lostnfound.item_id"

        fun newIntent(packageContext: Context, itemId: UUID): Intent {
            val intent = Intent(packageContext, LostFoundItemPagerActivity::class.java)
            intent.putExtra(EXTRA_ITEM_ID, itemId)
            return intent
        }
    }

    private lateinit var mViewPager: ViewPager
    private lateinit var mItems: List<LostFoundItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_viewpager)


        val itemId: UUID = intent.getSerializableExtra(EXTRA_ITEM_ID) as UUID

        mViewPager = findViewById(R.id.item_view_pager)

        mItems = LostFoundItemLab.get(this).getItems()
        val fragmentManager: FragmentManager = supportFragmentManager
        mViewPager.adapter = object : FragmentStatePagerAdapter(fragmentManager) {
            override fun getItem(position: Int): Fragment {
                val item: LostFoundItem = mItems[position]
                return LostFoundItemFragment.newInstance(item.mId)
            }

            override fun getCount(): Int {
                return mItems.size
            }
        }

        for (i in mItems.indices) {
            if (mItems[i].mId == itemId) {
                mViewPager.currentItem = i
                break
            }
        }
    }
}
