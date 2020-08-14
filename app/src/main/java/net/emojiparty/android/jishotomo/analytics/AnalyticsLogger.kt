package net.emojiparty.android.jishotomo.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Event
import com.google.firebase.analytics.FirebaseAnalytics.Param
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesControl

class AnalyticsLogger(
  context: Context,
  private val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
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
    firebaseAnalytics.logEvent(Event.APP_OPEN, Bundle())
  }

  fun logSearchResultsOrViewItemList(pagedEntriesControl: PagedEntriesControl) {
    if (pagedEntriesControl.searchType == PagedEntriesControl.SEARCH) {
      logSearchResults(pagedEntriesControl)
    } else {
      logViewItemList(pagedEntriesControl)
    }
  }

  fun logViewItem(
    entryId: Int,
    expression: String
  ) {
    val bundle = entryBundle(entryId, expression)
    bundle.putString(Param.ITEM_CATEGORY, CATEGORY_ENTRY)
    firebaseAnalytics.logEvent(Event.VIEW_ITEM, bundle)
  }

  fun logToggleFavoriteEvent(
    entryId: Int,
    expression: String,
    favorited: Boolean
  ) {
    val bundle = entryBundle(entryId, expression)
    if (favorited) {
      firebaseAnalytics.logEvent(EVENT_FAVORITE_ENTRY, bundle)
    } else {
      firebaseAnalytics.logEvent(EVENT_UNFAVORITE_ENTRY, bundle)
    }
  }

  fun logAddWidget() {
    firebaseAnalytics.logEvent(EVENT_ADD_WIDGET, Bundle())
  }

  fun logDeleteWidget() {
    firebaseAnalytics.logEvent(EVENT_REMOVE_WIDGET, Bundle())
  }

  fun logWidgetUpdated(
    jlptLevel: Int,
    entryId: Int,
    expression: String
  ) {
    val bundle = entryBundle(entryId, expression)
    bundle.putString(PARAM_JLPT_LEVEL, jlptLevel.toString())
    firebaseAnalytics.logEvent(EVENT_UPDATED_WIDGET, bundle)
  }

  fun logCsvSuccess() {
    firebaseAnalytics.logEvent(EVENT_CSV_SUCCESS, Bundle())
  }

  fun logCsvFailed() {
    firebaseAnalytics.logEvent(EVENT_CSV_FAILED, Bundle())
  }

  fun logUnfavoriteAll() {
    firebaseAnalytics.logEvent(EVENT_UNFAVORITE_ALL, Bundle())
  }

  private fun entryBundle(
    entryId: Int,
    expression: String
  ): Bundle {
    val bundle = Bundle()
    bundle.putString(Param.ITEM_ID, entryId.toString())
    bundle.putString(Param.ITEM_NAME, expression)
    return bundle
  }

  private fun logSearchResults(pagedEntriesControl: PagedEntriesControl) {
    val bundle = Bundle()
    bundle.putString(Param.SEARCH_TERM, pagedEntriesControl.searchTerm)
    firebaseAnalytics.logEvent(Event.VIEW_SEARCH_RESULTS, bundle)
  }

  private fun logViewItemList(pagedEntriesControl: PagedEntriesControl) {
    val bundle = Bundle()
    bundle.putString(Param.ITEM_CATEGORY, pagedEntriesControl.searchType)
    if (pagedEntriesControl.searchType == PagedEntriesControl.JLPT) {
      bundle.putString(PARAM_JLPT_LEVEL, pagedEntriesControl.jlptLevel.toString())
    }
    firebaseAnalytics.logEvent(Event.VIEW_ITEM_LIST, bundle)
  }
}
