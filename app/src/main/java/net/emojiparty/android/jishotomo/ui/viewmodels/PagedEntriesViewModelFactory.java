package net.emojiparty.android.jishotomo.ui.viewmodels;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import net.emojiparty.android.jishotomo.data.room.EntryDao;

public class PagedEntriesViewModelFactory extends ViewModelProvider.NewInstanceFactory {
  private Application application;
  @NonNull private EntryDao entryDao;

  public PagedEntriesViewModelFactory(Application application, EntryDao entryDao) {
    this.application = application;
    this.entryDao = entryDao;
  }

  @NonNull @Override public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    return (T) new PagedEntriesViewModel(application, entryDao);
  }
}
