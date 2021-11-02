package com.example.musicplayer.controller

import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import com.example.musicplayer.R
import com.example.musicplayer.model.Music
import java.lang.Exception

class MusicService {
    var player = MediaPlayer()
    lateinit var handler : CompleteHandler
    fun initMediaPlayer(music: Music) {
//        player = MediaPlayer.create(this, Uri.parse(path))
        try {
            player.reset()
            player.setDataSource(music.path)
            player.prepare()
            player.start()
        } catch (e: Exception) {
            return
        }

        player.setOnCompletionListener {
            handler.complete()
            Log.d("service","complete handler")
        }

    }

    fun bindHandler(handler: CompleteHandler) {
        this.handler = handler
    }

    fun pause() : Boolean{
        Log.d("click","pause")
        try  {
            if (player.isPlaying) {
                player.pause()
                return true
            } else {
                player.start()
                return false
            }
        } catch (e: Exception) {
            return false
        }
    }

    fun getCurrentPosition() : Int {
        return this.player.currentPosition
    }

    fun isPlaying() : Boolean {
        return player.isPlaying
    }

    fun reset() {
        this.player.seekTo(0)
    }

    fun onDestroy() {
        this.player.stop()
        this.player.release()
    }

    interface CompleteHandler {
        fun complete()
    }
}