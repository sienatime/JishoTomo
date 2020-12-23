package net.emojiparty.android.jishotomo.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import net.emojiparty.android.jishotomo.data.AppRepository
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry

class PagedEntriesViewModel : ViewModel() {

  private val entries: LiveData<PagedList<SearchResultEntry>>
  private val pagedEntriesControl = MutableLiveData<PagedEntriesControl>()

  fun getPagedEntriesControlLiveData(): MutableLiveData<PagedEntriesControl> {
    return pagedEntriesControl
  }

  fun getPagedEntriesControl(): PagedEntriesControl {
    return pagedEntriesControl.value ?: PagedEntriesControl.Browse
  }

  fun setPagedEntriesControl(pagedEntriesControl: PagedEntriesControl) {
    this.pagedEntriesControl.value = pagedEntriesControl
  }

  fun getEntries(): LiveData<PagedList<SearchResultEntry>> = entries

  fun isFavorites(): Boolean = pagedEntriesControl.value is PagedEntriesControl.Favorites

  fun isJlpt(): Boolean = pagedEntriesControl.value is PagedEntriesControl.JLPT

  fun isSearch(): Boolean = pagedEntriesControl.value is PagedEntriesControl.Search

  fun getSearchTerm(): String? {
    return (pagedEntriesControl.value as? PagedEntriesControl.Search)?.searchTerm
  }

  fun hasEntries(): Boolean = (entries.value?.size ?: 0) > 0

  init {
    val appRepo = AppRepository()

    entries = Transformations.switchMap(
      pagedEntriesControl
    ) { pagedEntriesControl: PagedEntriesControl ->
      return@switchMap when (pagedEntriesControl) {
        is PagedEntriesControl.Search -> appRepo.search(pagedEntriesControl.searchTerm)
        is PagedEntriesControl.Favorites -> appRepo.getFavorites()
        is PagedEntriesControl.JLPT -> appRepo.getByJlptLevel(pagedEntriesControl.level)
        is PagedEntriesControl.Browse -> appRepo.browse()
      }
    }
  }
}
