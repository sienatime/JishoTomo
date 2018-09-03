package net.emojiparty.android.jishotomo.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import java.util.List;

@Entity(tableName = "entries")
public class Entry {
  @PrimaryKey
  @NonNull
  private int id;

  @ColumnInfo(name = "primary_kanji")
  private String primaryKanji;

  @NonNull
  @ColumnInfo(name = "primary_reading")
  private String primaryReading;

  @ColumnInfo(name = "other_kanji")
  private String otherKanji;

  @ColumnInfo(name = "other_readings")
  private String otherReadings;

  @ColumnInfo(name = "jlpt_level")
  private Integer jlptLevel;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getPrimaryKanji() {
    return primaryKanji;
  }

  public void setPrimaryKanji(String primaryKanji) {
    this.primaryKanji = primaryKanji;
  }

  public String getPrimaryReading() {
    return primaryReading;
  }

  public void setPrimaryReading(String primaryReading) {
    this.primaryReading = primaryReading;
  }

  public String getOtherKanji() {
    return otherKanji;
  }

  public void setOtherKanji(String otherKanji) {
    this.otherKanji = otherKanji;
  }

  public String getOtherReadings() {
    return otherReadings;
  }

  public void setOtherReadings(String otherReadings) {
    this.otherReadings = otherReadings;
  }

  public Integer getJlptLevel() {
    return jlptLevel;
  }

  public void setJlptLevel(Integer jlptLevel) {
    this.jlptLevel = jlptLevel;
  }

  public List<String> otherReadingsList() {
    return SemicolonSplit.split(otherReadings);
  }

  public List<String> otherKanjiList() {
    return SemicolonSplit.split(otherKanji);
  }
}
