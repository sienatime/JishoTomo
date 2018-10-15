package net.emojiparty.android.jishotomo.ui.viewmodels;

import android.app.Application;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class EntryViewModelFactory extends ViewModelProvider.NewInstanceFactory {
  private Application application;
  private LifecycleOwner lifecycleOwner;
  private int entryId;

  public EntryViewModelFactory(Application application, LifecycleOwner lifecycleOwner, int entryId) {
    this.application = application;
    this.lifecycleOwner = lifecycleOwner;
    this.entryId = entryId;
  }

  @SuppressWarnings("unchecked")
  @NonNull @Override public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    return (T) new EntryViewModel(application, lifecycleOwner, entryId);
  }
}
