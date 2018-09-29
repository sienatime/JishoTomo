package net.emojiparty.android.jishotomo.data.csv;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import net.emojiparty.android.jishotomo.data.AppRepository;
import net.emojiparty.android.jishotomo.data.SemicolonSplit;
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses;
import net.emojiparty.android.jishotomo.data.models.SenseWithCrossReferences;
import net.emojiparty.android.jishotomo.data.room.Sense;
import net.emojiparty.android.jishotomo.ui.SenseDisplay;

public class CsvExporter {
  private SenseDisplay senseDisplay;

  public CsvExporter(Context context) {
    this.senseDisplay = new SenseDisplay(context);
  }

  // https://www.callicoder.com/java-read-write-csv-file-opencsv/
  // https://stackoverflow.com/questions/11341931/how-to-create-a-csv-on-android
  // TODO: switch on type to export different kinds of lists (faves or jlpt only)
  public void export() {
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

  private String meaning(EntryWithAllSenses entry) {
    StringBuilder builder = new StringBuilder();
    int numberOfSenses = entry.getSenses().size();
    for (int i = 0; i < numberOfSenses; i++) {
      SenseWithCrossReferences sense = entry.getSenses().get(i);

      appendPartsOfSpeech(builder, sense.getSense());
      if (numberOfSenses > 1) {
        int index = i + 1;
        builder.append(index);
        builder.append(". ");
      }
      builder.append(SemicolonSplit.splitAndJoin(sense.getSense().getGlosses()));
      builder.append("<br/>");
    }
    return builder.toString();
  }

  private void appendPartsOfSpeech(StringBuilder builder, Sense sense) {
    if (sense.getPartsOfSpeech() != null) {
      builder.append(senseDisplay.formatPartsOfSpeech(sense));
      builder.append("<br/>");
    }
  }

  private String reading(EntryWithAllSenses entry) {
    if (entry.hasKanji()) {
      return String.format("%s[%s]", entry.entry.getPrimaryKanji(), entry.entry.getPrimaryReading());
    } else {
      return entry.entry.getPrimaryReading();
    }
  }

  private CSVWriter semicolonSeparatedWriter(String csv) throws IOException {
    return new CSVWriter(new FileWriter(csv), ';', CSVWriter.NO_QUOTE_CHARACTER,
        CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
  }

  class CsvForbiddenExportTypeException extends RuntimeException {
    public CsvForbiddenExportTypeException(String message) {
      super(message);
    }
  }
}
