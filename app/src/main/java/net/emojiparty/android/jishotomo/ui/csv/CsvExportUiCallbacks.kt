package net.emojiparty.android.jishotomo.ui.csv

interface CsvExportUiCallbacks {
  fun onProgressUpdate(progress: Int?)
  fun onPreExecute()
  fun onPostExecute()
  fun onCancelled()
}
