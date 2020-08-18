package net.emojiparty.android.jishotomo.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import net.emojiparty.android.jishotomo.data.AppRepository
import net.emojiparty.android.jishotomo.data.models.CrossReferencedEntry
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses
import java.util.HashMap

class EntryViewModel : ViewModel() {
  fun entryLiveData(entryId: Int): LiveData<EntryWithAllSenses> {
    return liveData {
      val entry = AppRepository().getEntryWithAllSenses(entryId)
      val crossReferencedEntries = AppRepository().getCrossReferences(entryId)
      val hashMap = crossReferenceHash(crossReferencedEntries)

      entry.senses.forEach { sense ->
        hashMap[sense.sense.id]?.let {
          sense.crossReferences = it
        }
      }

      emit(entry)
    }
  }

  /**
   * the keys are sense ids,
   * the values are the list of cross-referenced entries
   */
  private fun crossReferenceHash(
    senses: List<CrossReferencedEntry>
  ): HashMap<Int, MutableList<CrossReferencedEntry>> {
    val hashMap = hashMapOf<Int, MutableList<CrossReferencedEntry>>()

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
