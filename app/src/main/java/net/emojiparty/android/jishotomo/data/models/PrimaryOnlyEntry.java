package net.emojiparty.android.jishotomo.data.models;

public class PrimaryOnlyEntry {
  public int id;
  public String primaryKanji;
  public String primaryReading;

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
