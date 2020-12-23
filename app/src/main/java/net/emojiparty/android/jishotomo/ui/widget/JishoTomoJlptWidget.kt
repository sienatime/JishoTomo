package net.emojiparty.android.jishotomo.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.TaskStackBuilder
import kotlinx.coroutines.runBlocking
import net.emojiparty.android.jishotomo.JishoTomoApp
import net.emojiparty.android.jishotomo.R.id
import net.emojiparty.android.jishotomo.R.layout
import net.emojiparty.android.jishotomo.analytics.AnalyticsLogger
import net.emojiparty.android.jishotomo.data.AppRepository
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry
import net.emojiparty.android.jishotomo.ui.activities.DefinitionActivity
import net.emojiparty.android.jishotomo.ui.activities.DefinitionFragment
import net.emojiparty.android.jishotomo.ui.presentation.AndroidResourceFetcher

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [ JishoTomoJlptWidgetConfigureActivity][JishoTomoJlptWidgetConfigureActivity]
 */
class JishoTomoJlptWidget : AppWidgetProvider() {
  override fun onEnabled(context: Context) {
    super.onEnabled(context)
    getAnalyticsLoggerFromContext(context)
      .logAddWidget()
  }

  override fun onUpdate(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray
  ) {
    for (appWidgetId in appWidgetIds) {
      updateAppWidget(context, appWidgetManager, appWidgetId)
    }
  }

  override fun onDeleted(
    context: Context,
    appWidgetIds: IntArray
  ) {
    for (appWidgetId in appWidgetIds) {
      JishoTomoJlptWidgetConfigureActivity.deleteJlptLevelPref(context, appWidgetId)
      getAnalyticsLoggerFromContext(context).logDeleteWidget()
    }
  }

  companion object {
    fun updateAppWidget(
      context: Context,
      appWidgetManager: AppWidgetManager,
      appWidgetId: Int
    ) {
      val selectedLevel = JishoTomoJlptWidgetConfigureActivity.loadJlptLevelPref(context, appWidgetId)
      val appRepo = AppRepository()
      val entry = runBlocking {
        appRepo.getRandomEntryByJlptLevel(selectedLevel)
      }

      val views = configureViewWithEntry(
        selectedLevel, entry, context, appWidgetId
      )

      appWidgetManager.updateAppWidget(appWidgetId, views)
      getAnalyticsLoggerFromContext(context)
        .logWidgetUpdated(
          selectedLevel,
          entry.id,
          entry.kanjiOrReading
        )
    }

    private fun configureViewWithEntry(
      selectedLevel: Int,
      entry: SearchResultEntry,
      context: Context,
      appWidgetId: Int
    ): RemoteViews {
      val views = RemoteViews(context.packageName, layout.jisho_tomo_jlpt_widget)
      views.setTextViewText(id.widget_kanji, entry.kanjiOrReading)
      views.setTextViewText(id.widget_reading, entry.reading)

      val readingVisible = if (entry.reading == null) View.GONE else View.VISIBLE
      views.setViewVisibility(id.widget_reading, readingVisible)
      views.setTextViewText(id.widget_gloss, entry.getPrimaryGloss())

      val jlptStringId = AndroidResourceFetcher(context.resources, context.packageName).stringForJlptLevel(selectedLevel)
      views.setTextViewText(id.widget_level, context.getString(jlptStringId))
      val appPendingIntent = openDefinitionActivity(entry, context, appWidgetId)
      views.setOnClickPendingIntent(id.widget_container, appPendingIntent)

      return views
    }

    private fun openDefinitionActivity(
      entry: SearchResultEntry,
      context: Context,
      appWidgetId: Int
    ): PendingIntent? {
      val appIntent = Intent(context, DefinitionActivity::class.java).apply {
        this.putExtra(DefinitionFragment.ENTRY_ID_EXTRA, entry.id)
      }
      return TaskStackBuilder.create(context)
        .addNextIntentWithParentStack(appIntent)
        .getPendingIntent(appWidgetId, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getAnalyticsLoggerFromContext(context: Context): AnalyticsLogger {
      val appContext = context.applicationContext
      val app = appContext as JishoTomoApp
      return app.analyticsLogger
    }
  }
}
