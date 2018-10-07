package net.emojiparty.android.jishotomo.ui.presentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.emojiparty.android.jishotomo.data.SemicolonSplit;
import net.emojiparty.android.jishotomo.data.models.CrossReferencedEntry;

public class CrossReferenceText {
  public static String format(List<CrossReferencedEntry> crossReferencedEntries) {
    ArrayList<String> linkTexts = new ArrayList<>();
    for (CrossReferencedEntry entry : crossReferencedEntries) {
      linkTexts.add(entryLink(entry));
    }

    return SemicolonSplit.join(linkTexts);
  }

  private static String entryLink(CrossReferencedEntry entry) {
    return String.format(Locale.US,
        "<a href='net.emojiparty.android.jishotomo://definition/%d'>%s</a>", entry.id,
        entry.getKanjiOrReading());
  }
}
