package net.emojiparty.android.jishotomo.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import net.emojiparty.android.jishotomo.data.EntryDao;
import net.emojiparty.android.jishotomo.data.EntryWithAllSenses;

public class PagedEntryViewModel extends AndroidViewModel {
  public LiveData<PagedList<EntryWithAllSenses>> entries;

  public PagedEntryViewModel(Application application, EntryDao entryDao) {
    super(application);
    this.entries = new LivePagedListBuilder<>(
        entryDao.getAll(), 20).build();
  }
}
