package net.emojiparty.android.jishotomo.ui.presentation;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import java.io.File;
import net.emojiparty.android.jishotomo.JishoTomoApp;
import net.emojiparty.android.jishotomo.R;
import net.emojiparty.android.jishotomo.analytics.AnalyticsLogger;
import net.emojiparty.android.jishotomo.data.AppRepository;
import net.emojiparty.android.jishotomo.data.csv.CsvExporter;
import net.emojiparty.android.jishotomo.ui.activities.CsvExportAsyncTask;
import net.emojiparty.android.jishotomo.ui.dialogs.CallbackDialog;
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesControl;

public class FavoritesMenu {
  private AnalyticsLogger analyticsLogger;

  public FavoritesMenu(JishoTomoApp app) {
    analyticsLogger = app.getAnalyticsLogger();
  }

  public static void setButtonVisibility(Menu menu, boolean visible) {
    MenuItem exportIcon = menu.getItem(1);
    MenuItem unfavoriteAllIcon = menu.getItem(2);
    exportIcon.setVisible(visible);
    unfavoriteAllIcon.setVisible(visible);
  }

  public void explainUnfavoriteAll(FragmentActivity activity) {
    CallbackDialog dialog = new CallbackDialog(
        this::unfavoriteAll,
        R.string.explain_unfavorite_all,
        R.string.okay
    );
    dialog.show(activity.getSupportFragmentManager(), "unfavorite_all_explain");
  }

  private void unfavoriteAll() {
    new AppRepository().unfavoriteAll();
    analyticsLogger.logUnfavoriteAll();
  }

  public void explainCsvExport(FragmentActivity activity, PagedEntriesControl pagedEntriesControl) {
    CallbackDialog dialog = new CallbackDialog(
        () -> checkForPermissionThenExport(activity, pagedEntriesControl),
        R.string.export_instructions,
        R.string.export_yes
    );
    dialog.show(activity.getSupportFragmentManager(), "export_explain");
  }

  // https://developer.android.com/training/data-storage/files#InternalVsExternalStorage
  private boolean isExternalStorageWritable() {
    String state = Environment.getExternalStorageState();
    return Environment.MEDIA_MOUNTED.equals(state);
  }

  private void checkForPermissionThenExport(FragmentActivity activity, PagedEntriesControl pagedEntriesControl) {
    if (!isExternalStorageWritable()) {
      Toast.makeText(activity, R.string.no_external_storage, Toast.LENGTH_LONG).show();
      return;
    }

    exportCsv(activity, pagedEntriesControl);
  }

  private void exportCsv(FragmentActivity activity, PagedEntriesControl pagedEntriesControl) {
    ProgressBar exportIndicator = activity.findViewById(R.id.exporting);

    CsvExportAsyncTask.CsvExportUiCallbacks uiCallbacks =
        new CsvExportAsyncTask.CsvExportUiCallbacks() {
          @Override public void onProgressUpdate(Integer progress) {
            exportIndicator.setProgress(progress);
          }

          @Override public void onPreExecute() {
            exportIndicator.setVisibility(View.VISIBLE);
          }

          @Override public void onPostExecute() {
            exportIndicator.setVisibility(View.GONE);
            File csv = new File(CsvExporter.fileLocation(activity));
            Uri csvUri =
                FileProvider.getUriForFile(activity, activity.getString(R.string.fileprovider_package), csv);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, csvUri);
            shareIntent.setType("text/csv");
            activity.startActivity(Intent.createChooser(shareIntent, activity.getString(R.string.share_csv)));
            analyticsLogger.logCsvSuccess();
          }

          @Override public void onCanceled() {
            exportIndicator.setVisibility(View.GONE);
            Toast.makeText(activity, R.string.csv_failed, Toast.LENGTH_SHORT).show();
            analyticsLogger.logCsvFailed();
          }
        };

    new CsvExportAsyncTask(uiCallbacks, pagedEntriesControl).execute(activity);
  }
}
