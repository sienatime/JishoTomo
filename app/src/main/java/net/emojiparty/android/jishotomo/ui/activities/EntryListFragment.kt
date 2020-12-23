package net.emojiparty.android.jishotomo.ui.activities

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.paging.PagedList
import kotlinx.android.synthetic.main.fragment_entry_list.loading
import kotlinx.android.synthetic.main.fragment_entry_list.no_results
import kotlinx.android.synthetic.main.fragment_entry_list.search_results_rv
import net.emojiparty.android.jishotomo.R
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry
import net.emojiparty.android.jishotomo.ui.adapters.PagedEntriesAdapter
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesViewModel

class EntryListFragment : Fragment(R.layout.fragment_entry_list) {
  private val viewModel: PagedEntriesViewModel by activityViewModels()

  private var adapter: PagedEntriesAdapter? = null

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    setupRecyclerView()
    setRecyclerViewWithNewAdapter()

    viewModel.getPagedEntriesControlLiveData().observe(
      viewLifecycleOwner,
      {
        // this is so that the PagedListAdapter does not try to perform a diff
        // against the two lists when changing search types. the app was really laggy
        // when changing lists without re-instantiating the adapter.
        setRecyclerViewWithNewAdapter()
        no_results.visibility = View.GONE
        loading.visibility = View.VISIBLE
      }
    )

    super.onViewCreated(view, savedInstanceState)
  }

  // Paging library reference https://developer.android.com/topic/libraries/architecture/paging
  private fun setupRecyclerView() {
    viewModel.getEntries().observe(
      viewLifecycleOwner,
      { entries: PagedList<SearchResultEntry> ->
        loading.visibility = View.INVISIBLE
        adapter?.submitList(entries)
        setNoResultsText(entries.size)
      }
    )
  }

  private fun setRecyclerViewWithNewAdapter() {
    adapter = PagedEntriesAdapter(R.layout.list_item_entry)
    search_results_rv.adapter = adapter
  }

  private fun setNoResultsText(size: Int) {
    if (size == 0) {
      no_results.visibility = View.VISIBLE
      no_results.text = viewModel.noResultsText()
    } else {
      no_results.visibility = View.GONE
    }
  }
}
