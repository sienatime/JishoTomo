package net.emojiparty.android.jishotomo.ui;

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.emojiparty.android.jishotomo.R;
import net.emojiparty.android.jishotomo.data.models.CrossReferencedEntry;

public class CrossReferenceText {
  public static String format(View view, List<CrossReferencedEntry> crossReferencedEntries) {
    Resources resources = view.getResources();

    ArrayList<String> linkTexts = new ArrayList<>();
    for (CrossReferencedEntry entry : crossReferencedEntries) {
      linkTexts.add(entryLink(entry, resources));
    }

    String links = TextUtils.join(resources.getString(R.string.list_of_words_delimiter), linkTexts);
    return String.format(resources.getString(R.string.see_also_format), links);
  }

  private static String entryLink(CrossReferencedEntry entry, Resources resources) {
    Locale locale = resources.getConfiguration().locale;
    return String.format(locale,
        "<a href='net.emojiparty.android.jishotomo://definition/%d'>%s</a>", entry.id,
        entry.getKanjiOrReading());
  }
}
