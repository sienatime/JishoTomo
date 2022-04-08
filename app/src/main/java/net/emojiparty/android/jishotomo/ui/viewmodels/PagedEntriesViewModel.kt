package net.emojiparty.android.jishotomo.ui.viewmodels

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import net.emojiparty.android.jishotomo.R
import net.emojiparty.android.jishotomo.data.AppRepository
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry
import net.emojiparty.android.jishotomo.ui.presentation.ResourceFetcher
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesControl.Browse
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesControl.Favorites
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesControl.JLPT
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesControl.Search

class PagedEntriesViewModel(
  private val resourceFetcher: ResourceFetcher,
  private val appRepo: AppRepository = AppRepository()
) : ViewModel() {

  val pagedEntriesControlLiveData = MutableLiveData<PagedEntriesControl>()
  private val exportProgress = MutableLiveData<Int>()
  private val isExporting = MutableLiveData<Boolean>()

  val entriesLiveData = pagedEntriesControlLiveData.switchMap { query ->
    Pager(
      PagingConfig(pageSize = 20)
    ) {
      getSearchResults(query)
    }.flow.asLiveData()
  }

  fun setPagedEntriesControl(pagedEntriesControl: PagedEntriesControl) {
    this.pagedEntriesControlLiveData.value = pagedEntriesControl
  }

  fun getExportProgress(): LiveData<Int> = exportProgress

  fun setExportProgress(progess: Int) {
    exportProgress.value = progess
  }

  fun getIsExporting(): LiveData<Boolean> = isExporting

  fun setIsExporting(isExporting: Boolean) {
    this.isExporting.value = isExporting
  }

  fun isSearch(): Boolean = pagedEntriesControlLiveData.value is Search

  fun getSearchTerm(): String? {
    return (pagedEntriesControlLiveData.value as? Search)?.searchTerm
  }

  fun getJlptLevel(): Int? {
    return (pagedEntriesControlLiveData.value as? JLPT)?.level
  }

  fun getName(): String {
    return (pagedEntriesControlLiveData.value ?: Browse).name
  }

  fun noResultsText(): String {
    return when (val control = pagedEntriesControlLiveData.value) {
      is Favorites -> resourceFetcher.getString(R.string.no_favorites)
      is Search -> String.format(
        resourceFetcher.getString(R.string.no_search_results), control.searchTerm
      )
      else -> resourceFetcher.getString(R.string.nothing_here)
    }
  }

  fun titleIdForSearchType(): Int {
    return when (val control = pagedEntriesControlLiveData.value) {
      is Browse -> R.string.app_name
      is Favorites -> R.string.favorites
      is JLPT -> resourceFetcher.stringForJlptLevel(control.level)
      is Search -> R.string.search_results
      null -> 0
    }
  }

  fun isExportVisible(): Boolean {
    return isFavorites() || isJlpt()
  }

  fun isUnfavoriteAllVisible(): Boolean {
    return isFavorites()
  }

  fun restoreFromBundleValues(
    searchType: String?,
    searchTerm: String?,
    jlptLevel: Int
  ) {
    val pagedEntriesControl = when {
      jlptLevel > 0 -> JLPT(jlptLevel)
      searchTerm != null -> Search(searchTerm)
      searchType == Favorites.name -> Favorites
      else -> Browse
    }
    setPagedEntriesControl(pagedEntriesControl)
  }

  fun setFromSearchIntentAction(
    action: String?,
    query: String?
  ) {
    val pagedEntriesControl = if (Intent.ACTION_SEARCH == action) {
      Search(query ?: "")
    } else {
      Browse
    }
    setPagedEntriesControl(pagedEntriesControl)
  }

  fun getEntriesForExportAsync(): Deferred<List<EntryWithAllSenses>> {
    return when (val control = pagedEntriesControlLiveData.value) {
      is Favorites -> viewModelScope.async { appRepo.getAllFavorites() }
      is JLPT -> viewModelScope.async { appRepo.getAllByJlptLevel(control.level) }
      else -> throw RuntimeException(
        "Not allowed to export this kind of list! ${control?.name}"
      )
    }
  }

  private fun getSearchResults(pagedEntriesControl: PagedEntriesControl): PagingSource<Int, SearchResultEntry> {
    return when (pagedEntriesControl) {
      is Search -> appRepo.search(pagedEntriesControl.searchTerm)
      is Favorites -> appRepo.getFavorites()
      is JLPT -> appRepo.getByJlptLevel(pagedEntriesControl.level)
      else -> appRepo.browse()
    }
  }

  private fun isFavorites(): Boolean = pagedEntriesControlLiveData.value is Favorites

  private fun isJlpt(): Boolean = pagedEntriesControlLiveData.value is JLPT
}
