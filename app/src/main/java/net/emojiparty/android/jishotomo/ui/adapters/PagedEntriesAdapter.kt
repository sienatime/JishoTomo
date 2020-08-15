package net.emojiparty.android.jishotomo.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import net.emojiparty.android.jishotomo.BR
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry
import net.emojiparty.android.jishotomo.ui.adapters.PagedEntriesAdapter.DataBindingViewHolder

class PagedEntriesAdapter(private val layoutId: Int) :
  PagedListAdapter<SearchResultEntry, DataBindingViewHolder>(DIFF_CALLBACK) {

  override fun onBindViewHolder(
    holder: DataBindingViewHolder,
    position: Int
  ) {
    val entry = getItem(position)
    if (entry != null) {
      holder.bind(entry)
    } else {
      holder.clear()
    }
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): DataBindingViewHolder {
    val context = parent.context
    val layoutInflater = LayoutInflater.from(context)
    val binding =
      DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, layoutId, parent, false)
    return DataBindingViewHolder(
      binding, context
    )
  }

  // https://medium.com/google-developers/android-data-binding-recyclerview-db7c40d9f0e4
  class DataBindingViewHolder(
    var binding: ViewDataBinding,
    var context: Context
  ) : ViewHolder(binding.root) {
    fun bind(presenter: SearchResultEntry?) {
      binding.setVariable(BR.presenter, presenter)
      binding.executePendingBindings()
    }

    fun clear() {
      binding.unbind()
    }
  }

  companion object {
    private val DIFF_CALLBACK: ItemCallback<SearchResultEntry> =
      object : ItemCallback<SearchResultEntry>() {
        override fun areItemsTheSame(
          oldSearchResultEntry: SearchResultEntry,
          newSearchResultEntry: SearchResultEntry
        ): Boolean {
          return oldSearchResultEntry.id == newSearchResultEntry.id
        }

        override fun areContentsTheSame(
          oldSearchResultEntry: SearchResultEntry,
          newSearchResultEntry: SearchResultEntry
        ): Boolean {
          return oldSearchResultEntry == newSearchResultEntry
        }
      }
  }
}
