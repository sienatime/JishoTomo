package net.emojiparty.android.jishotomo.data.models;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Relation;
import android.content.Context;
import java.util.List;
import net.emojiparty.android.jishotomo.analytics.AnalyticsLogger;
import net.emojiparty.android.jishotomo.data.AppRepository;
import net.emojiparty.android.jishotomo.data.SemicolonSplit;
import net.emojiparty.android.jishotomo.data.room.Entry;
import net.emojiparty.android.jishotomo.data.room.Sense;

public class EntryWithAllSenses {
  @Embedded public Entry entry;
  @Relation(parentColumn = "id", entityColumn = "entry_id", entity = Sense.class)
  public List<SenseWithCrossReferences> senses;
  @Ignore private AnalyticsLogger analyticsLogger;
  @Ignore private AppRepository appRepository;

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

  // lazily instantiate AppRepository and AnalyticsLogger so that we don't have to
  // always instantiate unless Favorite button is clicked,
  // and if it is clicked again, we already have the references.
  public void toggleFavorite(Context context) {
    getAppRepository().toggleFavorite(getEntry());
    getAnalyticsLogger(context).logToggleFavoriteEvent(entry.getId(), getKanjiOrReading(),
        !entry.getFavorited());
  }

  private AppRepository getAppRepository() {
    if (appRepository == null) {
      appRepository = new AppRepository();
    } return appRepository;
  }

  private AnalyticsLogger getAnalyticsLogger(Context context) {
    if (analyticsLogger == null) {
      analyticsLogger = new AnalyticsLogger(context);
    }
    return analyticsLogger;
  }
}
