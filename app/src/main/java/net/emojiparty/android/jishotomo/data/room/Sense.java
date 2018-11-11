package net.emojiparty.android.jishotomo.data.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "senses", indices = {@Index("entry_id")})
public class Sense {
  @PrimaryKey
  @NonNull
  private int id;

  // ints are non-null by default
  @NonNull
  @ColumnInfo(name = "entry_id")
  private int entryId;

  @ColumnInfo(name = "parts_of_speech")
  private String partsOfSpeech;

  @NonNull
  private String glosses;

  @ColumnInfo(name = "applies_to")
  private String appliesTo;

  @ColumnInfo(name = "cross_references")
  private String crossReferences;

  @NonNull public int getId() {
    return id;
  }

  public void setId(@NonNull int id) {
    this.id = id;
  }

  public int getEntryId() {
    return entryId;
  }

  public void setEntryId(int entryId) {
    this.entryId = entryId;
  }

  public String getPartsOfSpeech() {
    return partsOfSpeech;
  }

  public void setPartsOfSpeech(String partsOfSpeech) {
    this.partsOfSpeech = partsOfSpeech;
  }

  @NonNull public String getGlosses() {
    return glosses;
  }

  public void setGlosses(@NonNull String glosses) {
    this.glosses = glosses;
  }

  public String getAppliesTo() {
    return appliesTo;
  }

  public void setAppliesTo(String appliesTo) {
    this.appliesTo = appliesTo;
  }

  public String getCrossReferences() {
    return crossReferences;
  }

  public void setCrossReferences(String crossReferences) {
    this.crossReferences = crossReferences;
  }
}
