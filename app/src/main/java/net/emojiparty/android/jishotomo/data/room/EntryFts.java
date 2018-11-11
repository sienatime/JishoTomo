package net.emojiparty.android.jishotomo.data.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;

@Fts4(contentEntity = Entry.class)
@Entity(tableName = "entriesFts")
public class EntryFts {
  @ColumnInfo(name = "primary_kanji")
  private String primaryKanji;

  @NonNull
  @ColumnInfo(name = "primary_reading")
  private String primaryReading;

  @ColumnInfo(name = "other_kanji")
  private String otherKanji;

  @ColumnInfo(name = "other_readings")
  private String otherReadings;

  public EntryFts(String primaryKanji, @NonNull String primaryReading, String otherKanji,
      String otherReadings) {
    this.primaryKanji = primaryKanji;
    this.primaryReading = primaryReading;
    this.otherKanji = otherKanji;
    this.otherReadings = otherReadings;
  }

  public String getPrimaryKanji() {
    return primaryKanji;
  }

  @NonNull public String getPrimaryReading() {
    return primaryReading;
  }

  public String getOtherKanji() {
    return otherKanji;
  }

  public String getOtherReadings() {
    return otherReadings;
  }
}
