package net.emojiparty.android.jishotomo.ui.activities;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import net.emojiparty.android.jishotomo.JishoTomoApp;
import net.emojiparty.android.jishotomo.R;
import net.emojiparty.android.jishotomo.analytics.AnalyticsLogger;
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry;
import net.emojiparty.android.jishotomo.ui.adapters.PagedEntriesAdapter;
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesControl;
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesViewModel;

public class DrawerActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  private PagedEntriesViewModel viewModel;
  private ProgressBar loadingIndicator;
  private MenuItem searchViewMenuItem;
  private RecyclerView searchResults;
  private TextView toolbarTitle;
  private PagedEntriesAdapter adapter;
  private AnalyticsLogger analyticsLogger;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_drawer);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    analyticsLogger = ((JishoTomoApp) getApplication()).getAnalyticsLogger();
    viewModel = ViewModelProviders.of(this).get(PagedEntriesViewModel.class);
    searchResults = findViewById(R.id.search_results_rv);

    setRecyclerViewWithNewAdapter();
    loadingIndicator = findViewById(R.id.loading);
    toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
    searchIntent(getIntent());
    setupDrawer(toolbar);
    setupNavigationView();
    setupRecyclerView();

    toolbarTitle.setOnClickListener((View view) -> {
      searchResults.scrollToPosition(0);
    });
  }

  private void setRecyclerViewWithNewAdapter() {
    adapter = new PagedEntriesAdapter(R.layout.list_item_entry);
    searchResults.setAdapter(adapter);
  }
  // https://developer.android.com/training/search/setup
  // https://developer.android.com/guide/topics/search/search-dialog
  private void searchIntent(Intent intent) {
    String query = null;
    String searchType;
    int titleId = 0;
    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
      query = intent.getStringExtra(SearchManager.QUERY);
      searchType = PagedEntriesControl.SEARCH;
      titleId = R.string.search_results;
    } else {
      searchType = PagedEntriesControl.BROWSE;
      titleId = R.string.app_name;
    }
    PagedEntriesControl pagedEntriesControl = new PagedEntriesControl(searchType, query);
    setPagedEntriesControl(pagedEntriesControl, titleId);
  }

  @Override protected void onNewIntent(Intent intent) {
    searchIntent(intent);
  }

  // Paging library reference https://developer.android.com/topic/libraries/architecture/paging
  private void setupRecyclerView() {
    viewModel.entries.observe(this, (PagedList<SearchResultEntry> entries) -> {
      loadingIndicator.setVisibility(View.INVISIBLE);
      adapter.submitList(entries);
    });
  }

  private void setupDrawer(Toolbar toolbar) {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();
  }

  private void setupNavigationView() {
    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
  }

  @Override public void onBackPressed() {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.drawer, menu);

    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    searchViewMenuItem = menu.findItem(R.id.menu_search);
    SearchView searchView = (SearchView) searchViewMenuItem.getActionView();
    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    searchView.setIconifiedByDefault(false);

    return true;
  }

  private void setPagedEntriesControl(PagedEntriesControl pagedEntriesControl, int titleId) {
    if (titleId > 0) {
      toolbarTitle.setText(getResources().getString(titleId));
    }
    // this is so that the PagedListAdapter does not try to perform a diff
    // against the two lists when changing search types. the app was really laggy
    // when changing lists without re-instantiating the adapter.
    setRecyclerViewWithNewAdapter();

    loadingIndicator.setVisibility(View.VISIBLE);
    viewModel.pagedEntriesControlLiveData.setValue(pagedEntriesControl);
    analyticsLogger.logSearchResultsOrViewItemList(pagedEntriesControl);
  }

  private ArrayList<Integer> jlptMenuIds() {
    ArrayList<Integer> ids = new ArrayList<>();
    ids.add(R.id.nav_jlptn1);
    ids.add(R.id.nav_jlptn2);
    ids.add(R.id.nav_jlptn3);
    ids.add(R.id.nav_jlptn4);
    ids.add(R.id.nav_jlptn5);
    return ids;
  }

  @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    int id = item.getItemId();

    PagedEntriesControl pagedEntriesControl = new PagedEntriesControl();
    ArrayList<Integer> jlptIds = jlptMenuIds();
    int titleId = 0;

    if (id == R.id.nav_search) {
      searchViewMenuItem.expandActionView();
    } else if (id == R.id.nav_browse) {
      pagedEntriesControl.searchType = PagedEntriesControl.BROWSE;
      titleId = R.string.app_name;
    } else if (id == R.id.nav_favorites) {
      pagedEntriesControl.searchType = PagedEntriesControl.FAVORITES;
      titleId = R.string.favorites;
    } else if (jlptIds.indexOf(id) > -1) {
      pagedEntriesControl.searchType = PagedEntriesControl.JLPT;
      int jlptLevel = jlptIds.indexOf(id) + 1;
      pagedEntriesControl.jlptLevel = jlptLevel;
      titleId = getResources().getIdentifier("jlpt_n" + String.valueOf(jlptLevel), "string", getPackageName());
    }

    if (pagedEntriesControl.searchType != null) {
      setPagedEntriesControl(pagedEntriesControl, titleId);
    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }
}
