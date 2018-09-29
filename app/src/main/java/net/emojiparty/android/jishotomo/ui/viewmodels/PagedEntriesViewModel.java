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
  public final LiveData<PagedList<SearchResultEntry>> entries;
  public MutableLiveData<PagedEntriesControl> pagedEntriesControlLiveData = new MutableLiveData<>();
  public PagedEntriesControl pagedEntriesControl = new PagedEntriesControl();

  public PagedEntriesViewModel(Application application) {
    super(application);
    AppRepository appRepo = new AppRepository();

    this.entries = Transformations.switchMap(pagedEntriesControlLiveData, pagedEntriesControl -> {
      this.pagedEntriesControl = pagedEntriesControl;
      String searchType = pagedEntriesControl.searchType;
      String searchTerm = pagedEntriesControl.searchTerm;

      switch (searchType) {
        case PagedEntriesControl.SEARCH:
          return appRepo.search(searchTerm);
        case PagedEntriesControl.FAVORITES:
          return appRepo.getFavorites();
        case PagedEntriesControl.JLPT:
          return appRepo.getByJlptLevel(pagedEntriesControl.jlptLevel);
        default:
          return appRepo.browse();
      }
    });
  }
}
