package musicplayer.cs371m.musicplayer

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel

class MainViewModel(application: Application) : AndroidViewModel(application) {
    // A repository can be a local database or the network
    //  or a combination of both
    private val repository = Repository()
    private var songResources = repository.fetchData()

    // Public properties, mostly accessed by PlayerFragment, but useful elsewhere

    // This variable controls what song is playing
    var currentIndex = 0
    // It is convenient to have the player never be null, so proactively
    // create it, but you should not create MediaPlayer instances
    // in the view model
    var player: MediaPlayer = MediaPlayer.create(
        application.applicationContext,
        getCurrentSongResourceId()
    )
    // Should I loop the current song?
    var loop = false
    // How many songs have played?
    var songsPlayed = 0
    // Is the player playing?
    var isPlaying = false

    // Creating a mutable list also creates a copy
    fun getCopyOfSongInfo(): MutableList<SongInfo> {
        return songResources.toMutableList()
    }

    fun shuffleAndReturnCopyOfSongInfo(): MutableList<SongInfo> {
        // XXX Write me
    }

    fun getCurrentSongName() : String {
        // XXX Write me
    }
    // Private function
    private fun nextIndex() : Int {
        // XXX Write me
    }
    fun nextSong() {
        // XXX Write me
    }
    fun getNextSongName() : String {
        // XXX Write me
    }

    fun prevSong() {
        // XXX Write me
    }

    fun getCurrentSongResourceId(): Int {
        // XXX Write me
    }
}