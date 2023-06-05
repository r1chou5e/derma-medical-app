package com.example.dermamedicalapplication

import com.example.dermamedicalapplication.fragments.AdminPostAdapter
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.dermamedicalapplication.databinding.ActivityAdminPostBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth


class AdminPostActivity : AppCompatActivity() {
    private lateinit var binding :ActivityAdminPostBinding
    lateinit var view: ViewPager2
    lateinit var adapter: AdminPostAdapter
    val firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.adminNav.selectedItemId = R.id.post

        binding.adminNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.user -> {
                    startActivity(Intent(this, AdminUserActivity::class.java))
                    overridePendingTransition(0, 0)
                }
            }
            true
        }
        adapter = AdminPostAdapter(supportFragmentManager, lifecycle)
        view = binding.view
        view.adapter = adapter



        binding.postTab.addTab( binding.postTab.newTab().setText("Bài viết mới"))
        binding.postTab.addTab( binding.postTab.newTab().setText("Bài viết đã duyệt"))
        binding.postTab.addTab( binding.postTab.newTab().setText("Bài viết đã từ chối"))

        binding.postTab.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener
        {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(tab!= null)
                {
                    view.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        view.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback()
        {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.postTab.selectTab(binding.postTab.getTabAt(position))
            }
        })

        binding.backBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }


}




