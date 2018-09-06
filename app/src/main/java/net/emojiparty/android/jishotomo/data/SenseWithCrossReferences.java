package net.emojiparty.android.jishotomo.data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import java.util.List;

public class SenseWithCrossReferences {
  @Embedded Sense sense;

  @Ignore
  public String xRefString;
  @Ignore
  public List<CrossReferencedEntry> crossReferences;

  public Sense getSense() {
    return sense;
  }

  public List<CrossReferencedEntry> getCrossReferences() {
    return crossReferences;
  }

  public void setCrossReferences(List<CrossReferencedEntry> crossReferences) {
    this.crossReferences = crossReferences;
  }

  public void setxRefString() {
    if (crossReferences == null) {
      return;
    }
    String text = "See also: ";
    for (CrossReferencedEntry xrefJoin : crossReferences) {
      text += xrefJoin.getKanjiOrReading() + ", ";
    }
    this.xRefString = text;
  }
}
