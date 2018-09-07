package net.emojiparty.android.jishotomo.ui.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import net.emojiparty.android.jishotomo.data.AppRepository;
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses;

public class EntryViewModel extends AndroidViewModel {
  public LiveData<EntryWithAllSenses> entry;

  public EntryViewModel(@NonNull Application application, LifecycleOwner lifecycleOwner, int entryId) {
    super(application);
    entry = new AppRepository(application, lifecycleOwner).getEntryWithAllSenses(entryId);
  }
}
