package net.emojiparty.android.jishotomo.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import net.emojiparty.android.jishotomo.data.CrossReference;
import net.emojiparty.android.jishotomo.data.EntryDao;
import net.emojiparty.android.jishotomo.data.EntryWithAllSenses;
import net.emojiparty.android.jishotomo.data.PrimaryOnlyEntry;
import net.emojiparty.android.jishotomo.data.SenseWithCrossReferences;

public class EntryViewModel extends AndroidViewModel {
  public LiveData<EntryWithAllSenses> entry;

  public EntryViewModel(@NonNull Application application, EntryDao entryDao, int entryId) {
    super(application);
    entry = entryDao.getEntryById(entryId);
    setCrossReferences(entryDao);
  }

  private void setCrossReferences(EntryDao entryDao) {
    if (entry.getValue() != null) {
      for (SenseWithCrossReferences senseWithCr : entry.getValue().getSenses()) {
        for (CrossReference cr : senseWithCr.getCrossReferences()) {
          int senseId = cr.getSenseId();
          PrimaryOnlyEntry entry = entryDao.getEntryBySenseId(senseId);
          cr.setCrossReferenceEntry(entry);
        }
      }
    }
  }
}
