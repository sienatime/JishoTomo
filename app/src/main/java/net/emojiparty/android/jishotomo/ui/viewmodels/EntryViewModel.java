package net.emojiparty.android.jishotomo.ui.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;
import net.emojiparty.android.jishotomo.data.AppRepository;
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses;

public class EntryViewModel extends AndroidViewModel {
  public LiveData<EntryWithAllSenses> entry;

  public EntryViewModel(@NonNull Application application, LifecycleOwner lifecycleOwner, int entryId) {
    super(application);
    entry = new AppRepository(lifecycleOwner).getEntryWithAllSenses(entryId);
  }
}
