package net.emojiparty.android.jishotomo.ui.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import net.emojiparty.android.jishotomo.data.room.EntryDao;
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry;

public class PagedEntriesViewModel extends AndroidViewModel {
  public LiveData<PagedList<SearchResultEntry>> entries;

  public PagedEntriesViewModel(Application application, EntryDao entryDao) {
    super(application);
    this.entries = new LivePagedListBuilder<>(
        entryDao.getAll(), 20).build();
  }
}
