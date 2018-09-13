package net.emojiparty.android.jishotomo.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import net.emojiparty.android.jishotomo.R;
import net.emojiparty.android.jishotomo.data.AppRepository;
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry;
import net.emojiparty.android.jishotomo.ui.activities.DefinitionActivity;

import static net.emojiparty.android.jishotomo.ui.activities.DefinitionActivity.ENTRY_ID_EXTRA;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link JishoTomoJlptWidgetConfigureActivity JishoTomoJlptWidgetConfigureActivity}
 */
public class JishoTomoJlptWidget extends AppWidgetProvider {

  static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

    int selectedLevel =
        JishoTomoJlptWidgetConfigureActivity.loadJlptLevelPref(context, appWidgetId);
    AppRepository appRepo = new AppRepository();

    appRepo.getRandomEntryByJlptLevel(selectedLevel, (SearchResultEntry entry) -> {
      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.jisho_tomo_jlpt_widget);
      views.setTextViewText(R.id.widget_kanji, entry.getKanjiOrReading());
      views.setTextViewText(R.id.widget_reading, entry.getReading());
      int readingVisible = entry.getReading() == null ? View.GONE : View.VISIBLE;
      views.setViewVisibility(R.id.widget_reading, readingVisible);
      views.setTextViewText(R.id.widget_gloss, entry.getPrimaryGloss());

      Intent appIntent = new Intent(context, DefinitionActivity.class);
      appIntent.putExtra(ENTRY_ID_EXTRA, entry.id);

      PendingIntent
          appPendingIntent = PendingIntent.getActivity(context, appWidgetId, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);

      views.setOnClickPendingIntent(R.id.widget_container, appPendingIntent);

      appWidgetManager.updateAppWidget(appWidgetId, views);
    });
  }

  @Override public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    for (int appWidgetId : appWidgetIds) {
      updateAppWidget(context, appWidgetManager, appWidgetId);
    }
  }

  @Override public void onDeleted(Context context, int[] appWidgetIds) {
    for (int appWidgetId : appWidgetIds) {
      JishoTomoJlptWidgetConfigureActivity.deleteJlptLevelPref(context, appWidgetId);
    }
  }
}

