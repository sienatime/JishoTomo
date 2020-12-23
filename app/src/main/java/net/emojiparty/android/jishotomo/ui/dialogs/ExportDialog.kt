package net.emojiparty.android.jishotomo.ui.dialogs

import android.os.Environment
import android.widget.Toast
import net.emojiparty.android.jishotomo.R.string
import net.emojiparty.android.jishotomo.ui.csv.CsvExportAsyncTask
import net.emojiparty.android.jishotomo.ui.csv.CsvExportAsyncTask.CsvExportUiCallbacks
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesControl

class ExportDialog(
  private val pagedEntriesControl: PagedEntriesControl,
  private val uiCallbacks: CsvExportUiCallbacks
) : CallbackDialog(
  string.export_instructions,
  string.export_yes
) {

  override fun onConfirm() {
    checkForPermissionThenExport(pagedEntriesControl)
  }

  override val dialogTag: String = "export_explain"

  // https://developer.android.com/training/data-storage/files#InternalVsExternalStorage
  private val isExternalStorageWritable: Boolean
    get() {
      return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }

  private fun checkForPermissionThenExport(
    pagedEntriesControl: PagedEntriesControl
  ) {
    if (!isExternalStorageWritable) {
      Toast.makeText(activity, string.no_external_storage, Toast.LENGTH_LONG).show()
      return
    }
    exportCsv(pagedEntriesControl)
  }

  private fun exportCsv(
    pagedEntriesControl: PagedEntriesControl
  ) {
    CsvExportAsyncTask(uiCallbacks, pagedEntriesControl).execute(activity)
  }
}
