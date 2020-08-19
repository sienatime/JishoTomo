package net.emojiparty.android.jishotomo.ui.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.emojiparty.android.jishotomo.analytics.AnalyticsLogger
import net.emojiparty.android.jishotomo.data.AppRepository
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses

class EntryViewModel : ViewModel() {
  private val appRepository: AppRepository by lazy { AppRepository() }

  private var entry: LiveData<EntryWithAllSenses> = MutableLiveData<EntryWithAllSenses>()

  fun entryLiveData(lifecycleOwner: LifecycleOwner, entryId: Int): LiveData<EntryWithAllSenses> {
    entry = appRepository.getEntryWithAllSenses(entryId, lifecycleOwner)

    return entry
  }

  fun toggleFavorite(analyticsLogger: AnalyticsLogger) {
    entry.value?.let { entry ->
      viewModelScope.launch {
        appRepository.toggleFavorite(entry.entry)
        analyticsLogger.logToggleFavoriteEvent(
          entry.id,
          entry.kanjiOrReading,
          !entry.isFavorited
        )
      }
    }
  }
}
