package net.emojiparty.android.jishotomo.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.emojiparty.android.jishotomo.ui.presentation.ResourceFetcher

class PagedEntriesViewModelFactory(
  private val resourceFetcher: ResourceFetcher
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return PagedEntriesViewModel(resourceFetcher) as T
  }
}
