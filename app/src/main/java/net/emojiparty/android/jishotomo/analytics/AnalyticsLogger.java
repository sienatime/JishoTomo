package net.emojiparty.android.jishotomo.analytics;

import android.content.Context;
import android.os.Bundle;
import com.google.firebase.analytics.FirebaseAnalytics;
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesControl;

public class AnalyticsLogger {
  private FirebaseAnalytics firebaseAnalytics;
  private final String CATEGORY_ENTRY = "entry";
  private final String EVENT_FAVORITE_ENTRY = "favorite_entry";
  private final String EVENT_UNFAVORITE_ENTRY = "unfavorite_entry";
  private final String EVENT_ADD_WIDGET = "add_widget";
  private final String EVENT_REMOVE_WIDGET = "remove_widget";
  private final String EVENT_UPDATED_WIDGET = "updated_widget";
  private final String EVENT_CSV_SUCCESS = "csv_success";
  private final String EVENT_CSV_FAILED = "csv_failed";
  private final String PARAM_JLPT_LEVEL = "jlpt_level";

  public AnalyticsLogger(Context context) {
    this.firebaseAnalytics = FirebaseAnalytics.getInstance(context);
  }

  public void logAppOpenEvent() {
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, new Bundle());
  }

  public void logSearchResultsOrViewItemList(PagedEntriesControl pagedEntriesControl) {
    if (pagedEntriesControl.searchType.equals(PagedEntriesControl.SEARCH)) {
      logSearchResults(pagedEntriesControl);
    } else {
      logViewItemList(pagedEntriesControl);
    }
  }

  public void logViewItem(int entryId, String expression) {
    Bundle bundle = entryBundle(entryId, expression);
    bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, CATEGORY_ENTRY);
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
  }

  public void logToggleFavoriteEvent(int entryId, String expression, boolean favorited) {
    Bundle bundle = entryBundle(entryId, expression);
    if (favorited) {
      firebaseAnalytics.logEvent(EVENT_FAVORITE_ENTRY, bundle);
    } else {
      firebaseAnalytics.logEvent(EVENT_UNFAVORITE_ENTRY, bundle);
    }
  }

  public void logAddWidget() {
    firebaseAnalytics.logEvent(EVENT_ADD_WIDGET, new Bundle());
  }

  public void logDeleteWidget() {
    firebaseAnalytics.logEvent(EVENT_REMOVE_WIDGET,  new Bundle());
  }

  public void logWidgetUpdated(int jlptLevel, int entryId, String expression) {
    Bundle bundle = entryBundle(entryId, expression);
    bundle.putString(PARAM_JLPT_LEVEL, String.valueOf(jlptLevel));
    firebaseAnalytics.logEvent(EVENT_UPDATED_WIDGET, bundle);
  }

  public void logCsvSuccess() {
    firebaseAnalytics.logEvent(EVENT_CSV_SUCCESS,  new Bundle());
  }

  public void logCsvFailed() {
    firebaseAnalytics.logEvent(EVENT_CSV_FAILED,  new Bundle());
  }

  private Bundle entryBundle(int entryId, String expression) {
    Bundle bundle = new Bundle();
    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(entryId));
    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, expression);
    return bundle;
  }

  private void logSearchResults(PagedEntriesControl pagedEntriesControl) {
    Bundle bundle = new Bundle();
    bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, pagedEntriesControl.searchTerm);
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS, bundle);
  }

  private void logViewItemList(PagedEntriesControl pagedEntriesControl) {
    Bundle bundle = new Bundle();
    bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, pagedEntriesControl.searchType);
    if (pagedEntriesControl.searchType.equals(PagedEntriesControl.JLPT)) {
      bundle.putString(PARAM_JLPT_LEVEL, String.valueOf(pagedEntriesControl.jlptLevel));
    }
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle);
  }
}
