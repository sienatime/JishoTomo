package net.emojiparty.android.jishotomo.ui.viewmodels;

import android.support.annotation.Nullable;

public class PagedEntriesControl {
  public String searchType;
  @Nullable public String searchTerm;
  public final static String SEARCH = "SEARCH";
  public final static String BROWSE = "BROWSE";
  public final static String FAVORITES = "FAVORITES";

  public PagedEntriesControl() {
  }

  public PagedEntriesControl(String searchType, @Nullable String searchTerm) {
    this.searchTerm = searchTerm;
    this.searchType = searchType;
  }

  public PagedEntriesControl(String searchType) {
    this.searchType = searchType;
  }
}
