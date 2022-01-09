package net.emojiparty.android.jishotomo.ui.dialogs

import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import net.emojiparty.android.jishotomo.R.string
import net.emojiparty.android.jishotomo.data.csv.CsvExporter
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses
import net.emojiparty.android.jishotomo.ui.csv.CsvExportUiCallbacks

class ExportDialog(
  private val deferredEntries: Deferred<List<EntryWithAllSenses>>,
  private val uiCallbacks: CsvExportUiCallbacks
) : CallbackDialog(
  string.export_instructions,
  string.export_yes
) {

  override fun onConfirm() {
    checkForPermissionThenExport()
  }

  override val dialogTag: String = "export_explain"

  // https://developer.android.com/training/data-storage/files#InternalVsExternalStorage
  private val isExternalStorageWritable: Boolean
    get() {
      return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }

  private fun checkForPermissionThenExport() {
    if (!isExternalStorageWritable) {
      Toast.makeText(activity, string.no_external_storage, Toast.LENGTH_LONG).show()
      return
    }
    exportCsv()
  }

  private fun exportCsv() {
    dismiss()
    activity?.lifecycleScope?.launch {
      val csvExporter = CsvExporter(
        requireContext(),
        { progress ->
          uiCallbacks.onProgressUpdate(progress)
        },
        {
          uiCallbacks.onCancelled()
        }
      )

      uiCallbacks.onPreExecute()

      csvExporter.export(deferredEntries.await())

      uiCallbacks.onPostExecute()
    }
  }
}
