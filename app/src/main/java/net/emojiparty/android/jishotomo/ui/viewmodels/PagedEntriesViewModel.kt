package net.emojiparty.android.jishotomo.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import net.emojiparty.android.jishotomo.data.AppRepository
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry

class PagedEntriesViewModel : ViewModel() {

  val entries: LiveData<PagedList<SearchResultEntry>>
  val pagedEntriesControlLiveData = MutableLiveData<PagedEntriesControl>()
  lateinit var pagedEntriesControl: PagedEntriesControl

  init {
    val appRepo = AppRepository()
    entries = Transformations.switchMap(
        pagedEntriesControlLiveData
    ) { pagedEntriesControl: PagedEntriesControl ->
      this.pagedEntriesControl = pagedEntriesControl
      val searchType = pagedEntriesControl.searchType
      val searchTerm = pagedEntriesControl.searchTerm
      when (searchType) {
        PagedEntriesControl.SEARCH -> return@switchMap appRepo.search(searchTerm!!)
        PagedEntriesControl.FAVORITES -> return@switchMap appRepo.getFavorites()
        PagedEntriesControl.JLPT -> return@switchMap appRepo.getByJlptLevel(
            pagedEntriesControl.jlptLevel!!
        )
        else -> return@switchMap appRepo.browse()
      }
    }
  }
}