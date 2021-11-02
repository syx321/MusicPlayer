package com.example.musicplayer.controller

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.musicplayer.R
import com.example.musicplayer.model.Music
import com.example.musicplayer.utils.Util
import java.util.*

class SYXAdapter(activity: Activity, val resource: Int,data: ArrayList<Music>) :
    ArrayAdapter<Music>(activity,resource,data) {
    lateinit var myLitener: OnItemClickLitener

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(resource,parent,false)
        val musicName = view.findViewById<TextView>(R.id.musicName)
        val author = view.findViewById<TextView>(R.id.author)
        val duration = view.findViewById<TextView>(R.id.duration)
        val music = this.getItem(position)
        if (music != null) {
            musicName.text = music.musicName
            author.text = music.author
            duration.text = Util.getDuration(music.duration)
        }

        view.setOnClickListener(View.OnClickListener {
            myLitener.onItemClicked(position)
        })

        return view
    }

    fun setOnClickLitener(mOnItemClickLitener: OnItemClickLitener) {
        this.myLitener = mOnItemClickLitener
    }

    interface OnItemClickLitener {
        fun onItemClicked(position: Int)
    }
}