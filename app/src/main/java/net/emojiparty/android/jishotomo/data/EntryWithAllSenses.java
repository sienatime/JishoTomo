package net.emojiparty.android.jishotomo.data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;
import android.content.Context;
import android.content.Intent;
import java.util.List;
import net.emojiparty.android.jishotomo.ui.DefinitionActivity;

import static net.emojiparty.android.jishotomo.ui.DefinitionActivity.ENTRY_ID_EXTRA;

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
    return SemicolonSplit.splitAndJoin(primarySense.getGlosses());
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

  public void openDefinitionActivity(Context context) {
    Intent intent = new Intent(context, DefinitionActivity.class);
    intent.putExtra(ENTRY_ID_EXTRA, entry.getId());
    context.startActivity(intent);
  }


}
