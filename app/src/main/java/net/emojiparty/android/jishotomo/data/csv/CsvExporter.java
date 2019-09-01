package net.emojiparty.android.jishotomo.data.csv;

import android.content.Context;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.emojiparty.android.jishotomo.data.AppRepository;
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses;
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesControl;

public class CsvExporter {
  public interface ExportCallback {
    void onUpdateProgress(Integer progress);

    void onFailure(Exception exception);
  }

  public static String fileLocation(Context context) {
    return (context.getExternalFilesDir("csv_export") + "/jisho_tomo_export.csv");
  }

  private Context context;
  private ExportCallback callback;

  public CsvExporter(Context context) {
    this.context = context;
  }

  public void setCallback(ExportCallback callback) {
    this.callback = callback;
  }

  // https://www.callicoder.com/java-read-write-csv-file-opencsv/
  // https://stackoverflow.com/questions/11341931/how-to-create-a-csv-on-android
  public void export(String searchType, Integer jlptLevel) {
    CSVWriter writer;
    List<EntryWithAllSenses> entries = new ArrayList<>();

    try {
      entries = entriesForSearchType(searchType, jlptLevel);
    } catch (CsvForbiddenExportTypeException exception) {
      callback.onFailure(exception);
    }

    try {
      writer = semicolonSeparatedWriter(CsvExporter.fileLocation(context));
      int totalCount = entries.size();
      for (int i = 0; i < totalCount; i++) {
        EntryWithAllSenses entry = entries.get(i);
        writer.writeNext(new CsvEntry(entry, context).toArray());
        callback.onUpdateProgress((i + 1) * 100 / totalCount);
      }
      writer.close();
    } catch (IOException exception) {
      callback.onFailure(exception);
    }
  }

  private List<EntryWithAllSenses> entriesForSearchType(String searchType, Integer jlptLevel) {
    AppRepository appRepo = new AppRepository();

    switch (searchType) {
      case PagedEntriesControl.FAVORITES:
        return appRepo.getAllFavorites();
      case PagedEntriesControl.JLPT:
        return appRepo.getAllByJlptLevel(jlptLevel);
      default:
        throw new CsvForbiddenExportTypeException(
            "Not allowed to export this kind of list! " + searchType);
    }
  }

  private CSVWriter semicolonSeparatedWriter(String csv) throws IOException {
    return new CSVWriter(new FileWriter(csv), ';', CSVWriter.NO_QUOTE_CHARACTER,
        CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
  }

  class CsvForbiddenExportTypeException extends RuntimeException {
    CsvForbiddenExportTypeException(String message) {
      super(message);
    }
  }
}
