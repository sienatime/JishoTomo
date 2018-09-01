package net.emojiparty.android.jishotomo.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import com.huma.room_for_asset.RoomAsset;
import net.emojiparty.android.jishotomo.data.AppDatabase;
import net.emojiparty.android.jishotomo.data.EntryWithAllSenses;

public class PagedEntryViewModel extends AndroidViewModel {
  public LiveData<PagedList<EntryWithAllSenses>> entries;

  public PagedEntryViewModel(@NonNull Application app) {
    super(app);
    AppDatabase db =
        RoomAsset.databaseBuilder(app.getApplicationContext(), AppDatabase.class, "jishotomo.db")
            .build();
    this.entries = new LivePagedListBuilder<>(
        db.entryDao().getAll(), 20).build();
  }
}
