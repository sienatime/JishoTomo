package net.emojiparty.android.jishotomo.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.emojiparty.android.jishotomo.analytics.AnalyticsLogger
import net.emojiparty.android.jishotomo.data.AppRepository
import net.emojiparty.android.jishotomo.data.models.CrossReferencedEntry
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses
import java.util.HashMap

class EntryViewModel(private val entryId: Int) : ViewModel() {
  private val appRepository: AppRepository by lazy { AppRepository() }
  private val entry = MutableLiveData<EntryWithAllSenses>()
  private val isFavorited = MutableLiveData<Boolean>()

  init {
    viewModelScope.launch {
      val entryWithSenses = appRepository.getEntryWithAllSenses(entryId)
      entry.value = entryWithSenses
      isFavorited.value = entryWithSenses.isFavorited
    }
  }

  fun entryLiveData(): LiveData<EntryWithAllSenses> {
    return entry
  }

  fun isFavoritedLiveData(): LiveData<Boolean> {
    return isFavorited
  }

  fun getCrossReferencesForSense(senseId: Int): List<CrossReferencedEntry> {
    return getCrossEntriesHash()[senseId] ?: emptyList()
  }

  private var crossEntriesHash: HashMap<Int, MutableList<CrossReferencedEntry>>? = null

  private fun getCrossEntriesHash(): HashMap<Int, MutableList<CrossReferencedEntry>> {
    if (crossEntriesHash == null) {
      runBlocking {
        val crossReferencedEntries = appRepository.getCrossReferences(entry.value!!.id)
        crossEntriesHash = crossReferenceHash(crossReferencedEntries)
      }
    }
    return crossEntriesHash!!
  }

  fun toggleFavorite(analyticsLogger: AnalyticsLogger) {
    entry.value?.let { entry ->
      viewModelScope.launch {
        appRepository.toggleFavorite(entry.entry)
        isFavorited.value = entry.isFavorited
        analyticsLogger.logToggleFavoriteEvent(
          entry.id,
          entry.kanjiOrReading,
          entry.isFavorited
        )
      }
    }
  }

  private fun crossReferenceHash(
    senses: List<CrossReferencedEntry>
  ): HashMap<Int, MutableList<CrossReferencedEntry>> {
    val hashMap = HashMap<Int, MutableList<CrossReferencedEntry>>()

    senses.forEach { crossReferencedEntry ->
      val senseId = crossReferencedEntry.senseId

      if (hashMap[senseId] == null) {
        hashMap[senseId] = mutableListOf(crossReferencedEntry)
      } else {
        hashMap[senseId]!!.add(crossReferencedEntry)
      }
    }
    return hashMap
  }
}
