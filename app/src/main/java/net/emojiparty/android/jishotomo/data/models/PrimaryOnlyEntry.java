package net.emojiparty.android.jishotomo.data.models;

import androidx.room.ColumnInfo;

public class PrimaryOnlyEntry {
  public int id;
  @ColumnInfo(name = "primary_kanji")
  public String primaryKanji;
  @ColumnInfo(name = "primary_reading")
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
