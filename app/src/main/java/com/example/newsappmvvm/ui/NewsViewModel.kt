package com.example.newsappmvvm.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.*
import android.os.Build
import android.provider.ContactsContract.CommonDataKinds.Email.TYPE_MOBILE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsappmvvm.NewsApplication
import com.example.newsappmvvm.models.NewsResponse
import com.example.newsappmvvm.models.Article
import com.example.newsappmvvm.repository.NewsRepository
import com.example.newsappmvvm.util.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class NewsViewModel(
    val app:Application,
    private val newsRepository: NewsRepository
) : AndroidViewModel(app) {

    val topHeadlinesLivaData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var topHeadlinePage = 1
    var newsResponse: NewsResponse? = null


    init {
        getTopHeadlines("us")
    }

     fun getTopHeadlines(countryCode: String) = viewModelScope.launch {
        safeTopHeadlinesCall(countryCode)
    }

    private fun handleTopHeadlinesResponse(response: Response<NewsResponse>): Resource<NewsResponse> {

        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                topHeadlinePage++
                if (newsResponse == null) {
                    newsResponse = resultResponse
                } else {
                    val oldArticles = newsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(newsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


    val searchNewsLivaData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }


    private fun handleSearchForNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {

        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())

    }


        fun saveArticle(article: Article) = viewModelScope.launch {
            newsRepository.upsert(article)
        }


        fun getSavedNews() = newsRepository.getSavedNews()


        fun deleteArticle(article: Article) = viewModelScope.launch {
            newsRepository.deleteArticle(article)
        }


    private suspend fun safeSearchNewsCall(searchQuery: String) {
        //newSearchQuery = searchQuery
        searchNewsLivaData.postValue(Resource.Loading())
        try {
            if(isConnected()) {
                val response = newsRepository.searchForNews(searchQuery, searchNewsPage)
                searchNewsLivaData.postValue(handleSearchForNewsResponse(response))
            } else {
                searchNewsLivaData.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> searchNewsLivaData.postValue(Resource.Error("Network Failure"))
                else -> searchNewsLivaData.postValue(Resource.Error("Conversion Error"))
            }
        }
    }


    private suspend fun safeTopHeadlinesCall(countryCode: String) {
        topHeadlinesLivaData.postValue(Resource.Loading())
        try {
            if(isConnected()) {
                val response = newsRepository.getTopHeadlines(countryCode, topHeadlinePage)
                topHeadlinesLivaData.postValue(handleTopHeadlinesResponse(response))
            } else {
                topHeadlinesLivaData.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> topHeadlinesLivaData.postValue(Resource.Error("Network Failure"))
                else -> topHeadlinesLivaData.postValue(Resource.Error("Conversion Error"))
            }
        }
    }



    private fun isConnected(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }


}
