package musicplayer.cs371m.musicplayer

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import musicplayer.cs371m.musicplayer.databinding.PlayerFragmentBinding
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.floor

class PlayerFragment : Fragment() {
    // When this is true, the displayTime coroutine should not modify the seek bar
    val userModifyingSeekBar = AtomicBoolean(false)
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: RVDiffAdapter

    private var _binding: PlayerFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlayerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initRecyclerViewDividers(rv: RecyclerView) {
        // Let's have dividers between list items
        val dividerItemDecoration = DividerItemDecoration(
            rv.context, LinearLayoutManager.VERTICAL
        )
        rv.addItemDecoration(dividerItemDecoration)
    }

    // Please put all display updates in this function
    // The exception is that
    //   displayTime updates the seek bar, time passed & time remaining
    private fun updateDisplay() {
        // If settings is active, we are in the background and do
        // not have a binding.  Return early.
        if (_binding == null) {
            return
        }
        //XXX Write me. Make sure all player UI elements are up to date
        // That includes all buttons, textViews, icons & the seek bar

        // playerPlayPauseButton
        if(viewModel.isPlaying){
            binding.playerPlayPauseButton.setImageResource(R.drawable.ic_pause_black_24dp)
        }else{
            binding.playerPlayPauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        }

        // playerCurrentSongText
        val currentSong: String = viewModel.getCurrentSongName()
        binding.playerCurrentSongText.text = currentSong

        // playerNextSongText
        val nextSong: String = viewModel.getNextSongName()
        binding.playerNextSongText.text = nextSong

        // highlight current
        adapter.notifyItemChanged(viewModel.currentIndex)

        // Loop
        if(viewModel.loop){
            MainActivity.setBackgroundColor(binding.loopIndicator, Color.RED)
        }
        else{
            MainActivity.setBackgroundColor(binding.loopIndicator, Color.TRANSPARENT)
        }

        //XXX End
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Make the RVDiffAdapter and set it up
        //XXX Write me. Setup adapter.
        adapter = RVDiffAdapter(viewModel){ songIndex ->
            // Handle item click
            initPlayer(songIndex)
            handleCurrentSong()
        }
        binding.playerRV.adapter = adapter
        binding.playerRV.layoutManager = LinearLayoutManager(requireContext())
        binding.playerRV.isNestedScrollingEnabled = false

        initRecyclerViewDividers(binding.playerRV)
        adapter.submitList(viewModel.getCopyOfSongInfo())
        //XXX End

        //XXX Write me. Write callbacks for buttons
        // play/pause
        binding.playerPlayPauseButton.setOnClickListener {
            handleCurrentSong()
        }
        // next
        binding.playerSkipForwardButton.setOnClickListener {
            handlePlayNextSong(viewModel.isPlaying)
        }
        // prev
        binding.playerSkipBackButton.setOnClickListener {
            handlePlayPrevSong(viewModel.isPlaying)
        }
        // permute
        binding.playerPermuteButton.setOnClickListener {
            handleShuffleList()
        }
        //XXX End

        //XXX Write me. binding.playerSeekBar.setOnSeekBarChangeListener
        //binding.playerSeekBar.max = 100
        binding.playerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                if(fromUser){
//                    handleSeekBarDragging()
//                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                handleSeekBarDragBegin()
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                handleSeekBarDragEnd(seekBar)
            }
        })
        //XXX End

        updateDisplay()

        // Don't change this code.  We are launching a coroutine (user-level thread) that will
        // execute concurrently with our code, but will update the displayed time
        val millisec = 100L
        viewLifecycleOwner.lifecycleScope.launch {
            displayTime(millisec)
        }
    }

    // The suspend modifier marks this as a function called from a coroutine
    // Note, this whole function is somewhat reminiscent of the Timer class
    // from Fling and Peck.  We use an independent thread to manage one small
    // piece of our GUI.  This coroutine should not modify any data accessed
    // by the main thread (it can read property values)
    private suspend fun displayTime(misc: Long) {
        // This only runs while the display is active
        while (viewLifecycleOwner.lifecycleScope.coroutineContext.isActive) {
            val currentPosition = viewModel.player.currentPosition
            val maxTime = viewModel.player.duration
            // Update the seek bar (if the user isn't updating it)
            // and update the passed and remaining time
            //XXX Write me

            if(!userModifyingSeekBar.get()){  //  && viewModel.isPlaying
                // update time current/remaining
                binding.playerTimePassedText.text = convertTime(currentPosition)
                binding.playerTimeRemainingText.text = convertTime(maxTime - currentPosition)
                // update seekbar
                val maxProgress = binding.playerSeekBar.max
                binding.playerSeekBar.progress = maxProgress * currentPosition / maxTime
            }

            //XXX End
            // Leave this code as is.  it inserts a delay so that this thread does
            // not consume too much CPU
            delay(misc)
        }
    }

    // This method converts time in milliseconds to minutes-second formatted string
    // with two digit minutes and two digit sections, e.g., 01:30
    private fun convertTime(milliseconds: Int): String {
        //XXX Write me
        val seconds = (milliseconds / 1000).toInt()
        val mm = seconds / 60
        val ss = seconds % 60
        return String.format("%02d:%02d", mm, ss)
        //XXX End me
    }

    // XXX Write me, handle player dynamics and currently playing/next song
    // play current song
    private fun handleCurrentSong(){
        if(viewModel.isPlaying){
            viewModel.player.pause()
            viewModel.isPlaying = false
        }else{
            viewModel.player.start()
            viewModel.isPlaying = true
            viewModel.songsPlayed += 1
        }
        updateDisplay()
    }

    // play next song
    private fun handlePlayNextSong(autoStart: Boolean){
        updateDisplay()
        initPlayer(-1)
        if(autoStart){
            handleCurrentSong()
        }
        updateDisplay()
    }

    // play prev song
    private fun handlePlayPrevSong(autoStart: Boolean){
        updateDisplay()
        initPlayer(-2)
        if(autoStart){
            handleCurrentSong()
        }
        updateDisplay()
    }

    // seekbar dragging
    private fun handleSeekBarDragging() {
    }
    // seekbar begin
    private fun handleSeekBarDragBegin(){
        userModifyingSeekBar.set(true)
    }

    // seekbar end
    private fun handleSeekBarDragEnd(seekBar: SeekBar?){
        if(userModifyingSeekBar.get()){
            val progress: Int = seekBar?.progress ?: 0
            val maxProgress = binding.playerSeekBar.max
            val maxTime = viewModel.player.duration
            val seekTime = maxTime * progress / maxProgress
            viewModel.player.seekTo(seekTime)
        }
        userModifyingSeekBar.set(false)
    }

    // handle shuffle
    private fun handleShuffleList(){
        adapter.submitList(viewModel.shuffleAndReturnCopyOfSongInfo())
        updateDisplay()
    }

    // init player time info
    private fun initPlayer(songIndex : Int){
        if(viewModel.isPlaying){
            viewModel.player.stop()
            viewModel.isPlaying = false
        }

        viewModel.player.reset()
        viewModel.player.release()

        if(songIndex == -1){
            viewModel.nextSong()
        }else if(songIndex == -2)
        {
            viewModel.prevSong()
        }
        else{
            viewModel.currentIndex = songIndex
        }

        viewModel.player = MediaPlayer.create(viewModel.getApplication(),
            viewModel.getCurrentSongResourceId())

        viewModel.player.setOnCompletionListener {
            viewModel.isPlaying = false
            if(viewModel.loop){
                //viewModel.player.isLooping = true
                handleCurrentSong()
            }else{
                handlePlayNextSong(true)
            }
        }
        //
        //adapter.notifyItemChanged(viewModel.currentIndex)
    }
    // XXX End

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
