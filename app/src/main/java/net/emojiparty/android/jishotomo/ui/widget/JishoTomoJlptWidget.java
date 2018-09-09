package net.emojiparty.android.jishotomo.ui.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;
import net.emojiparty.android.jishotomo.R;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link JishoTomoJlptWidgetConfigureActivity JishoTomoJlptWidgetConfigureActivity}
 */
public class JishoTomoJlptWidget extends AppWidgetProvider {

  static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

    int selectedLevel =
        JishoTomoJlptWidgetConfigureActivity.loadJlptLevelPref(context, appWidgetId);
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.jisho_tomo_jlpt_widget);
    views.setTextViewText(R.id.appwidget_text, String.valueOf(selectedLevel));

    appWidgetManager.updateAppWidget(appWidgetId, views);
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

