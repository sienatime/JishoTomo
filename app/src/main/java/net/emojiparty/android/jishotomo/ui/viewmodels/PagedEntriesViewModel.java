package net.emojiparty.android.jishotomo.ui.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.PagedList;
import net.emojiparty.android.jishotomo.data.AppRepository;
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry;

public class PagedEntriesViewModel extends AndroidViewModel {
  public MutableLiveData<String> searchTermLiveData = new MutableLiveData<>();
  public final LiveData<PagedList<SearchResultEntry>> entries;

  public PagedEntriesViewModel(Application application) {
    super(application);
    AppRepository appRepo = new AppRepository();
    this.entries = Transformations.switchMap(searchTermLiveData, searchTerm -> {
      if (searchTerm == null) {
        return appRepo.browse();
      } else {
        return appRepo.search(searchTerm);
      }
    });
  }
}
