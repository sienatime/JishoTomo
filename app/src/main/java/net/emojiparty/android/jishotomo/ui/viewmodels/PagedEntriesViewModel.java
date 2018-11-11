package net.emojiparty.android.jishotomo.ui.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.PagedList;
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
