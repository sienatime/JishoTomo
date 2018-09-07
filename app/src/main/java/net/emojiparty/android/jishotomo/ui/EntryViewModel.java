package net.emojiparty.android.jishotomo.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import net.emojiparty.android.jishotomo.data.room.EntryDao;
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses;

public class EntryViewModel extends AndroidViewModel {
  public LiveData<EntryWithAllSenses> entry;

  public EntryViewModel(@NonNull Application application, EntryDao entryDao, int entryId) {
    super(application);
    entry = entryDao.getEntryById(entryId);
  }
}
