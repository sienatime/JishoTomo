package net.emojiparty.android.jishotomo.data.models;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import java.util.List;
import net.emojiparty.android.jishotomo.data.room.Sense;

public class SenseWithCrossReferences {
  @Embedded public Sense sense;

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

  public String getCrossReferencesDisplay() {
    if (crossReferences == null) {
      return null;
    }
    String text = "See also: ";
    for (CrossReferencedEntry xrefJoin : crossReferences) {
      text += xrefJoin.getKanjiOrReading() + ", ";
    }
    return text;
  }
}
