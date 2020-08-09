package net.emojiparty.android.jishotomo.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.TaskStackBuilder;
import android.view.View;
import android.widget.RemoteViews;
import net.emojiparty.android.jishotomo.JishoTomoApp;
import net.emojiparty.android.jishotomo.R;
import net.emojiparty.android.jishotomo.analytics.AnalyticsLogger;
import net.emojiparty.android.jishotomo.data.AppRepository;
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry;
import net.emojiparty.android.jishotomo.ui.activities.DefinitionActivity;
import net.emojiparty.android.jishotomo.ui.presentation.StringForJlptLevel;

import static net.emojiparty.android.jishotomo.ui.activities.DefinitionFragment.ENTRY_ID_EXTRA;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link JishoTomoJlptWidgetConfigureActivity
 * JishoTomoJlptWidgetConfigureActivity}
 */
public class JishoTomoJlptWidget extends AppWidgetProvider {

  static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
    int selectedLevel =
        JishoTomoJlptWidgetConfigureActivity.loadJlptLevelPref(context, appWidgetId);
    AppRepository appRepo = new AppRepository();

    appRepo.getRandomEntryByJlptLevel(selectedLevel, (SearchResultEntry entry) -> {
      RemoteViews views = configureViewWithEntry(selectedLevel, entry, context, appWidgetId);
      appWidgetManager.updateAppWidget(appWidgetId, views);
      getAnalyticsLoggerFromContext(context).logWidgetUpdated(selectedLevel, entry.getId(),
          entry.kanjiOrReading());
    });
  }

  private static RemoteViews configureViewWithEntry(int selectedLevel, SearchResultEntry entry,
      Context context, int appWidgetId) {
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.jisho_tomo_jlpt_widget);
    views.setTextViewText(R.id.widget_kanji, entry.kanjiOrReading());
    views.setTextViewText(R.id.widget_reading, entry.reading());
    int readingVisible = entry.reading() == null ? View.GONE : View.VISIBLE;
    views.setViewVisibility(R.id.widget_reading, readingVisible);
    views.setTextViewText(R.id.widget_gloss, entry.getPrimaryGloss());

    int jlptStringId = StringForJlptLevel.getId(selectedLevel, context);
    views.setTextViewText(R.id.widget_level, context.getString(jlptStringId));

    PendingIntent appPendingIntent = openDefinitionActivity(entry, context, appWidgetId);
    views.setOnClickPendingIntent(R.id.widget_container, appPendingIntent);
    return views;
  }

  private static PendingIntent openDefinitionActivity(SearchResultEntry entry, Context context, int appWidgetId) {
    Intent appIntent = new Intent(context, DefinitionActivity.class);
    appIntent.putExtra(ENTRY_ID_EXTRA, entry.getId());

    return TaskStackBuilder.create(context)
        .addNextIntentWithParentStack(appIntent)
        .getPendingIntent(appWidgetId, PendingIntent.FLAG_UPDATE_CURRENT);
  }

  @Override public void onEnabled(Context context) {
    super.onEnabled(context);
    getAnalyticsLoggerFromContext(context).logAddWidget();
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    for (int appWidgetId : appWidgetIds) {
      updateAppWidget(context, appWidgetManager, appWidgetId);
    }
  }

  @Override public void onDeleted(Context context, int[] appWidgetIds) {
    for (int appWidgetId : appWidgetIds) {
      JishoTomoJlptWidgetConfigureActivity.deleteJlptLevelPref(context, appWidgetId);
      getAnalyticsLoggerFromContext(context).logDeleteWidget();
    }
  }

  private static AnalyticsLogger getAnalyticsLoggerFromContext(Context context) {
    Context appContext = context.getApplicationContext();
    JishoTomoApp app = (JishoTomoApp) appContext;
    return app.getAnalyticsLogger();
  }
}
