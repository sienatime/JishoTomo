package net.emojiparty.android.jishotomo.ui.csv

import android.content.Context
import android.os.AsyncTask
import net.emojiparty.android.jishotomo.data.csv.CsvExporter
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesControl

class CsvExportAsyncTask(
  private val uiCallbacks: CsvExportUiCallbacks,
  private val pagedEntriesControl: PagedEntriesControl
) : AsyncTask<Context?, Int?, Void?>() {
  interface CsvExportUiCallbacks {
    fun onProgressUpdate(progress: Int?)
    fun onPreExecute()
    fun onPostExecute()
    fun onCancelled()
  }

  override fun onPreExecute() {
    super.onPreExecute()
    uiCallbacks.onPreExecute()
  }

  override fun onPostExecute(aVoid: Void?) {
    super.onPostExecute(aVoid)
    uiCallbacks.onPostExecute()
  }

  override fun onProgressUpdate(vararg values: Int?) {
    super.onProgressUpdate(*values)
    uiCallbacks.onProgressUpdate(values[0])

  }

  override fun onCancelled() {
    super.onCancelled()
    uiCallbacks.onCancelled()
  }

  override fun doInBackground(vararg params: Context?): Void? {
    params[0]?.let { context ->
      val csvExporter = CsvExporter(
          context,
          { progress ->
            onProgressUpdate(progress)
          },
          {
            cancel(true)
          }
      )

      csvExporter.export(pagedEntriesControl)
    }

    return null
  }
}
