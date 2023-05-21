package com.example.newsappmvvm.repository


import com.example.newsappmvvm.api.RetrofitInstance
import com.example.newsappmvvm.database.ArticleDatabase
import com.example.newsappmvvm.models.Article

class NewsRepository(
    val database: ArticleDatabase
) {

    suspend fun getTopHeadlines(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getTopHeadlines(countryCode, pageNumber)

    suspend fun searchForNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun upsert(article: Article) =
        database.getArticleDao().upsert(article)

    suspend fun deleteArticle(article: Article) =
        database.getArticleDao().deleteArticle(article)

    fun getSavedNews() = database.getArticleDao().getAllArticles()

}