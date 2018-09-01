package net.emojiparty.android.jishotomo.data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;
import android.text.TextUtils;
import java.util.List;

public class EntryWithAllSenses {
  @Embedded Entry entry;
  @Relation(parentColumn = "id", entityColumn = "entry_id")
  public List<Sense> senses;

  public Entry getEntry() {
    return entry;
  }

  public List<Sense> getSenses() {
    return senses;
  }

  public String getPrimaryGloss() {
    Sense primarySense = getPrimarySense();
    return TextUtils.join(", ", primarySense.glossesList());
  }

  public String getKanjiOrReading() {
    return hasKanji() ? getEntry().getPrimaryKanji() : getEntry().getPrimaryReading();
  }

  public String getReading() {
    return hasKanji() ? getEntry().getPrimaryReading() : null;
  }

  private Sense getPrimarySense() {
    return getSenses().get(0);
  }

  private boolean hasKanji() {
    return getEntry().getPrimaryKanji() != null;
  }
}
