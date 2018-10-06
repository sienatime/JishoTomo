package net.emojiparty.android.jishotomo.ui.activities;

import android.content.Context;
import android.os.AsyncTask;
import net.emojiparty.android.jishotomo.data.csv.CsvExporter;
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesControl;

public class CsvExportAsyncTask extends AsyncTask<Context, Integer, Void> {
  private DrawerActivity.CsvExportUiCallbacks uiCallbacks;
  private PagedEntriesControl pagedEntriesControl;

  CsvExportAsyncTask(DrawerActivity.CsvExportUiCallbacks uiCallbacks, PagedEntriesControl pagedEntriesControl) {
    super();
    this.uiCallbacks = uiCallbacks;
    this.pagedEntriesControl = pagedEntriesControl;
  }

  @Override protected void onPreExecute() {
    super.onPreExecute();
    uiCallbacks.onPreExecute();
  }

  @Override protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);
    uiCallbacks.onPostExecute();
  }

  @Override protected void onProgressUpdate(Integer... values) {
    super.onProgressUpdate(values);
    uiCallbacks.onProgressUpdate(values[0]);
  }

  @Override protected void onCancelled() {
    super.onCancelled();
    uiCallbacks.onCanceled();
  }

  @Override protected Void doInBackground(Context... contexts) {
    Context context = contexts[0];
    CsvExporter csvExporter = new CsvExporter(context);

    csvExporter.setCallback(new CsvExporter.ExportCallback() {
      @Override public void onUpdateProgress(Integer progress) {
        onProgressUpdate(progress);
      }

      @Override public void onFailure(Exception exception) {
        cancel(true);
      }
    });

    csvExporter.export(pagedEntriesControl.searchType,
        pagedEntriesControl.jlptLevel);

    return null;
  }
}
