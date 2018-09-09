package net.emojiparty.android.jishotomo.ui.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import net.emojiparty.android.jishotomo.R;

/**
 * The configuration screen for the {@link JishoTomoJlptWidget JishoTomoJlptWidget} AppWidget.
 */
public class JishoTomoJlptWidgetConfigureActivity extends Activity {

  private static final String PREFS_NAME =
      "net.emojiparty.android.jishotomo.ui.widget.JishoTomoJlptWidget";
  private static final String PREF_PREFIX_KEY = "appwidget_";
  int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
  private RadioGroup jlptButtonGroup;

  View.OnClickListener onClickListener = (View view) -> {
    final Context context = JishoTomoJlptWidgetConfigureActivity.this;

    int level = findSelectedLevel();
    saveJlptLevel(context, appWidgetId, level);

    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    JishoTomoJlptWidget.updateAppWidget(context, appWidgetManager, appWidgetId);

    Intent resultValue = new Intent();
    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
    setResult(RESULT_OK, resultValue);
    finish();
  };

  // https://stackoverflow.com/questions/6440259/how-to-get-the-selected-index-of-a-radiogroup-in-android
  private int findSelectedLevel() {
    int radioButtonID = jlptButtonGroup.getCheckedRadioButtonId();
    View radioButton = jlptButtonGroup.findViewById(radioButtonID);
    int index = jlptButtonGroup.indexOfChild(radioButton);
    return index + 1;
  }

  public JishoTomoJlptWidgetConfigureActivity() {
    super();
  }

  static void saveJlptLevel(Context context, int appWidgetId, int level) {
    SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
    prefs.putInt(PREF_PREFIX_KEY + appWidgetId, level);
    prefs.apply();
  }

  static int loadJlptLevelPref(Context context, int appWidgetId) {
    SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
    int level = prefs.getInt(PREF_PREFIX_KEY + appWidgetId, 0);
    if (level > 0) {
      return level;
    } else {
      return 1;
    }
  }

  static void deleteJlptLevelPref(Context context, int appWidgetId) {
    SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
    prefs.remove(PREF_PREFIX_KEY + appWidgetId);
    prefs.apply();
  }

  @Override public void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    // Set the result to CANCELED.  This will cause the widget host to cancel
    // out of the widget placement if the user presses the back button.
    setResult(RESULT_CANCELED);

    setContentView(R.layout.jisho_tomo_jlpt_widget_configure);
    jlptButtonGroup = findViewById(R.id.jlpt_button_group);
    findViewById(R.id.add_button).setOnClickListener(onClickListener);

    // Find the widget id from the intent.
    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    if (extras != null) {
      appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    // If this activity was started with an intent without an app widget ID, finish with an error.
    if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
      finish();
      return;
    }

    int level = loadJlptLevelPref(JishoTomoJlptWidgetConfigureActivity.this, appWidgetId);
    ((RadioButton)jlptButtonGroup.getChildAt(level - 1)).setChecked(true);
  }
}
