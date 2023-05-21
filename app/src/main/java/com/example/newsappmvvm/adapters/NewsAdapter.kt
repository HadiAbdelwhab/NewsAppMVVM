package com.example.newsappmvvm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsappmvvm.models.Article
import com.example.newsappmvvm.R

class NewsAdapter() : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val articleImageView: ImageView = itemView.findViewById(R.id.top_headlines_ImageView)
        val articleTitle: TextView = itemView.findViewById(R.id.top_headlines_title_text_view)
        val articleSource: TextView = itemView.findViewById(R.id.top_headlines_source_text_view)

    }

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
    val diifer = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_item_breaking_news, parent, false)
        return ArticleViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return diifer.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = diifer.currentList[position]

        Glide.with(holder.itemView).load(article.urlToImage).into(holder.articleImageView)
        holder.articleSource.text = article.source?.name
        holder.articleTitle.text = article.title


        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(article)
        }
    }


    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: ((Article) -> Unit)?) {
        onItemClickListener = listener
    }

}