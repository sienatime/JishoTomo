package net.emojiparty.android.jishotomo.ui.viewmodels

sealed class PagedEntriesControl(val name: String) {
  data class Search(val searchTerm: String) : PagedEntriesControl("SEARCH")
  data class JLPT(val level: Int) : PagedEntriesControl("JLPT")
  object Browse : PagedEntriesControl("BROWSE")
  object Favorites : PagedEntriesControl("FAVORITES")
}
