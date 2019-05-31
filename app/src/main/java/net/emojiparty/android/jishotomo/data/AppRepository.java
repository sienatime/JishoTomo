package net.emojiparty.android.jishotomo.data;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import android.os.AsyncTask;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Date;
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
  private final int PAGE_SIZE = 20;
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

  private boolean isKana(int codePoint) {
    return codePoint >= 12352 && codePoint <= 12543;
  }

  private boolean isCJK(int codePoint) {
    return codePoint >= 19968;
  }

  public LiveData<PagedList<SearchResultEntry>> search(String term) {
    int unicodeCodePoint = Character.codePointAt(term, 0);
    if (isKana(unicodeCodePoint) || isCJK(unicodeCodePoint)) {
      String wildcardQuery = String.format("*%s*", term);
      return new LivePagedListBuilder<>(entryDao.searchByJapaneseTerm(wildcardQuery), PAGE_SIZE).build();
    } else {
      return new LivePagedListBuilder<>(entryDao.searchByEnglishTerm(term), PAGE_SIZE).build();
    }
  }

  public LiveData<PagedList<SearchResultEntry>> browse() {
    return new LivePagedListBuilder<>(entryDao.browse(), PAGE_SIZE).build();
  }

  public LiveData<PagedList<SearchResultEntry>> getFavorites() {
    return new LivePagedListBuilder<>(entryDao.getFavorites(), PAGE_SIZE).build();
  }

  public LiveData<PagedList<SearchResultEntry>> getByJlptLevel(Integer level) {
    return new LivePagedListBuilder<>(entryDao.findByJlptLevel(level), PAGE_SIZE).build();
  }

  public List<EntryWithAllSenses> getAllFavorites() {
    return entryDao.getAllFavorites();
  }

  public List<EntryWithAllSenses> getAllByJlptLevel(Integer jlptLevel) {
    return entryDao.getAllByJlptLevel(jlptLevel);
  }

  public interface OnDataLoaded {
    void success(SearchResultEntry entry);
  }

  public void getRandomEntryByJlptLevel(Integer level, OnDataLoaded callback) {
    AsyncTask.execute(() -> {
      int jlptCount = entryDao.getJlptLevelCount(level);
      SearchResultEntry entry = entryDao.randomByJlptLevel(level, randomOffset(jlptCount));
      callback.success(entry);
    });
  }

  private int randomOffset(int count) {
    int max = count - 1;
    return (int)(Math.random() * max);
  }

  public void toggleFavorite(Entry entry) {
    AsyncTask.execute(() -> {
      entry.toggleFavorited();
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
