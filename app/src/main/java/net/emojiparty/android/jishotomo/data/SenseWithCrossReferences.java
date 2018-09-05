package net.emojiparty.android.jishotomo.data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Relation;
import java.util.ArrayList;
import java.util.List;

public class SenseWithCrossReferences {
  @Embedded Sense sense;
  @Relation(parentColumn = "id", entityColumn = "sense_id")
  public List<CrossReference> crossReferences;

  @Ignore
  public String xRefString = "";

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

  public String crossReferenceText(List<PrimaryOnlyEntry> entries) {
    if (entries == null) {
      return "nullzo";
    }
    String text = "See also: ";
    for (PrimaryOnlyEntry entry : entries) {
      text += entry.getKanjiOrReading();
    }
    return text;
  }
}
