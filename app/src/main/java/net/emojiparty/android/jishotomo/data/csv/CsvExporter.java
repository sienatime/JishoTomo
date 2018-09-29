package net.emojiparty.android.jishotomo.data.csv;

import android.os.AsyncTask;
import android.os.Environment;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import net.emojiparty.android.jishotomo.data.AppRepository;
import net.emojiparty.android.jishotomo.data.SemicolonSplit;
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses;
import net.emojiparty.android.jishotomo.data.models.SenseWithCrossReferences;

public class CsvExporter {
  // https://www.callicoder.com/java-read-write-csv-file-opencsv/
  // https://stackoverflow.com/questions/11341931/how-to-create-a-csv-on-android
  // TODO: switch on type to export different kinds of lists (faves or jlpt only)
  public static void export() {
    AsyncTask.execute(() -> {
      String csv =
          (Environment.getExternalStorageDirectory().getAbsolutePath() + "/jisho_tomo_export.csv");
      CSVWriter writer = null;

      try {
        writer = semicolonSeparatedWriter(csv);
        for (EntryWithAllSenses entry : new AppRepository().getAllFavorites()) {
          writer.writeNext(new String[]{entry.getKanjiOrReading(), meaning(entry), reading(entry)});
        }
        writer.close();
      } catch (IOException exception) {
        exception.toString();
      }
    });
  }

  // TODO: remove index if only 1 sense
  // TODO: add alternative info after looping through senses
  private static String meaning(EntryWithAllSenses entry) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < entry.getSenses().size(); i++) {
      SenseWithCrossReferences sense = entry.getSenses().get(i);
      int index = i + 1;
      stringBuilder.append(index);
      stringBuilder.append(". ");
      stringBuilder.append(SemicolonSplit.splitAndJoin(sense.getSense().getGlosses()));
      stringBuilder.append("<br/>");
    }
    return stringBuilder.toString();
  }

  private static String reading(EntryWithAllSenses entry) {
    if (entry.hasKanji()) {
      return String.format("%s[%s]", entry.entry.getPrimaryKanji(), entry.entry.getPrimaryReading());
    } else {
      return entry.entry.getPrimaryReading();
    }
  }

  private static CSVWriter semicolonSeparatedWriter(String csv) throws IOException {
    return new CSVWriter(new FileWriter(csv), ';', CSVWriter.NO_QUOTE_CHARACTER,
        CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
  }

  class CsvForbiddenExportTypeException extends RuntimeException {
    public CsvForbiddenExportTypeException(String message) {
      super(message);
    }
  }
}
