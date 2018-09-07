package net.emojiparty.android.jishotomo.data;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.emojiparty.android.jishotomo.data.models.CrossReferencedEntry;
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses;
import net.emojiparty.android.jishotomo.data.models.SenseWithCrossReferences;
import net.emojiparty.android.jishotomo.data.room.EntryDao;
import net.emojiparty.android.jishotomo.data.room.SenseDao;

public class AppRepository {

  public MutableLiveData<EntryWithAllSenses> getEntryWithAllSenses(EntryDao entryDao,
      SenseDao senseDao, int entryId) {
    MutableLiveData<EntryWithAllSenses> entryWithAllSensesMutableLiveData = new MutableLiveData<>();

    entryDao.getEntryById(entryId).observeForever((@Nullable EntryWithAllSenses entry) -> {
      if (entry != null) {
        setCrossReferences(entry, senseDao, entryWithAllSensesMutableLiveData);
      }
    });

    return entryWithAllSensesMutableLiveData;
  }

  private void setCrossReferences(EntryWithAllSenses entry, SenseDao senseDao,
      MutableLiveData<EntryWithAllSenses> entryWithAllSensesMutableLiveData) {
    senseDao.getCrossReferencedEntries(entry.getEntry().getId())
        .observeForever((@Nullable List<CrossReferencedEntry> crossReferencedEntries) -> {
          if (crossReferencedEntries != null) {
            HashMap<Integer, List<CrossReferencedEntry>> hashMap =
                crossReferenceHash(crossReferencedEntries);

            for (SenseWithCrossReferences sense : entry.getSenses()) {
              List<CrossReferencedEntry> list = hashMap.get(sense.getSense().getId());
              if (list != null) {
                sense.setCrossReferences(list);
                sense.setxRefString();
              }
            }
          }
          entryWithAllSensesMutableLiveData.setValue(entry);
        });
  }

  private HashMap<Integer, List<CrossReferencedEntry>> crossReferenceHash(
      List<CrossReferencedEntry> senses) {
    HashMap<Integer, List<CrossReferencedEntry>> hashMap = new HashMap<>();
    for (CrossReferencedEntry crossReferencedEntry : senses) {
      Integer senseId = crossReferencedEntry.senseId;
      if (hashMap.get(senseId) == null) {
        ArrayList<CrossReferencedEntry> xrefs = new ArrayList<>();
        xrefs.add(crossReferencedEntry);
        hashMap.put(senseId, xrefs);
      } else {
        hashMap.get(senseId).add(crossReferencedEntry);
      }
    }
    return hashMap;
  }
}
