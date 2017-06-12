package pl.edu.maciejewskipwr.grzegorz.musicplayer

import android.app.Service
import android.content.ContentUris
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.util.Log

import java.util.ArrayList
import java.util.Random

/**
 * Created by PanG on 12.06.2017.
 */

class MusicSettings : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private var shuffle = false
    private var rand: Random? = null
    private var player: MediaPlayer? = null
    private var songList: ArrayList<Song>? = null
    private var position: Int = 0

    private val musicBind = MusicBinder()

    private var songTitle = ""

    inner class MusicBinder : Binder() {
        internal val service: MusicSettings
            get() = this@MusicSettings
    }

    override fun onCreate() {
        super.onCreate()
        position = 0
        player = MediaPlayer()
        initMusicPlayer()
        rand = Random()
    }

    fun setShuffle() {
        shuffle = !shuffle
    }

    fun initMusicPlayer() {
        player!!.setWakeMode(applicationContext,
                PowerManager.PARTIAL_WAKE_LOCK)
        player!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        player!!.setOnPreparedListener(this)
        player!!.setOnCompletionListener(this)
        player!!.setOnErrorListener(this)
    }

    fun setSong(songIndex: Int) {
        position = songIndex
    }

    fun setList(songList: ArrayList<Song>) {
        this.songList = songList
    }

    override fun onBind(intent: Intent): IBinder? {
        return musicBind
    }

    override fun onUnbind(intent: Intent): Boolean {
        player!!.stop()
        player!!.release()
        return false
    }

    override fun onCompletion(mp: MediaPlayer) {
        if (player!!.currentPosition > 0) {
            mp.reset()
            playNext()
        }
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        mp.reset()
        return false
    }

    override fun onPrepared(mp: MediaPlayer) {
        mp.start()
    }


    fun playSong() {
        player!!.reset()

        val playSong = songList!![position]
        songTitle = playSong.title
        val currSong = playSong.id
        val trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong)
        try {
            player!!.setDataSource(applicationContext, trackUri)
        } catch (e: Exception) {
            Log.e("MUSIC SERVICE", "Error setting data source", e)
        }

        player!!.prepareAsync()
    }

    val posn: Int
        get() = player!!.currentPosition

    val dur: Int
        get() = player!!.duration

    val isPlaying: Boolean
        get() = player!!.isPlaying

    fun pausePlayer() {
        player!!.pause()
    }

    fun seek(posn: Int) {
        player!!.seekTo(posn)
    }

    fun go() {
        player!!.start()
    }

    fun playPrev() {
        position--
        if (position < 0) position = songList!!.size - 1
        playSong()
    }

    //skip to next
    fun playNext() {
        if (shuffle) {
            var newSong = position
            while (newSong == position) {
                newSong = rand!!.nextInt(songList!!.size)
            }
            position = newSong
        } else {
            position++
            if (position >= songList!!.size) position = 0
        }
        playSong()
    }

    override fun onDestroy() {
        stopForeground(true)
    }

    companion object {
        private val NOTIFY_ID = 1
    }
}