package net.emojiparty.android.jishotomo.data;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import net.emojiparty.android.jishotomo.JishoTomoApp;
import net.emojiparty.android.jishotomo.data.models.CrossReferencedEntry;
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses;
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry;
import net.emojiparty.android.jishotomo.data.models.SenseWithCrossReferences;
import net.emojiparty.android.jishotomo.data.room.Entry;
import net.emojiparty.android.jishotomo.data.room.EntryDao;
import net.emojiparty.android.jishotomo.data.room.SenseDao;

public class AppRepository {
  private LifecycleOwner lifecycleOwner;
  @Inject public EntryDao entryDao;
  @Inject public SenseDao senseDao;

  public AppRepository() {
    JishoTomoApp.getAppComponent().inject(this);
  }

  public AppRepository(LifecycleOwner lifecycleOwner) {
    this.lifecycleOwner = lifecycleOwner;
    JishoTomoApp.getAppComponent().inject(this);
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

  public LiveData<PagedList<SearchResultEntry>> search(String term) {
    String formattedQuery = String.format("%%%s%%", term);
    return new LivePagedListBuilder<>(entryDao.search(formattedQuery), 20).build();
  }

  public LiveData<PagedList<SearchResultEntry>> browse() {
    return new LivePagedListBuilder<>(entryDao.getAll(), 20).build();
  }

  public void toggleFavorite(Entry entry) {
    AsyncTask.execute(() -> {
      entry.setFavorited(!entry.getFavorited());
      entryDao.updateEntry(entry);
    });
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
