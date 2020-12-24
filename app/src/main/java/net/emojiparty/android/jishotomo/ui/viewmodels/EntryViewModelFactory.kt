package net.emojiparty.android.jishotomo.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EntryViewModelFactory(private val entryId: Int) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return EntryViewModel(entryId) as T
  }
}
