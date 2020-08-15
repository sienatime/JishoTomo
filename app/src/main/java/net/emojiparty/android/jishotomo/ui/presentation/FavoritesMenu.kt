package net.emojiparty.android.jishotomo.ui.presentation

import android.content.Intent
import android.os.Environment
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import net.emojiparty.android.jishotomo.R.id
import net.emojiparty.android.jishotomo.R.string
import net.emojiparty.android.jishotomo.analytics.AnalyticsLogger
import net.emojiparty.android.jishotomo.data.AppRepository
import net.emojiparty.android.jishotomo.data.csv.CsvExporter.Companion.fileLocation
import net.emojiparty.android.jishotomo.ui.csv.CsvExportAsyncTask
import net.emojiparty.android.jishotomo.ui.csv.CsvExportAsyncTask.CsvExportUiCallbacks
import net.emojiparty.android.jishotomo.ui.dialogs.CallbackDialog
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesControl
import java.io.File

class FavoritesMenu(
  private val analyticsLogger: AnalyticsLogger
) {

  fun explainUnfavoriteAll(activity: FragmentActivity) {
    val dialog = CallbackDialog(
      string.explain_unfavorite_all,
      string.okay,
      ::unfavoriteAll
    )
    dialog.show(activity.supportFragmentManager, "unfavorite_all_explain")
  }

  private fun unfavoriteAll() {
    AppRepository().unfavoriteAll()
    analyticsLogger.logUnfavoriteAll()
  }

  fun explainCsvExport(
    activity: FragmentActivity,
    pagedEntriesControl: PagedEntriesControl
  ) {
    val dialog = CallbackDialog(
      string.export_instructions,
      string.export_yes
    ) { checkForPermissionThenExport(activity, pagedEntriesControl) }
    dialog.show(activity.supportFragmentManager, "export_explain")
  }

  // https://developer.android.com/training/data-storage/files#InternalVsExternalStorage
  private val isExternalStorageWritable: Boolean
    get() {
      return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }

  private fun checkForPermissionThenExport(
    activity: FragmentActivity,
    pagedEntriesControl: PagedEntriesControl
  ) {
    if (!isExternalStorageWritable) {
      Toast.makeText(activity, string.no_external_storage, Toast.LENGTH_LONG).show()
      return
    }
    exportCsv(activity, pagedEntriesControl)
  }

  private fun exportCsv(
    activity: FragmentActivity,
    pagedEntriesControl: PagedEntriesControl
  ) {
    val exportIndicator = activity.findViewById<ProgressBar>(id.exporting)

    val uiCallbacks: CsvExportUiCallbacks = object : CsvExportUiCallbacks {
      override fun onProgressUpdate(progress: Int?) {
        exportIndicator.progress = progress!!
      }

      override fun onPreExecute() {
        exportIndicator.visibility = View.VISIBLE
      }

      override fun onPostExecute() {
        exportIndicator.visibility = View.GONE
        val csv = File(fileLocation(activity))
        val csvUri = FileProvider.getUriForFile(
          activity, activity.getString(string.fileprovider_package), csv
        )
        val shareIntent = Intent().apply {
          this.action = Intent.ACTION_SEND
          this.putExtra(Intent.EXTRA_STREAM, csvUri)
          this.type = "text/csv"
        }
        activity.startActivity(
          Intent.createChooser(shareIntent, activity.getString(string.share_csv))
        )
        analyticsLogger.logCsvSuccess()
      }

      override fun onCancelled() {
        exportIndicator.visibility = View.GONE
        Toast.makeText(activity, string.csv_failed, Toast.LENGTH_SHORT).show()
        analyticsLogger.logCsvFailed()
      }
    }
    CsvExportAsyncTask(uiCallbacks, pagedEntriesControl).execute(activity)
  }
}
