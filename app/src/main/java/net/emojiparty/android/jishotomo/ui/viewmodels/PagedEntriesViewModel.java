package net.emojiparty.android.jishotomo.ui.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry;
import net.emojiparty.android.jishotomo.data.room.EntryDao;

public class PagedEntriesViewModel extends AndroidViewModel {
  public MutableLiveData<String> searchTermLiveData = new MutableLiveData<>();
  public final LiveData<PagedList<SearchResultEntry>> entries;

  public PagedEntriesViewModel(Application application, EntryDao entryDao) {
    super(application);
    this.entries = Transformations.switchMap(searchTermLiveData, searchTerm -> {
      if (searchTerm == null) {
        return new LivePagedListBuilder<>(entryDao.getAll(), 20).build();
      } else {
        String formattedQuery = String.format("%%%s%%", searchTerm);
        return new LivePagedListBuilder<>(entryDao.search(formattedQuery), 20).build();
      }
    });
  }
}
