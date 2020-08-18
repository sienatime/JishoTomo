package net.emojiparty.android.jishotomo.ui.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import net.emojiparty.android.jishotomo.data.AppRepository
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses

class EntryViewModel : ViewModel() {
  fun entryLiveData(lifecycleOwner: LifecycleOwner, entryId: Int): LiveData<EntryWithAllSenses> {
    return AppRepository().getEntryWithAllSenses(entryId, lifecycleOwner)
  }
}
