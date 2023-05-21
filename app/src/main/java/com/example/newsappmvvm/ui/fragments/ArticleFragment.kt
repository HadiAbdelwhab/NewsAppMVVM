package com.example.newsappmvvm.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.newsappmvvm.databinding.FragmentArticleBinding
import com.example.newsappmvvm.ui.NewsActivity
import com.example.newsappmvvm.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar


class ArticleFragment : Fragment() {
    private lateinit var newsViewModel: NewsViewModel
    //val args:ArticleFragmentArgs
    private val args: ArticleFragmentArgs by navArgs()

    val TAG = "ArticleFragment"
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsViewModel = (activity as NewsActivity).newsViewModel

        val article = args.article
        binding.webView.apply {
            webViewClient= WebViewClient()
            article.url?.let { loadUrl(it) }
        }
        article.url?.let { Log.d(TAG, it) }
        binding.fab.setOnClickListener {
            newsViewModel.saveArticle(article)
            Snackbar.make(view,"Article saved !! ",Snackbar.LENGTH_SHORT).show()
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}