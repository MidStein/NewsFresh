package com.example.newsfresh

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.memeshare.MySingleton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NewsItemClicked{
    private lateinit var mAdapter: NewsListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.layoutManager = LinearLayoutManager(this)
        fetchData("")
        mAdapter = NewsListAdapter(this)
        recyclerView.adapter = mAdapter

        ArrayAdapter.createFromResource(
            this,
            R.array.categories_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        }
        categorySpinner.onItemSelectedListener = CategoriesSpinnerClass()

        ArrayAdapter.createFromResource(
            this,
            R.array.sources_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sourceSpinner.adapter = adapter
        }
        sourceSpinner.onItemSelectedListener = SourcesSpinnerClass()
    }
    private fun fetchData(urlParam: String) {
        val endUrl = "https://newsdata.io/api/1/news?apikey=pub_65101e7e04c6a37ca23e0201e77acaffb8e7&language=en$urlParam"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, endUrl, null,
            {
                val newsJsonArray = it.getJSONArray("results")
                val newsArray = ArrayList<News>()
                for (i in 0 until newsJsonArray.length())   {
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = News(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("source_id"),
                        newsJsonObject.getString("link"),
                        newsJsonObject.getString("image_url")
                    )
                    newsArray.add(news)
                }
                mAdapter.updateNews(newsArray)
            },{})
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    override fun onItemClicked(item: News) {
        val builder = CustomTabsIntent.Builder();
        val customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(item.url));
    }

    inner class CategoriesSpinnerClass : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val item = parent!!.getItemAtPosition(position).toString()
            if (!(item.equals("Category"))) {
                val text = "&category=$item"
                sourceSpinner.setSelection(0)
                fetchData(text)
            }
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
    inner class SourcesSpinnerClass : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val item = parent!!.getItemAtPosition(position).toString()
            if (!(item.equals("Source"))) {
                val text = "&domain=$item"
                categorySpinner.setSelection(0)
                fetchData(text)
            }
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}