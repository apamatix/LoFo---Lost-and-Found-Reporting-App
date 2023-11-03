package com.example.lostnfound

import androidx.fragment.app.Fragment

class LostFoundItemListActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return LostFoundItemListFragment()
    }
}