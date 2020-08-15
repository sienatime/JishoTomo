package net.emojiparty.android.jishotomo.data.csv

import android.content.Context
import com.opencsv.CSVWriter
import net.emojiparty.android.jishotomo.data.AppRepository
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses
import net.emojiparty.android.jishotomo.ui.presentation.AndroidResourceFetcher
import net.emojiparty.android.jishotomo.ui.presentation.SenseDisplay
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesControl
import java.io.FileWriter
import java.io.IOException
import java.util.ArrayList

class CsvExporter(
  private val context: Context,
  val onUpdateProgress: (progress: Int) -> Unit,
  val onFailure: (exception: Exception) -> Unit
) {

  // https://www.callicoder.com/java-read-write-csv-file-opencsv/
  // https://stackoverflow.com/questions/11341931/how-to-create-a-csv-on-android
  fun export(
    searchType: String,
    jlptLevel: Int
  ) {
    val writer: CSVWriter
    var entries: List<EntryWithAllSenses?> = ArrayList()
    try {
      entries = entriesForSearchType(searchType, jlptLevel)
    } catch (exception: CsvForbiddenExportTypeException) {
      onFailure(exception)
    }
    try {
      writer = semicolonSeparatedWriter(fileLocation(context))
      val totalCount = entries.size
      for (i in 0 until totalCount) {
        val entry = entries[i]
        writer.writeNext(
            CsvEntry(
                entry!!, SenseDisplay(
                AndroidResourceFetcher(context.resources), context.packageName
            )
            ).toArray()
        )
        onUpdateProgress((i + 1) * 100 / totalCount)
      }
      writer.close()
    } catch (exception: IOException) {
      onFailure(exception)
    }
  }

  private fun entriesForSearchType(
    searchType: String,
    jlptLevel: Int
  ): List<EntryWithAllSenses?> {
    val appRepo = AppRepository()
    return when (searchType) {
      PagedEntriesControl.FAVORITES -> appRepo.getAllFavorites()
      PagedEntriesControl.JLPT -> appRepo.getAllByJlptLevel(jlptLevel)
      else -> throw CsvForbiddenExportTypeException(
          "Not allowed to export this kind of list! $searchType"
      )
    }
  }

  @Throws(IOException::class)
  private fun semicolonSeparatedWriter(csv: String): CSVWriter {
    return CSVWriter(
        FileWriter(csv), ';', CSVWriter.NO_QUOTE_CHARACTER,
        CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END
    )
  }

  internal inner class CsvForbiddenExportTypeException(message: String?) : RuntimeException(
      message
  )

  companion object {
    @JvmStatic fun fileLocation(context: Context): String {
      return context.getExternalFilesDir("csv_export").toString() + "/jisho_tomo_export.csv"
    }
  }

}
