package net.emojiparty.android.jishotomo.ui.viewmodels;

import android.app.Application;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

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
