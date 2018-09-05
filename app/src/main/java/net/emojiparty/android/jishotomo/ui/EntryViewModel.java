package net.emojiparty.android.jishotomo.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import java.util.List;
import net.emojiparty.android.jishotomo.data.EntryDao;
import net.emojiparty.android.jishotomo.data.EntryWithAllSenses;
import net.emojiparty.android.jishotomo.data.SenseWithEntry;

public class EntryViewModel extends AndroidViewModel {
  public LiveData<EntryWithAllSenses> entry;
  public MutableLiveData<List<SenseWithEntry>> crossReferencedSenses = new MutableLiveData<>();

  public EntryViewModel(@NonNull Application application, EntryDao entryDao, int entryId) {
    super(application);
    entry = entryDao.getEntryById(entryId);
  }
}
