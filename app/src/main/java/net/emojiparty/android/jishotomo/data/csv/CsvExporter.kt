package net.emojiparty.android.jishotomo.data.csv

import android.content.Context
import com.opencsv.CSVWriter
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses
import net.emojiparty.android.jishotomo.ui.presentation.AndroidResourceFetcher
import net.emojiparty.android.jishotomo.ui.presentation.SenseDisplay
import java.io.FileWriter
import java.io.IOException

class CsvExporter(
  private val context: Context,
  val onUpdateProgress: (progress: Int) -> Unit,
  val onFailure: (exception: Exception) -> Unit
) {

  // https://www.callicoder.com/java-read-write-csv-file-opencsv/
  // https://stackoverflow.com/questions/11341931/how-to-create-a-csv-on-android
  fun export(
    entries: List<EntryWithAllSenses>
  ) {
    val writer: CSVWriter
    try {
      writer = tabSeparatedWriter(fileLocation(context))
      val totalCount = entries.size
      val senseDisplay = SenseDisplay(
        AndroidResourceFetcher(context.resources, context.packageName)
      )
      for (i in 0 until totalCount) {
        val entry = entries[i]
        writer.writeNext(
          CsvEntry(entry, senseDisplay).toArray()
        )
        onUpdateProgress((i + 1) * 100 / totalCount)
      }
      writer.close()
    } catch (exception: IOException) {
      onFailure(exception)
    }
  }

  @Throws(IOException::class)
  private fun tabSeparatedWriter(csv: String): CSVWriter {
    return CSVWriter(
      FileWriter(csv), '\t', CSVWriter.NO_QUOTE_CHARACTER,
      CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END
    )
  }

  companion object {
    fun fileLocation(context: Context): String {
      return context.getExternalFilesDir("csv_export")?.toString() + "/jisho_tomo_export.tsv"
    }
  }
}
