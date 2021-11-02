package com.example.musicplayer.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import com.example.musicplayer.model.Music
import java.text.SimpleDateFormat

object Util {
    const val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1
    const val ACTIVITY_ACTION = "com.example.musicplayer.activity"

    @SuppressLint("Range")
    fun getMusicData(context: Context): ArrayList<Music> {
        val musics: ArrayList<Music> = ArrayList<Music>()
        val resolver = context.contentResolver
        val cursor = resolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null, null, null,
            MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        )
        while (cursor != null && cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
            val path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
            var author = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
            val time = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
            if (author == "<unknown") {
                author = "未知艺术家"
            }
            if (time > 200) {
                val music = Music(name, author, path, time)
                musics.add(music)
            }
        }
        cursor?.close()
        return musics
    }

    fun checkPublishPermission(activity: Activity?): Boolean {
        val permissions: MutableList<String> = ArrayList()
        if (PackageManager.PERMISSION_GRANTED !=
            ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissions.size != 0) {
            ActivityCompat.requestPermissions(
                activity,
                permissions.toTypedArray(),
                PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
            return false
        }
        return true
    }

    /*
    * 显示音乐总时长，转化为分秒
    * */
    fun getDuration(duration: Long): String? {
        val format = SimpleDateFormat("mm:ss")
        return format.format(duration)
    }


    fun initTime(currentPosition: Int, time: Long): String {
        val curMinute = currentPosition / 1000 / 60 //分
        val curSecond = currentPosition / 1000 % 60 //秒
        val durMinute = time / 1000 / 60 //分
        val durSecond = time / 1000 % 60 //秒
        return getTime(curMinute) + ":" +
                getTime(curSecond) + "/" +
                getTime(durMinute.toInt()) + ":" +
                getTime(durSecond.toInt())
    }

    private fun getTime(time: Int): String {
        return if (time < 10) "0$time" else time.toString() + ""
    }
}