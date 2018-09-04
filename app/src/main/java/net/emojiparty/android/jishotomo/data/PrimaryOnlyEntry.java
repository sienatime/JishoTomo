package net.emojiparty.android.jishotomo.data;

public class PrimaryOnlyEntry {
  int id;
  String primaryKanji;
  String primaryReading;

  public String getKanjiOrReading() {
    return hasKanji() ? primaryKanji : primaryReading;
  }

  public String getReading() {
    return hasKanji() ? primaryReading : null;
  }

  public boolean hasKanji() {
    return primaryKanji != null;
    }
}
