package net.emojiparty.android.jishotomo.data.models;

import androidx.room.Relation;
import java.util.List;
import net.emojiparty.android.jishotomo.data.SemicolonSplit;
import net.emojiparty.android.jishotomo.data.room.Sense;

// this should have only the things needed to display the search result:
//   the Entry's primary_kanji, primary_reading
//   the first Sense
//     that Sense's glosses

public class SearchResultEntry extends PrimaryOnlyEntry {
  @Relation(parentColumn = "id", entityColumn = "entry_id", entity = Sense.class, projection = {"glosses"})
  public List<String> glosses;

  public String getPrimaryGloss() {
    return SemicolonSplit.splitAndJoin(glosses.get(0));
  }
}
