package com.example.dermamedicalapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.SearchView
import com.example.dermamedicalapplication.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        val posts = arrayOf("Vì sao phải uống nước", "Top 5 bài thuốc dân gian trị nấm ngoài da", "Mật ong có tác dụng gì cho làn da của bạn ?", "Thức khuya có làm ảnh hưởng đến sức khỏe ?")
        val postAdapter: ArrayAdapter<String> = ArrayAdapter (
            this, android.R.layout.simple_list_item_1,
            posts
        )

        binding.searchResultLv.adapter = postAdapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                binding.searchView.clearFocus()
                if (posts.contains(p0)) {
                    postAdapter.filter.filter(p0)
                }

                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                postAdapter.filter.filter(p0)
                return false
            }

        })
    }
}