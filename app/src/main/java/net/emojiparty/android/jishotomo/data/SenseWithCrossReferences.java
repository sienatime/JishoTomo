package net.emojiparty.android.jishotomo.data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;
import java.util.ArrayList;
import java.util.List;

public class SenseWithCrossReferences {
  @Embedded Sense sense;
  @Relation(parentColumn = "id", entityColumn = "sense_id")
  public List<CrossReference> crossReferences;

  public Sense getSense() {
    return sense;
  }

  public List<CrossReference> getCrossReferences() {
    return crossReferences;
  }

  public void setCrossReferences(List<CrossReference> crossReferences) {
    this.crossReferences = crossReferences;
  }

  public List<Integer> getCrossReferenceSenseIds() {
    ArrayList<Integer> ids = new ArrayList<>();
    for (CrossReference cr : crossReferences) {
      ids.add(cr.getCrossReferenceSenseId());
    }

    return ids;
  }

  public String crossReferenceText() {
    //String text = "See also: ";
    //for (LiveData<SenseWithEntry> senseWithEntry : xRefSenses) {
    //  if (senseWithEntry.getValue() != null) {
    //    text += senseWithEntry.getValue().getPrimaryReading();
    //  }
    //}
    return "";
  }
}
