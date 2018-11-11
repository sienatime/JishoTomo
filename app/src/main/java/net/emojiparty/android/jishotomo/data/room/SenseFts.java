package net.emojiparty.android.jishotomo.data.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Fts4;

@Fts4(contentEntity = Sense.class)
@Entity(tableName = "sensesFts")
public class SenseFts {
  @NonNull
  private String glosses;

  public SenseFts(String glosses) {
    this.glosses = glosses;
  }

  public String getGlosses() {
    return glosses;
  }
}
