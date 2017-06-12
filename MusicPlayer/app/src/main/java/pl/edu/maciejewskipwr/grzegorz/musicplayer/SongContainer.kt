package pl.edu.maciejewskipwr.grzegorz.musicplayer


import android.content.Context
import android.support.constraint.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import java.util.ArrayList

class SongContainer(c: Context, private val songs: ArrayList<Song>) : BaseAdapter() {
    private val songInf: LayoutInflater = LayoutInflater.from(c)

    override fun getCount(): Int {
        return songs.size
    }

    override fun getItem(arg0: Int): Any? {
        return null
    }

    override fun getItemId(arg0: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val songListElement = songInf.inflate(R.layout.song, parent, false) as ConstraintLayout
        val songView = songListElement.findViewById(R.id.song_title) as TextView
        val artistView = songListElement.findViewById(R.id.song_artist) as TextView
        val currentSong = songs[position]
        songView.text = currentSong.title
        artistView.text = currentSong.artist
        songListElement.tag = position
        return songListElement
    }

}