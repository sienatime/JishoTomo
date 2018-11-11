package net.emojiparty.android.jishotomo.data.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import net.emojiparty.android.jishotomo.data.models.PrimaryOnlyEntry;

@Entity(tableName = "cross_references", indices = {@Index("sense_id")})
public class CrossReference {
  @PrimaryKey @NonNull private int id;

  @NonNull @ColumnInfo(name = "sense_id") private int senseId;

  @NonNull @ColumnInfo(name = "cross_reference_sense_id") private int crossReferenceSenseId;

  @Ignore
  private PrimaryOnlyEntry crossReferenceEntry;

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

  public PrimaryOnlyEntry getCrossReferenceEntry() {
    return crossReferenceEntry;
  }

  public void setCrossReferenceEntry(PrimaryOnlyEntry crossReferenceEntry) {
    this.crossReferenceEntry = crossReferenceEntry;
  }
}
