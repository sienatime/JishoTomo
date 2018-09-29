package net.emojiparty.android.jishotomo.data.csv;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import net.emojiparty.android.jishotomo.data.AppRepository;
import net.emojiparty.android.jishotomo.data.SemicolonSplit;
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses;
import net.emojiparty.android.jishotomo.data.models.SenseWithCrossReferences;
import net.emojiparty.android.jishotomo.data.room.Sense;
import net.emojiparty.android.jishotomo.ui.SenseDisplay;
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesControl;

public class CsvExporter {
  private SenseDisplay senseDisplay;

  public CsvExporter(Context context) {
    this.senseDisplay = new SenseDisplay(context);
  }

  // https://www.callicoder.com/java-read-write-csv-file-opencsv/
  // https://stackoverflow.com/questions/11341931/how-to-create-a-csv-on-android
  public void export(String searchType, Integer jlptLevel) {
    AsyncTask.execute(() -> {
      String csv =
          (Environment.getExternalStorageDirectory().getAbsolutePath() + "/jisho_tomo_export.csv");
      CSVWriter writer = null;

      try {
        writer = semicolonSeparatedWriter(csv);
        for (EntryWithAllSenses entry : entriesForSearchType(searchType, jlptLevel)) {
          writer.writeNext(new String[]{entry.getKanjiOrReading(), meaning(entry), reading(entry)});
        }
        writer.close();
      } catch (IOException exception) {
        exception.toString();
      }
    });
  }

  private List<EntryWithAllSenses> entriesForSearchType(String searchType, Integer jlptLevel) {
    AppRepository appRepo = new AppRepository();

    switch (searchType) {
      case PagedEntriesControl.FAVORITES:
        return appRepo.getAllFavorites();
      case PagedEntriesControl.JLPT:
        return appRepo.getAllByJlptLevel(jlptLevel);
      default:
        throw new CsvForbiddenExportTypeException("Not allowed to export this kind of list! " + searchType);
    }
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
