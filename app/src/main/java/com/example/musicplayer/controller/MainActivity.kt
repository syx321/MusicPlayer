package com.example.musicplayer.controller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import com.example.musicplayer.R
import com.example.musicplayer.model.Music
import com.example.musicplayer.utils.Util
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    lateinit var listView: ListView
    lateinit var adapter: SYXAdapter
    lateinit var musicList: ArrayList<Music>
    lateinit var musicName: TextView
    lateinit var seekBar: SeekBar
    lateinit var seekTime: TextView
    lateinit var prev: ImageButton
    lateinit var pause: ImageButton
    lateinit var next: ImageButton
    lateinit var selectType: ImageButton
    lateinit var musicService: MusicService
    var curentMusic: Int = 0

    private object PlayType {
        val SINGLE = 0
        val RANDOM = 1
        val ORDER = 2
    }

    var type = PlayType.ORDER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.hide()
        }
        Util.checkPublishPermission(this)
        musicList = Util.getMusicData(this)
        adapter = SYXAdapter(this, R.layout.list_item, musicList)
        adapter.setOnClickLitener(object : SYXAdapter.OnItemClickLitener {
            override fun onItemClicked(position: Int) {
                initMediaPlayer(position)
                curentMusic = position
            }
        })
        listView = findViewById(R.id.list_view)
        listView.adapter = adapter
        seekBar = findViewById(R.id.seekbar)
        seekBar.setOnSeekBarChangeListener(this)
        seekTime = findViewById(R.id.seekTime)
        prev = findViewById(R.id.prev)
        prev.setOnClickListener(this)
        pause = findViewById(R.id.pause)
        pause.setOnClickListener(this)
        next = findViewById(R.id.next)
        next.setOnClickListener(this)
        selectType = findViewById(R.id.type)
        selectType.setOnClickListener(this)
        musicService = MusicService()
        musicName = findViewById(R.id.musicName)
        this.musicService.bindHandler(object : MusicService.CompleteHandler {
            override fun complete() {
                next()
            }
        })
    }

    private fun initMediaPlayer(position: Int) {
        val music = musicList.get(position)
        this.musicService.reset()
        seekBar.max = (music.duration / 1000).toInt()
        seekBar.progress = 0
        val mHandler = Handler()
        //Make sure you update Seekbar on UI thread
        runOnUiThread(object : Runnable {
            override fun run() {
                val mCurrentPosition: Int = musicService.getCurrentPosition()
                seekBar.progress = mCurrentPosition / 1000
                seekTime.text = Util.initTime(mCurrentPosition, music.duration)
                mHandler.postDelayed(this, 1)
            }
        })
        pause.setImageResource(R.mipmap.pause)
        musicService.initMediaPlayer(music)
        musicName.text = music.musicName
        Log.d("player", musicService.isPlaying().toString())
    }

    override fun onClick(veiw: View) {
        when (veiw.id) {
            R.id.prev -> prev()
            R.id.next -> next()
            R.id.pause -> pauseClicked()
            R.id.type -> selectType()
        }
    }

    private fun prev() {
        this.getPrevMusic().let {
            this.initMediaPlayer(it)
            Log.d("click", "prev" + it.toString())
        }
    }

    private fun getPrevMusic(): Int {
        if (curentMusic == 0) {
            curentMusic = musicList.count() - 1
        } else {
            curentMusic -= 1
        }
        return curentMusic
    }


    private fun next() {
        this.getNextMusic().let {
            this.initMediaPlayer(it)
            Log.d("click", "next" + it.toString())
        }
    }

    private fun getNextMusic(): Int {
        when (type) {
            PlayType.ORDER -> {
                if (curentMusic == musicList.count() - 1) {
                    curentMusic = 0
                } else {
                    curentMusic += 1
                }
            }
            PlayType.RANDOM -> curentMusic = getRandomMusic()
            PlayType.SINGLE -> curentMusic
        }

        return curentMusic
    }

    private fun random() {
        Log.d("click", "random")
        this.getRandomMusic().let { this.initMediaPlayer(it) }
    }

    private fun getRandomMusic(): Int {
        return (0..(musicList.count() - 1)).random()
    }

    private fun pauseClicked() {
        Log.d("click", "pause")
        val result = musicService.pause()
        if (result) {
            pause.setImageResource(R.mipmap.play)
        } else {
            pause.setImageResource(R.mipmap.pause)
        }
    }

    private fun selectType() {
        val select = (this.type + 1) % 3
        this.type = select
        when (type) {
            PlayType.ORDER -> {
                selectType.setImageResource(R.mipmap.order)
            }
            PlayType.RANDOM -> {
                selectType.setImageResource(R.mipmap.random)
            }
            PlayType.SINGLE -> {
                selectType.setImageResource(R.mipmap.single)
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        return
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        return
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        //0-100
        val progress = this.seekBar.progress
        Log.d("progress", progress.toString())
        musicService.player.seekTo(progress * 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.musicService.onDestroy()
    }
}