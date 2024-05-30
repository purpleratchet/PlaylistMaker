package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.model.TrackSearchModel
import com.example.playlistmaker.search.ui.model.ScreenState
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel.Companion.EXTRA_TRACK
import com.example.playlistmaker.utils.Debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var userInput = ""
    private val trackAdapter = TracksAdapter {
        onTrackClickDebounce(it)
    }
    private val savedTrackAdapter = TracksAdapter {
        onTrackClickDebounce(it)
    }
    private var textWatcher: TextWatcher? = null
    private val viewModel by viewModel<SearchViewModel>()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var onTrackClickDebounce: (TrackSearchModel) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvSearchResult.adapter = trackAdapter
        binding.rvHistory.adapter = savedTrackAdapter
        setListeners()
        onTrackClickDebounce = Debounce().debounce(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false
        ) {
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                bundleOf(EXTRA_TRACK to it)
            )
            viewModel.addTrackToHistory(it)
        }
        viewModel.observeState().observe(viewLifecycleOwner, ::render)
        viewModel.getSavedTracksLiveData()
            .observe(viewLifecycleOwner) { savedTrackAdapter.updateTracks(it) }
    }

    private fun setListeners() {
        binding.refresh.setOnClickListener {
            viewModel.search(userInput)
        }
        binding.clearImageView.setOnClickListener {
            binding.searchEditText.setText("")
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
        }
        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            binding.rvHistory.visibility = View.GONE
            binding.historyMessage.visibility = View.GONE
            binding.clearHistoryButton.visibility = View.GONE
        }
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                updateViewVisibility(s)
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateViewVisibility(s)
                userInput = s.toString()
                viewModel.searchDebounce(userInput)
                binding.historyMessage.visibility = View.GONE
                binding.clearHistoryButton.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                updateViewVisibility(s)
                binding.historyMessage.visibility = View.GONE
//                binding.clearHistoryButton.visibility = View.GONE
            }
        }
        textWatcher?.let { binding.searchEditText.addTextChangedListener(it) }

        // Add focus change listener to clear search results when focused
        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (binding.searchEditText.text.isEmpty()) {
                    viewModel.showHistoryTracks()
                } else {
                    viewModel.resetSearchState()
                }
            }
        }
    }

    private fun updateViewVisibility(s: CharSequence?) {
        binding.clearImageView.visibility = clearButtonVisibility(s)
        if (s.isNullOrEmpty()) {
            binding.rvSearchResult.visibility = View.GONE
            binding.rvHistory.visibility = View.VISIBLE
            binding.historyMessage.visibility = View.VISIBLE
        } else {
            binding.rvHistory.visibility = View.GONE
            binding.rvSearchResult.visibility = View.VISIBLE
            binding.historyMessage.visibility = View.GONE
        }
    }

    private fun render(state: ScreenState) {
        when (state) {
            is ScreenState.Initial -> Initial()
            is ScreenState.Loading -> Loading()
            is ScreenState.SearchedState -> SearchedState(state.tracks)
            is ScreenState.SavedState -> SavedState(state.tracks)
            is ScreenState.ErrorState -> ErrorState(state.errorMessage)
            is ScreenState.EmptyState -> EmptyState(state.message)
        }
    }

    private fun Initial() {
        binding.progressBar.visibility = View.GONE
        binding.noResults.visibility = View.GONE
        binding.noInternet.visibility = View.GONE
        binding.refresh.visibility = View.GONE
        binding.rvSearchResult.visibility = View.GONE
        binding.rvHistory.visibility = View.VISIBLE
        if (binding.rvHistory.get(0) == null)  {
            binding.historyMessage.visibility = View.GONE
            binding.clearHistoryButton.visibility = View.GONE
        }
        binding.clearImageView.visibility = View.GONE
        binding.searchEditText.setText("")
        hideKeyboard()
    }

    private fun Loading() {
        binding.progressSearch.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
        hideKeyboard()
    }

    private fun ErrorState(errorMessage: String) {
        binding.progressBar.visibility = View.GONE
        binding.noInternet.visibility = View.VISIBLE
        binding.refresh.visibility = View.VISIBLE
        binding.noResults.visibility = View.VISIBLE
        binding.imageViewNoResults.setImageResource(R.drawable.ic_no_internet)
        binding.textViewNoResults.text = errorMessage
        hideKeyboard()
    }

    private fun EmptyState(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.noResults.visibility = View.VISIBLE
        binding.imageViewNoResults.setImageResource(R.drawable.ic_no_results)
        binding.textViewNoResults.text = message
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun SearchedState(tracks: ArrayList<TrackSearchModel>) {
        binding.historyMessage.visibility = View.GONE
        binding.noResults.visibility = View.GONE
        binding.rvSearchResult.visibility = View.VISIBLE
        binding.progressSearch.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.noInternet.visibility = View.GONE
        binding.refresh.visibility = View.GONE
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun SavedState(tracks: ArrayList<TrackSearchModel>) {
        binding.noResults.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.refresh.visibility = View.GONE
        binding.rvSearchResult.visibility = View.GONE
        binding.rvHistory.visibility = View.VISIBLE
        binding.historyMessage.visibility = View.VISIBLE
        binding.clearHistoryButton.visibility = View.VISIBLE
        savedTrackAdapter.tracks.clear()
        savedTrackAdapter.tracks.addAll(tracks)
        savedTrackAdapter.notifyDataSetChanged()
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    fun Fragment.hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity?.currentFocus ?: View(requireContext())
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        textWatcher?.let { binding.searchEditText.removeTextChangedListener(it) }
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.isEqual()
        binding.searchEditText.setText("")
        viewModel.resetSearchState() // Reset search state on resume
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 300L
    }
}