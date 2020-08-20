package net.emojiparty.android.jishotomo.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesControl

class AnalyticsLogger(
  private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
) {
  private val CATEGORY_ENTRY = "entry"
  private val EVENT_FAVORITE_ENTRY = "favorite_entry"
  private val EVENT_UNFAVORITE_ENTRY = "unfavorite_entry"
  private val EVENT_ADD_WIDGET = "add_widget"
  private val EVENT_REMOVE_WIDGET = "remove_widget"
  private val EVENT_UPDATED_WIDGET = "updated_widget"
  private val EVENT_CSV_SUCCESS = "csv_success"
  private val EVENT_CSV_FAILED = "csv_failed"
  private val EVENT_UNFAVORITE_ALL = "unfavorite_all"
  private val PARAM_JLPT_LEVEL = "jlpt_level"

  fun logAppOpenEvent() {
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN) { }
  }

  fun logSearchResultsOrViewItemList(pagedEntriesControl: PagedEntriesControl) {
    if (pagedEntriesControl is PagedEntriesControl.Search) {
      logSearchResults(pagedEntriesControl)
    } else {
      logViewItemList(pagedEntriesControl)
    }
  }

  fun logViewItem(
    entryId: Int,
    expression: String
  ) {
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM) {
      param(FirebaseAnalytics.Param.ITEM_ID, entryId.toString())
      param(FirebaseAnalytics.Param.ITEM_NAME, expression)
      param(FirebaseAnalytics.Param.ITEM_CATEGORY, CATEGORY_ENTRY)
    }
  }

  fun logToggleFavoriteEvent(
    entryId: Int,
    expression: String,
    favorited: Boolean
  ) {
    if (favorited) {
      firebaseAnalytics.logEvent(EVENT_FAVORITE_ENTRY) {
        param(FirebaseAnalytics.Param.ITEM_ID, entryId.toString())
        param(FirebaseAnalytics.Param.ITEM_NAME, expression)
      }
    } else {
      firebaseAnalytics.logEvent(EVENT_UNFAVORITE_ENTRY) {
        param(FirebaseAnalytics.Param.ITEM_ID, entryId.toString())
        param(FirebaseAnalytics.Param.ITEM_NAME, expression)
      }
    }
  }

  fun logAddWidget() {
    firebaseAnalytics.logEvent(EVENT_ADD_WIDGET) { }
  }

  fun logDeleteWidget() {
    firebaseAnalytics.logEvent(EVENT_REMOVE_WIDGET) { }
  }

  fun logWidgetUpdated(
    jlptLevel: Int,
    entryId: Int,
    expression: String
  ) {
    firebaseAnalytics.logEvent(EVENT_UPDATED_WIDGET) {
      param(FirebaseAnalytics.Param.ITEM_ID, entryId.toString())
      param(FirebaseAnalytics.Param.ITEM_NAME, expression)
      param(PARAM_JLPT_LEVEL, jlptLevel.toString())
    }
  }

  fun logCsvSuccess() {
    firebaseAnalytics.logEvent(EVENT_CSV_SUCCESS) { }
  }

  fun logCsvFailed() {
    firebaseAnalytics.logEvent(EVENT_CSV_FAILED) { }
  }

  fun logUnfavoriteAll() {
    firebaseAnalytics.logEvent(EVENT_UNFAVORITE_ALL) { }
  }

  private fun logSearchResults(pagedEntriesControl: PagedEntriesControl.Search) {
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS) {
      param(FirebaseAnalytics.Param.SEARCH_TERM, pagedEntriesControl.searchTerm)
    }
  }

  private fun logViewItemList(pagedEntriesControl: PagedEntriesControl) {
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST) {
      param(FirebaseAnalytics.Param.ITEM_CATEGORY, pagedEntriesControl.name)
      if (pagedEntriesControl is PagedEntriesControl.JLPT) {
        param(PARAM_JLPT_LEVEL, pagedEntriesControl.level.toString())
      }
    }
  }
}
