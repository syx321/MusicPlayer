package com.example.musicplayer.model

import java.io.Serializable

class Music(var musicName: String, var author: String, var path: String, var duration: Long){
    companion object {
        const val serialVersionUID = 1L
    }
}