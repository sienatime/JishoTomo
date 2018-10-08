package net.emojiparty.android.jishotomo.data.models;

import android.arch.persistence.room.Relation;
import android.content.Context;
import android.content.Intent;
import java.util.List;
import net.emojiparty.android.jishotomo.data.SemicolonSplit;
import net.emojiparty.android.jishotomo.data.room.Sense;
import net.emojiparty.android.jishotomo.ui.activities.DefinitionActivity;

import static net.emojiparty.android.jishotomo.ui.activities.DefinitionFragment.ENTRY_ID_EXTRA;

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

  public void openDefinitionActivity(Context context) {
    Intent intent = new Intent(context, DefinitionActivity.class);
    intent.putExtra(ENTRY_ID_EXTRA, id);
    context.startActivity(intent);
  }
}
