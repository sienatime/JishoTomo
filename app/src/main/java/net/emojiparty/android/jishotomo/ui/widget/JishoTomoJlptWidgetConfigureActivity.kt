package net.emojiparty.android.jishotomo.ui.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import kotlinx.android.synthetic.main.jisho_tomo_jlpt_widget_configure.add_button
import kotlinx.android.synthetic.main.jisho_tomo_jlpt_widget_configure.jlpt_button_group
import net.emojiparty.android.jishotomo.R.layout
import net.emojiparty.android.jishotomo.ui.widget.JishoTomoJlptWidget.Companion.updateAppWidget

/**
 * The configuration screen for the [JishoTomoJlptWidget] AppWidget.
 */
class JishoTomoJlptWidgetConfigureActivity : Activity() {
  private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
  private var jlptButtonGroup: RadioGroup? = null

  public override fun onCreate(bundle: Bundle?) {
    super.onCreate(bundle)

    // Set the result to CANCELED.  This will cause the widget host to cancel
    // out of the widget placement if the user presses the back button.
    setResult(RESULT_CANCELED)
    setContentView(layout.jisho_tomo_jlpt_widget_configure)
    jlptButtonGroup = jlpt_button_group
    add_button.setOnClickListener { addWidget() }

    // Find the widget id from the intent.
    intent.extras?.let {
      appWidgetId = it.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
    }

    // If this activity was started with an intent without an app widget ID, finish with an error.
    if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
      finish()
      return
    }
    val level = loadJlptLevelPref(this@JishoTomoJlptWidgetConfigureActivity, appWidgetId)
    (jlptButtonGroup?.getChildAt(level - 1) as RadioButton).isChecked = true
  }

  override fun onDestroy() {
    jlptButtonGroup = null
    super.onDestroy()
  }

  private fun addWidget() {
    val context: Context = this@JishoTomoJlptWidgetConfigureActivity
    val level = findSelectedLevel()
    saveJlptLevel(context, appWidgetId, level)

    val appWidgetManager = AppWidgetManager.getInstance(context)
    updateAppWidget(context, appWidgetManager, appWidgetId)

    val resultValue = Intent().apply {
      this.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    }
    setResult(RESULT_OK, resultValue)
    finish()
  }

  // https://stackoverflow.com/questions/6440259/how-to-get-the-selected-index-of-a-radiogroup-in-android
  private fun findSelectedLevel(): Int {
    return jlptButtonGroup?.let {
      val radioButtonID = it.checkedRadioButtonId
      val radioButton = it.findViewById<View>(radioButtonID)
      val index = it.indexOfChild(radioButton)
      return index + 1
    } ?: DEFAULT_LEVEL
  }

  companion object {
    private const val PREFS_NAME = "net.emojiparty.android.jishotomo.ui.widget.JishoTomoJlptWidget"
    private const val PREF_PREFIX_KEY = "appwidget_"
    private const val DEFAULT_LEVEL = 1

    fun saveJlptLevel(
      context: Context,
      appWidgetId: Int,
      level: Int
    ) {
      with(context.getSharedPreferences(PREFS_NAME, 0).edit()) {
        this.putInt(PREF_PREFIX_KEY + appWidgetId, level)
        this.apply()
      }
    }

    fun loadJlptLevelPref(
      context: Context,
      appWidgetId: Int
    ): Int {
      val prefs = context.getSharedPreferences(PREFS_NAME, 0)
      return prefs.getInt(PREF_PREFIX_KEY + appWidgetId, DEFAULT_LEVEL)
    }

    fun deleteJlptLevelPref(
      context: Context,
      appWidgetId: Int
    ) {
      with(context.getSharedPreferences(PREFS_NAME, 0).edit()) {
        this.remove(PREF_PREFIX_KEY + appWidgetId)
        this.apply()
      }
    }
  }
}
