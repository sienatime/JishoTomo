package net.emojiparty.android.jishotomo.data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;
import java.util.List;

public class EntryWithAllSenses {
  @Embedded Entry entry;
  @Relation(parentColumn = "id", entityColumn = "entry_id", entity = Sense.class)
  public List<SenseWithCrossReferences> senses;

  public Entry getEntry() {
    return entry;
  }

  public List<SenseWithCrossReferences> getSenses() {
    return senses;
  }

  public String getKanjiOrReading() {
    return hasKanji() ? getEntry().getPrimaryKanji() : getEntry().getPrimaryReading();
  }

  public String getReading() {
    return hasKanji() ? getEntry().getPrimaryReading() : null;
  }

  public boolean hasKanji() {
    return getEntry().getPrimaryKanji() != null;
  }

  public String getAlternateKanji() {
    return SemicolonSplit.splitAndJoin(getEntry().getOtherKanji());
  }

  public String getAlternateReadings() {
    return SemicolonSplit.splitAndJoin(getEntry().getOtherReadings());
  }

  public boolean hasAlternateKanji() {
    return getEntry().getOtherKanji() != null;
  }

  public boolean hasAlternateReadings() {
    return getEntry().getOtherReadings() != null;
  }
}
