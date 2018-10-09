package net.emojiparty.android.jishotomo.ui.activities;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
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
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import net.emojiparty.android.jishotomo.JishoTomoApp;
import net.emojiparty.android.jishotomo.R;
import net.emojiparty.android.jishotomo.analytics.AnalyticsLogger;
import net.emojiparty.android.jishotomo.data.csv.CsvExporter;
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry;
import net.emojiparty.android.jishotomo.ui.adapters.PagedEntriesAdapter;
import net.emojiparty.android.jishotomo.ui.dialogs.ExplainExportDialog;
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesControl;
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesViewModel;

import static net.emojiparty.android.jishotomo.ui.activities.DefinitionFragment.ENTRY_EMPTY;

public class DrawerActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  private PagedEntriesViewModel viewModel;
  private ProgressBar loadingIndicator;
  private ProgressBar exportIndicator;
  private MenuItem searchViewMenuItem;
  private RecyclerView searchResults;
  private TextView toolbarTitle;
  private PagedEntriesAdapter adapter;
  private AnalyticsLogger analyticsLogger;
  public FrameLayout fragmentContainer;
  private boolean showExportButton = false;
  private int lastEntryViewed = ENTRY_EMPTY;

  private String STATE_SEARCH_TYPE = "state_search_type";
  private String STATE_SEARCH_TERM = "state_search_term";
  private String STATE_JLPT_LEVEL = "state_jlpt_level";
  private String STATE_SHOW_EXPORT_BUTTON = "state_show_export_button";
  private String STATE_LAST_ENTRY_VIEWED = "state_last_entry_viewed";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_drawer);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    analyticsLogger = ((JishoTomoApp) getApplication()).getAnalyticsLogger();
    viewModel = ViewModelProviders.of(this).get(PagedEntriesViewModel.class);
    searchResults = findViewById(R.id.search_results_rv);
    fragmentContainer = findViewById(R.id.definition_fragment_container); // non-null on tablet

    setRecyclerViewWithNewAdapter();
    loadingIndicator = findViewById(R.id.loading);
    exportIndicator = findViewById(R.id.exporting);
    toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
    if (savedInstanceState == null) {
      searchIntent(getIntent());
    } else {
      restoreFromBundle(savedInstanceState);
    }
    setupDrawer(toolbar);
    setupNavigationView();
    setupRecyclerView();

    toolbarTitle.setOnClickListener((View view) -> {
      searchResults.scrollToPosition(0);
    });
  }

  private void restoreFromBundle(Bundle bundle) {
    String searchType = bundle.getString(STATE_SEARCH_TYPE);
    String searchTerm = bundle.getString(STATE_SEARCH_TERM);
    PagedEntriesControl pagedEntriesControl = new PagedEntriesControl(searchType, searchTerm);

    if (bundle.containsKey(STATE_JLPT_LEVEL)) {
      pagedEntriesControl.jlptLevel = bundle.getInt(STATE_JLPT_LEVEL);
    }
    setPagedEntriesControl(pagedEntriesControl);
    setShowExportButton(bundle.getBoolean(STATE_SHOW_EXPORT_BUTTON));
    int lastEntryViewedFromBundle = bundle.getInt(STATE_LAST_ENTRY_VIEWED);
    if (lastEntryViewedFromBundle != ENTRY_EMPTY) {
      addDefinitionFragment(lastEntryViewedFromBundle);
    }
  }

  private void setRecyclerViewWithNewAdapter() {
    adapter = new PagedEntriesAdapter(R.layout.list_item_entry);
    searchResults.setAdapter(adapter);
  }

  private void clearDefinitionBackstack() {
    if (fragmentContainer != null) {
      FragmentManager fragmentManager = getSupportFragmentManager();
      fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
      if (fragmentManager.getBackStackEntryCount() == 0) {
        DefinitionFragment fragment = DefinitionFragment.instance(ENTRY_EMPTY);
        fragmentManager.beginTransaction()
            .replace(R.id.definition_fragment_container, fragment)
            .commit();
      }
    }
  }

  public void addDefinitionFragment(int entryId) {
    lastEntryViewed = entryId;
    DefinitionFragment fragment = DefinitionFragment.instance(entryId);
    getSupportFragmentManager().beginTransaction()
        .add(R.id.definition_fragment_container, fragment)
        .addToBackStack(null)
        .commit();
  }

  // https://developer.android.com/training/search/setup
  // https://developer.android.com/guide/topics/search/search-dialog
  private void searchIntent(Intent intent) {
    String query = null;
    String searchType;
    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
      query = intent.getStringExtra(SearchManager.QUERY);
      searchType = PagedEntriesControl.SEARCH;
    } else {
      searchType = PagedEntriesControl.BROWSE;
    }
    setPagedEntriesControl(new PagedEntriesControl(searchType, query));
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

  @Override protected void onSaveInstanceState(Bundle outState) {
    if (viewModel.pagedEntriesControl.jlptLevel != null) {
      outState.putInt(STATE_JLPT_LEVEL, viewModel.pagedEntriesControl.jlptLevel);
    }
    outState.putString(STATE_SEARCH_TYPE, viewModel.pagedEntriesControl.searchType);
    outState.putString(STATE_SEARCH_TERM, viewModel.pagedEntriesControl.searchTerm);
    outState.putBoolean(STATE_SHOW_EXPORT_BUTTON, showExportButton);
    outState.putInt(STATE_LAST_ENTRY_VIEWED, lastEntryViewed);
    super.onSaveInstanceState(outState);
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
    MenuItem exportIcon = menu.getItem(1);
    searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
      @Override public void onViewAttachedToWindow(View view) {
        // hide export button while search input is open
        exportIcon.setVisible(false);
      }

      @Override public void onViewDetachedFromWindow(View view) {
        if (viewModel.pagedEntriesControl.searchType.equals(PagedEntriesControl.SEARCH)) {
          // we performed a search, so hide the button
          // (have to do this here because calling invalidateOptionsMenu while search input is open
          // makes the search input close)
          setShowExportButton(false);
        } else {
          // refresh export visibility based on current state, since
          // we hid the button when we opened the search input
          invalidateOptionsMenu();
        }
      }
    });
    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    searchView.setIconifiedByDefault(false);
    exportIcon.setVisible(showExportButton);

    return true;
  }

  // https://developer.android.com/training/data-storage/files#InternalVsExternalStorage
  private boolean isExternalStorageWritable() {
    String state = Environment.getExternalStorageState();
    return Environment.MEDIA_MOUNTED.equals(state);
  }

  private void checkForPermissionThenExport() {
    if (!isExternalStorageWritable()) {
      Toast.makeText(this, R.string.no_external_storage, Toast.LENGTH_LONG).show();
      return;
    }

    exportCsv();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.menu_export) {
      explainCsvExport();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void explainCsvExport() {
    ExplainExportDialog dialog = new ExplainExportDialog();
    dialog.setCallback(this::checkForPermissionThenExport);
    dialog.show(getSupportFragmentManager(), "export_explain");
  }

  private void exportCsv() {
    CsvExportAsyncTask.CsvExportUiCallbacks uiCallbacks =
        new CsvExportAsyncTask.CsvExportUiCallbacks() {
          @Override public void onProgressUpdate(Integer progress) {
            exportIndicator.setProgress(progress);
          }

          @Override public void onPreExecute() {
            exportIndicator.setVisibility(View.VISIBLE);
          }

          @Override public void onPostExecute() {
            exportIndicator.setVisibility(View.GONE);
            File csv = new File(CsvExporter.fileLocation(DrawerActivity.this));
            Uri csvUri =
                FileProvider.getUriForFile(DrawerActivity.this, "net.emojiparty.fileprovider", csv);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, csvUri);
            shareIntent.setType("text/csv");
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_csv)));
            analyticsLogger.logCsvSuccess();
          }

          @Override public void onCanceled() {
            exportIndicator.setVisibility(View.GONE);
            Toast.makeText(DrawerActivity.this, R.string.csv_failed, Toast.LENGTH_SHORT).show();
            analyticsLogger.logCsvSuccess();
          }
        };

    new CsvExportAsyncTask(uiCallbacks, viewModel.pagedEntriesControl).execute(DrawerActivity.this);
  }

  private void setPagedEntriesControl(PagedEntriesControl pagedEntriesControl) {
    toolbarTitle.setText(getResources().getString(titleIdForSearchType(pagedEntriesControl)));
    // this is so that the PagedListAdapter does not try to perform a diff
    // against the two lists when changing search types. the app was really laggy
    // when changing lists without re-instantiating the adapter.
    setRecyclerViewWithNewAdapter();
    clearDefinitionBackstack();
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

  private void setShowExportButton(boolean show) {
    showExportButton = show;
    invalidateOptionsMenu();
  }

  @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    int id = item.getItemId();

    PagedEntriesControl pagedEntriesControl = new PagedEntriesControl();
    ArrayList<Integer> jlptIds = jlptMenuIds();

    if (id == R.id.nav_search) {
      searchViewMenuItem.expandActionView();
      setShowExportButton(false);
    } else if (id == R.id.nav_browse) {
      pagedEntriesControl.searchType = PagedEntriesControl.BROWSE;
      setShowExportButton(false);
    } else if (id == R.id.nav_favorites) {
      pagedEntriesControl.searchType = PagedEntriesControl.FAVORITES;
      setShowExportButton(true);
    } else if (jlptIds.indexOf(id) > -1) {
      pagedEntriesControl.searchType = PagedEntriesControl.JLPT;
      pagedEntriesControl.jlptLevel = jlptIds.indexOf(id) + 1;
      setShowExportButton(true);
    }

    if (pagedEntriesControl.searchType != null) {
      setPagedEntriesControl(pagedEntriesControl);
    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  private int titleIdForSearchType(PagedEntriesControl pagedEntriesControl) {
    switch (pagedEntriesControl.searchType) {
      case PagedEntriesControl.BROWSE:
        return R.string.app_name;
      case PagedEntriesControl.FAVORITES:
        return R.string.favorites;
      case PagedEntriesControl.JLPT:
        return getResources().getIdentifier(
            "jlpt_n" + String.valueOf(pagedEntriesControl.jlptLevel), "string", getPackageName());
      case PagedEntriesControl.SEARCH:
        return R.string.search_results;
      default:
        return R.string.app_name;
    }
  }
}
