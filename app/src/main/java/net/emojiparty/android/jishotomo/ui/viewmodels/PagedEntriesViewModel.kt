package net.emojiparty.android.jishotomo.ui.viewmodels

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
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

  private val entries: LiveData<PagedList<SearchResultEntry>>
  private val pagedEntriesControl = MutableLiveData<PagedEntriesControl>()

  init {
    entries = Transformations.switchMap(
      pagedEntriesControl
    ) { pagedEntriesControl: PagedEntriesControl ->
      return@switchMap when (pagedEntriesControl) {
        is Search -> appRepo.search(pagedEntriesControl.searchTerm)
        is Favorites -> appRepo.getFavorites()
        is JLPT -> appRepo.getByJlptLevel(pagedEntriesControl.level)
        is Browse -> appRepo.browse()
      }
    }
  }

  fun getPagedEntriesControl(): MutableLiveData<PagedEntriesControl> {
    return pagedEntriesControl
  }

  fun setPagedEntriesControl(pagedEntriesControl: PagedEntriesControl) {
    this.pagedEntriesControl.value = pagedEntriesControl
  }

  fun getEntries(): LiveData<PagedList<SearchResultEntry>> = entries

  fun isSearch(): Boolean = pagedEntriesControl.value is Search

  fun getSearchTerm(): String? {
    return (pagedEntriesControl.value as? Search)?.searchTerm
  }

  fun getJlptLevel(): Int? {
    return (pagedEntriesControl.value as? JLPT)?.level
  }

  fun getName(): String {
    return (pagedEntriesControl.value ?: Browse).name
  }

  fun noResultsText(): String {
    return when (val control = pagedEntriesControl.value) {
      is Favorites -> resourceFetcher.getString(R.string.no_favorites)
      is Search -> String.format(
        resourceFetcher.getString(R.string.no_search_results), control.searchTerm
      )
      else -> resourceFetcher.getString(R.string.nothing_here)
    }
  }

  fun titleIdForSearchType(): Int {
    return when (val control = pagedEntriesControl.value) {
      is Browse -> R.string.app_name
      is Favorites -> R.string.favorites
      is JLPT -> resourceFetcher.stringForJlptLevel(control.level)
      is Search -> R.string.search_results
      null -> 0
    }
  }

  fun isExportVisible(): Boolean {
    return hasFavorites() || isJlpt()
  }

  fun isUnfavoriteAllVisible(): Boolean {
    return hasFavorites()
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
    return when (val control = pagedEntriesControl.value) {
      is Favorites -> viewModelScope.async { appRepo.getAllFavorites() }
      is JLPT -> viewModelScope.async { appRepo.getAllByJlptLevel(control.level) }
      else -> throw RuntimeException(
        "Not allowed to export this kind of list! ${control?.name}"
      )
    }
  }

  private fun hasFavorites(): Boolean {
    return isFavorites() && hasEntries()
  }

  private fun isFavorites(): Boolean = pagedEntriesControl.value is Favorites

  private fun isJlpt(): Boolean = pagedEntriesControl.value is JLPT

  private fun hasEntries(): Boolean = (entries.value?.size ?: 0) > 0
}
