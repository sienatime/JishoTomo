package net.emojiparty.android.jishotomo.data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;
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
}