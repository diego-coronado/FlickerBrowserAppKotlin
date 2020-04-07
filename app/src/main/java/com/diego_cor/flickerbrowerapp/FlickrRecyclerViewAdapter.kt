package com.diego_cor.flickerbrowerapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class FlickrImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var thumbnail: ImageView = view.findViewById(R.id.thumbnail)
    var title: TextView = view.findViewById(R.id.title)
}

class FlickrRecyclerViewAdapter(private var photoList: List<Photo>) :
    RecyclerView.Adapter<FlickrImageViewHolder>() {
    private val TAG = "FlickRecyclerViewAdapt"

    fun loadNewDAta(newPhotos: List<Photo>) {
        photoList = newPhotos
        notifyDataSetChanged()
    }

    fun getPhoto(position: Int): Photo? {
        return if (photoList.isNotEmpty()) photoList[position] else null
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): FlickrImageViewHolder {
        //Called by the layout manager when it needs a new view
        Log.d(TAG, "onCreateViewHolder new view requested")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.browse, parent, false)
        return FlickrImageViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount called")
        return if (photoList.isNotEmpty()) photoList.size else 0
    }

    override fun onBindViewHolder(holder: FlickrImageViewHolder, position: Int) {
        //Called by the layout manager when it watns new data in an existing view
        if (photoList.isEmpty()) {
            holder.thumbnail.setImageResource(R.drawable.placeholder)
            holder.title.text =
                "No photos match your search.\n\nUse the search icon to search for photos"
        } else {
            val photoItem = photoList[position]
            Log.d(TAG, "onBindViewHolder: ${photoItem.title} --> $position")
            Picasso.get().load(photoItem.image).error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder).into(holder.thumbnail)
            holder.title.text = photoItem.title
        }
    }
}