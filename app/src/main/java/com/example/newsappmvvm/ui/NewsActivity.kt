package com.example.newsappmvvm.ui

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.newsappmvvm.R
import com.example.newsappmvvm.database.ArticleDatabase
import com.example.newsappmvvm.databinding.ActivityNewsBinding
import com.example.newsappmvvm.repository.NewsRepository


class NewsActivity : AppCompatActivity() {

    lateinit var newsViewModel: NewsViewModel

    private lateinit var binding: ActivityNewsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val repository = NewsRepository(ArticleDatabase.invoke(this))
        val newsViewModelProviderFactory = NewsViewModelProviderFactory(application,repository)
        newsViewModel = ViewModelProvider(this, newsViewModelProviderFactory)[NewsViewModel::class.java]

        setNavController()



    }

    private fun setNavController() {
        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        navView.setupWithNavController(navController)
    }


}


