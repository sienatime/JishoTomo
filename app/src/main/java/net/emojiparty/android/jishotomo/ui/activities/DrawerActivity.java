package net.emojiparty.android.jishotomo.ui.activities;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import javax.inject.Inject;
import net.emojiparty.android.jishotomo.R;
import net.emojiparty.android.jishotomo.data.di.AppModule;
import net.emojiparty.android.jishotomo.data.di.DaggerAppComponent;
import net.emojiparty.android.jishotomo.data.di.RoomModule;
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry;
import net.emojiparty.android.jishotomo.data.room.EntryDao;
import net.emojiparty.android.jishotomo.ui.adapters.PagedEntriesAdapter;
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesViewModel;
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesViewModelFactory;

public class DrawerActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  private PagedEntriesViewModel viewModel;
  private ProgressBar loadingIndicator;
  @Inject public EntryDao entryDao;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_drawer);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    setupDagger();
    viewModel =
        ViewModelProviders.of(this, new PagedEntriesViewModelFactory(getApplication(), entryDao))
            .get(PagedEntriesViewModel.class);
    loadingIndicator = findViewById(R.id.loading);
    searchIntent(getIntent());
    setupDrawer(toolbar);
    setupNavigationView();
    setupRecyclerView();
  }

  // https://developer.android.com/training/search/setup
  // https://developer.android.com/guide/topics/search/search-dialog
  private void searchIntent(Intent intent) {
    loadingIndicator.setVisibility(View.VISIBLE);
    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
      String query = intent.getStringExtra(SearchManager.QUERY);
      viewModel.searchTermLiveData.setValue(query);
    } else {
      viewModel.searchTermLiveData.setValue(null);
    }
  }

  private void setupDagger() {
    DaggerAppComponent.builder()
        .appModule(new AppModule(getApplication()))
        .roomModule(new RoomModule(getApplication()))
        .build()
        .inject(this);
  }

  @Override protected void onNewIntent(Intent intent) {
    searchIntent(intent);
  }

  // Paging library reference https://developer.android.com/topic/libraries/architecture/paging
  private void setupRecyclerView() {
    RecyclerView searchResults = findViewById(R.id.search_results_rv);
    searchResults.setLayoutManager(new LinearLayoutManager(DrawerActivity.this));
    PagedEntriesAdapter adapter = new PagedEntriesAdapter(R.layout.list_item_entry);
    viewModel.entries.observe(this, (PagedList<SearchResultEntry> entries) -> {
      loadingIndicator.setVisibility(View.INVISIBLE);
      adapter.submitList(entries);
    });
    searchResults.setAdapter(adapter);
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

    // Get the SearchView and set the searchable configuration
    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
    // Assumes current activity is the searchable activity
    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

    return true;
  }

  // this is for the drawer, maybe move to another class
  @SuppressWarnings("StatementWithEmptyBody") @Override public boolean onNavigationItemSelected(
      MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.nav_camera) {
      // Handle the camera action
    } else if (id == R.id.nav_gallery) {

    } else if (id == R.id.nav_slideshow) {

    } else if (id == R.id.nav_manage) {

    } else if (id == R.id.nav_share) {

    } else if (id == R.id.nav_send) {

    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }
}
