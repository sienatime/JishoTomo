package net.emojiparty.android.jishotomo.data;

import android.app.Application;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import net.emojiparty.android.jishotomo.data.di.AppModule;
import net.emojiparty.android.jishotomo.data.di.DaggerAppComponent;
import net.emojiparty.android.jishotomo.data.di.RoomModule;
import net.emojiparty.android.jishotomo.data.models.CrossReferencedEntry;
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses;
import net.emojiparty.android.jishotomo.data.models.SenseWithCrossReferences;
import net.emojiparty.android.jishotomo.data.room.EntryDao;
import net.emojiparty.android.jishotomo.data.room.SenseDao;

public class AppRepository {
  private LifecycleOwner lifecycleOwner;
  @Inject public EntryDao entryDao;
  @Inject public SenseDao senseDao;

  public AppRepository(Application application, LifecycleOwner lifecycleOwner) {
    this.lifecycleOwner = lifecycleOwner;
    DaggerAppComponent.builder()
        .appModule(new AppModule(application))
        .roomModule(new RoomModule(application))
        .build()
        .inject(AppRepository.this);
  }

  public MutableLiveData<EntryWithAllSenses> getEntryWithAllSenses(int entryId) {
    MutableLiveData<EntryWithAllSenses> liveData = new MutableLiveData<>();

    entryDao.getEntryById(entryId).observe(lifecycleOwner, (@Nullable EntryWithAllSenses entry) -> {
      if (entry != null) {
        setCrossReferences(entry, liveData);
      }
    });

    return liveData;
  }

  private void setCrossReferences(EntryWithAllSenses entry,
      MutableLiveData<EntryWithAllSenses> liveData) {
    senseDao.getCrossReferencedEntries(entry.getEntry().getId())
        .observe(lifecycleOwner, (@Nullable List<CrossReferencedEntry> crossReferencedEntries) -> {
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
          liveData.setValue(entry);
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
