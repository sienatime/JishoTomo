package net.emojiparty.android.jishotomo.data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;
import java.util.List;

public class SenseWithCrossReferences {
  @Embedded Sense sense;
  @Relation(parentColumn = "id", entityColumn = "sense_id")
  public List<CrossReference> crossReferences;

  public Sense getSense() {
    return sense;
  }

  public void setSense(Sense sense) {
    this.sense = sense;
  }

  public List<CrossReference> getCrossReferences() {
    return crossReferences;
  }

  public void setCrossReferences(List<CrossReference> crossReferences) {
    this.crossReferences = crossReferences;
  }
}
