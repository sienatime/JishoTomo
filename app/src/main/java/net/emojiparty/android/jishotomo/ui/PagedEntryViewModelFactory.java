package net.emojiparty.android.jishotomo.ui;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import net.emojiparty.android.jishotomo.data.EntryDao;

public class PagedEntryViewModelFactory extends ViewModelProvider.NewInstanceFactory {
  private Application application;
  @Nullable private EntryDao entryDao;

  public PagedEntryViewModelFactory(Application application, EntryDao entryDao) {
    this.application = application;
    this.entryDao = entryDao;
  }

  @NonNull @Override public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    return (T) new PagedEntryViewModel(application, entryDao);
  }
}
