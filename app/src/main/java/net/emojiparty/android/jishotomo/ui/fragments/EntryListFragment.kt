package net.emojiparty.android.jishotomo.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.paging.PagedList
import net.emojiparty.android.jishotomo.R
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry
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
  ): View? {
    _binding = FragmentEntryListBinding.inflate(inflater, container, false)
    val view = binding.root
    return view
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    setupRecyclerView()
    setRecyclerViewWithNewAdapter()

    viewModel.getPagedEntriesControl().observe(
      viewLifecycleOwner,
      {
        // this is so that the PagedListAdapter does not try to perform a diff
        // against the two lists when changing search types. the app was really laggy
        // when changing lists without re-instantiating the adapter.
        setRecyclerViewWithNewAdapter()
        binding.noResults.visibility = View.GONE
        binding.loading.visibility = View.VISIBLE
      }
    )

    super.onViewCreated(view, savedInstanceState)
  }

  // Paging library reference https://developer.android.com/topic/libraries/architecture/paging
  private fun setupRecyclerView() {
    viewModel.getEntries().observe(
      viewLifecycleOwner,
      { entries: PagedList<SearchResultEntry> ->
        binding.loading.visibility = View.INVISIBLE
        adapter?.submitList(entries)
        setNoResultsText(entries.size)
      }
    )
  }

  private fun setRecyclerViewWithNewAdapter() {
    adapter = PagedEntriesAdapter(R.layout.list_item_entry)
    binding.searchResultsRv.adapter = adapter
  }

  private fun setNoResultsText(size: Int) {
    if (size == 0) {
      binding.noResults.visibility = View.VISIBLE
      binding.noResults.text = viewModel.noResultsText()
    } else {
      binding.noResults.visibility = View.GONE
    }
  }
}
