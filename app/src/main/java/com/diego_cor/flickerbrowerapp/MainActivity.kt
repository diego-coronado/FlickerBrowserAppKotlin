package com.diego_cor.flickerbrowerapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager

import kotlinx.android.synthetic.main.content_main.*
import java.lang.Exception

class MainActivity : BaseActivity(), GetRawData.OnDownloadComplete,
    GetFlickJsonData.OnDataAvailable,
    RecyclerItemClickListener.OnRecyclerClickListener {
    private val TAG = "MainActivity"

    private val flickrRecyclerViewAdapter = FlickrRecyclerViewAdapter(ArrayList())

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activateToolbar(false)

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.addOnItemTouchListener(RecyclerItemClickListener(this, recycler_view, this))
        recycler_view.adapter = flickrRecyclerViewAdapter

        val url = createUri(
            "https://api.flickr.com/services/feeds/photos_public.gne",
            "android,oreo",
            "en-us",
            true
        )
        val getRawData = GetRawData(this)
        getRawData.execute(url)

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
        Log.d(TAG, "onCreate ended")

    }

    override fun onItemClick(view: View, position: Int) {
        Log.d(TAG, "onItemClick: starts")
    }

    override fun onItemLongClick(view: View, position: Int) {
        Log.d(TAG, "onItemLongClick: starts")
        val photo = flickrRecyclerViewAdapter.getPhoto(position)
        if (photo != null) {
            val intent = Intent(this, PhotoDetailsActivity::class.java)
            intent.putExtra(PHOTO_TRANSFER, photo)
            startActivity(intent)
        }
    }

    private fun createUri(
        baseURL: String,
        searchCriteria: String,
        lang: String,
        matchAll: Boolean
    ): String {
        Log.d(TAG, "createUri started")
        var uri = Uri.parse(baseURL).buildUpon()
            .appendQueryParameter("tags", searchCriteria)
            .appendQueryParameter("tagmode", if (matchAll) "ALL" else "ANY")
            .appendQueryParameter("lang", lang).appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .build()

        return uri.toString()
    }

    //    companion object {
//        private const val TAG = "MainActivity"
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_search -> {
                startActivity(Intent(this, SearchActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDownloadComplete(data: String, status: DownloadStatus) {
        if (status == DownloadStatus.OK) {
            Log.d(TAG, "onDownloadcomplete called")

            val getFlickJsonData = GetFlickJsonData(this)
            getFlickJsonData.execute(data)
        } else {
            Log.d(TAG, "onDownloadComplete failed with status $status. Error message is $data")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onDataAvailable(data: List<Photo>) {
        flickrRecyclerViewAdapter.loadNewDAta(data)
    }

    override fun onError(exception: Exception) {
        Log.e(TAG, "onError called with ${exception.message}")
    }

    override fun onResume() {
        super.onResume()

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val queryResult = sharedPref.getString(FLICKR_QUERY, "")

        if (queryResult != null && queryResult.isNotEmpty()) {
            val url = createUri("https://api.flickr.com/services/feeds/photos_public.gne", queryResult,"en-us", true)
            val getRawData = GetRawData(this)
            getRawData.execute(url)
        }

    }
}
