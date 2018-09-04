package net.emojiparty.android.jishotomo.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "cross_references", indices = {@Index("sense_id")})
public class CrossReference {
  @PrimaryKey @NonNull private int id;

  @NonNull @ColumnInfo(name = "sense_id") private int senseId;

  @NonNull @ColumnInfo(name = "cross_reference_sense_id") private int crossReferenceSenseId;

  @NonNull public int getId() {
    return id;
  }

  public void setId(@NonNull int id) {
    this.id = id;
  }

  @NonNull public int getSenseId() {
    return senseId;
  }

  public void setSenseId(@NonNull int senseId) {
    this.senseId = senseId;
  }

  @NonNull public int getCrossReferenceSenseId() {
    return crossReferenceSenseId;
  }

  public void setCrossReferenceSenseId(@NonNull int crossReferenceSenseId) {
    this.crossReferenceSenseId = crossReferenceSenseId;
  }
}
