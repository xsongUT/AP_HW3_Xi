package musicplayer.cs371m.musicplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import musicplayer.cs371m.musicplayer.databinding.SettingsFragmentBinding

class SettingsFragment : Fragment() {
    // https://developer.android.com/topic/libraries/view-binding#fragments
    private var _binding: SettingsFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    private fun initMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Clear menu because we don't want settings icon
                menu.clear()
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Do nothing
                return false
            }
        }, viewLifecycleOwner)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMenu()
        // XXX Write me findNavController().popBackStack() exits
        // Handle the back navigation when a button is clicked (you can attach this to any button)
        binding.cancelButton.setOnClickListener {
            // This pops the current fragment off the back stack and goes back to the previous fragment (e.g., PlayerFragment)
            findNavController().popBackStack()
        }

        // update song count
        binding.countMusicPlayed.text = viewModel.songsPlayed.toString()
        // XXX End
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
