package net.emojiparty.android.jishotomo.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import kotlinx.coroutines.launch
import net.emojiparty.android.jishotomo.R
import net.emojiparty.android.jishotomo.databinding.FragmentEntryListBinding
import net.emojiparty.android.jishotomo.ui.adapters.PagedEntriesAdapter
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesViewModel

class EntryListFragment : Fragment() {
  private val viewModel: PagedEntriesViewModel by activityViewModels()

  private var _binding: FragmentEntryListBinding? = null
  private val binding get() = _binding!!

  private var adapter: PagedEntriesAdapter? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentEntryListBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    setRecyclerViewWithNewAdapter()

    binding.noResults.visibility = View.GONE
    binding.loading.visibility = View.VISIBLE

    viewModel.entriesLiveData.observe(viewLifecycleOwner) { pagingData ->
      // this is so that the PagingDataAdapter does not try to perform a diff
      // against the two lists when changing search types. the app was really laggy
      // when changing lists without re-instantiating the adapter.
      setRecyclerViewWithNewAdapter()
      binding.loading.visibility = View.GONE
      viewLifecycleOwner.lifecycleScope.launch {
        adapter?.submitData(pagingData)
      }
    }

    viewModel.getExportProgress().observe(viewLifecycleOwner) {
      binding.exporting.progress = it
    }

    viewModel.getIsExporting().observe(viewLifecycleOwner) {
      binding.exporting.isVisible = it
    }

    super.onViewCreated(view, savedInstanceState)
  }

  private fun setRecyclerViewWithNewAdapter() {
    adapter = PagedEntriesAdapter(R.layout.list_item_entry).also {
      it.addLoadStateListener { loadState ->
        if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && it.itemCount < 1) {
          binding.noResults.visibility = View.VISIBLE
          binding.noResults.text = viewModel.noResultsText()
        } else {
          binding.noResults.visibility = View.GONE
        }
      }
    }
    binding.searchResultsRv.adapter = adapter
  }
}
