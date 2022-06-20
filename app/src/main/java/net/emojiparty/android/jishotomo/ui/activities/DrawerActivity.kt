package net.emojiparty.android.jishotomo.ui.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import net.emojiparty.android.jishotomo.JishoTomoApp
import net.emojiparty.android.jishotomo.R
import net.emojiparty.android.jishotomo.analytics.AnalyticsLogger
import net.emojiparty.android.jishotomo.data.csv.CsvExporter
import net.emojiparty.android.jishotomo.databinding.ActivityDrawerBinding
import net.emojiparty.android.jishotomo.ui.csv.CsvExportUiCallbacks
import net.emojiparty.android.jishotomo.ui.dialogs.ExportDialog
import net.emojiparty.android.jishotomo.ui.dialogs.UnfavoriteAllDialog
import net.emojiparty.android.jishotomo.ui.fragments.DefinitionFragment
import net.emojiparty.android.jishotomo.ui.fragments.EntryListFragment
import net.emojiparty.android.jishotomo.ui.presentation.AndroidResourceFetcher
import net.emojiparty.android.jishotomo.ui.presentation.MenuButtons
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesControl
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesViewModel
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesViewModelFactory
import java.io.File

class DrawerActivity : AppCompatActivity(), OnNavigationItemSelectedListener {
  private val viewModel: PagedEntriesViewModel by viewModels {
    PagedEntriesViewModelFactory(AndroidResourceFetcher(resources, packageName))
  }
  private var searchViewMenuItem: MenuItem? = null

  private lateinit var analyticsLogger: AnalyticsLogger
  private var lastEntryViewed = DefinitionFragment.ENTRY_EMPTY
  private var fragmentContainer: FragmentContainerView? = null
  private var isLaunchingWithEntry: Boolean = false

  private lateinit var binding: ActivityDrawerBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityDrawerBinding.inflate(layoutInflater)
    val view = binding.root
    setContentView(view)

    setSupportActionBar(binding.appBarDrawer.drawerToolbar)
    supportActionBar!!.setDisplayShowTitleEnabled(false) // I handle the title separately

    fragmentContainer = binding.appBarDrawer.contentDrawer.drawerContentFragment

    fragmentContainer?.let { container ->
      supportFragmentManager.beginTransaction()
        .add(container.id, EntryListFragment())
        .commit()
    }

    analyticsLogger = (application as JishoTomoApp).analyticsLogger

    if (savedInstanceState == null) {
      searchIntent(intent)
    } else {
      restoreFromBundle(savedInstanceState)
    }
    setupDrawer()
    setupNavigationView()

    val entryId = intent.getIntExtra(DefinitionFragment.ENTRY_ID_EXTRA, DefinitionFragment.ENTRY_EMPTY)
    if (entryId != DefinitionFragment.ENTRY_EMPTY) {
      isLaunchingWithEntry = true
      addDefinitionFragment(entryId)
    }

    viewModel.pagedEntriesControlLiveData.observe(this) {
      pagedEntriesControlObserver(it)
    }

    binding.appBarDrawer.toolbarTitle.setOnClickListener { viewModel.toolbarTapped() }
  }

  override fun onDestroy() {
    searchViewMenuItem = null
    super.onDestroy()
  }

  private fun restoreFromBundle(bundle: Bundle) {
    viewModel.restoreFromBundleValues(
      bundle.getString(STATE_SEARCH_TYPE),
      bundle.getString(STATE_SEARCH_TERM),
      bundle.getInt(STATE_JLPT_LEVEL)
    )

    val lastEntryViewedFromBundle = bundle.getInt(STATE_LAST_ENTRY_VIEWED)
    if (lastEntryViewedFromBundle != DefinitionFragment.ENTRY_EMPTY) {
      addDefinitionFragment(lastEntryViewedFromBundle)
    }
  }

  private fun clearDefinitionBackstack() {
    supportFragmentManager.popBackStack(null, 0)
  }

  private fun addFragmentToBackstack(fragment: Fragment) {
    (fragmentContainer ?: binding.appBarDrawer.contentDrawer.tabletDefinitionFragmentContainer)?.let { container ->
      supportFragmentManager.beginTransaction()
        .add(container.id, fragment)
        .addToBackStack(null)
        .commit()
    }
  }

  private fun isTablet(): Boolean {
    return binding.appBarDrawer.contentDrawer.tabletDefinitionFragmentContainer != null
  }

  fun addDefinitionFragment(entryId: Int) {
    lastEntryViewed = entryId
    val fragment = DefinitionFragment.instance(entryId)
    addFragmentToBackstack(fragment)
    setToolbarTitle(R.string.app_name)
    if (!isTablet()) {
      MenuButtons.hideExtraButtons(binding.appBarDrawer.drawerToolbar.menu)
    }
  }

  private fun searchIntent(intent: Intent) {
    viewModel.setFromSearchIntentAction(
      intent.action,
      intent.getStringExtra(SearchManager.QUERY)
    )
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    searchIntent(intent)
  }

  private fun setupDrawer() {
    val toggle = ActionBarDrawerToggle(
      this,
      binding.drawerLayout,
      binding.appBarDrawer.drawerToolbar,
      R.string.navigation_drawer_open,
      R.string.navigation_drawer_close
    )
    binding.drawerLayout.addDrawerListener(toggle)
    toggle.syncState()
  }

  private fun setupNavigationView() {
    binding.navView.setNavigationItemSelectedListener(this)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    viewModel.getJlptLevel()?.let {
      outState.putInt(STATE_JLPT_LEVEL, it)
    }
    viewModel.getSearchTerm()?.let {
      outState.putString(STATE_SEARCH_TERM, it)
    }
    outState.putString(STATE_SEARCH_TYPE, viewModel.getName())
    outState.putInt(STATE_LAST_ENTRY_VIEWED, lastEntryViewed)
    super.onSaveInstanceState(outState)
  }

  override fun onBackPressed() {
    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
      closeDrawer()
    } else {
      super.onBackPressed()
      if (supportFragmentManager.backStackEntryCount == 0) {
        // since EntryListFragment isn't on the backstack, we know that's showing,
        // so restore title
        setToolbarTitle(viewModel.titleIdForSearchType())
      }
    }
  }

  override fun onPrepareOptionsMenu(menu: Menu): Boolean {
    MenuButtons.setExportVisibility(menu, viewModel.isExportVisible())
    MenuButtons.setUnfavoriteAllVisibility(menu, viewModel.isUnfavoriteAllVisible())
    return true
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.drawer, menu)
    val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

    searchViewMenuItem = menu.findItem(R.id.menu_search)
    val searchView = searchViewMenuItem!!.actionView as SearchView

    searchView.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
      override fun onViewAttachedToWindow(view: View) {
        // hide other buttons while search input is open
        MenuButtons.hideExtraButtons(menu)
        viewModel.getSearchTerm()?.let { query ->
          searchViewMenuItem?.expandActionView()
          searchView.setQuery(query, false)
        }
      }

      override fun onViewDetachedFromWindow(view: View) {
        if (viewModel.isSearch()) {
          // we performed a search, so hide the other buttons
          // (have to do this here because calling invalidateOptionsMenu while search input is open
          // makes the search input close)
          MenuButtons.hideExtraButtons(menu)
        } else {
          // refresh other menu buttons visibility based on current state, since
          // we hid the button when we opened the search input
          refreshMenuItems()
        }
      }
    })
    searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
    searchView.setIconifiedByDefault(false)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.menu_export) {
      ExportDialog(
        viewModel.getEntriesForExportAsync(),
        csvExportUiCallbacks
      ).show(supportFragmentManager)
      return true
    } else if (item.itemId == R.id.menu_remove_all_favorites) {
      explainUnfavoriteAll()
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  private val csvExportUiCallbacks: CsvExportUiCallbacks = object : CsvExportUiCallbacks {
    override fun onProgressUpdate(progress: Int?) {
      progress?.let {
        viewModel.setExportProgress(progress)
      }
    }

    override fun onPreExecute() {
      viewModel.setIsExporting(true)
    }

    override fun onPostExecute() {
      viewModel.setIsExporting(false)
      val csv = File(CsvExporter.fileLocation(this@DrawerActivity))
      val csvUri = FileProvider.getUriForFile(
        this@DrawerActivity, getString(R.string.fileprovider_package), csv
      )
      val shareIntent = Intent().apply {
        this.action = Intent.ACTION_SEND
        this.putExtra(Intent.EXTRA_STREAM, csvUri)
        this.type = "text/csv"
      }
      startActivity(
        Intent.createChooser(shareIntent, getString(R.string.share_csv))
      )
      analyticsLogger.logCsvSuccess()
    }

    override fun onCancelled() {
      viewModel.setIsExporting(false)
      Toast.makeText(this@DrawerActivity, R.string.csv_failed, Toast.LENGTH_SHORT).show()
      analyticsLogger.logCsvFailed()
    }
  }

  private fun explainUnfavoriteAll() {
    UnfavoriteAllDialog().show(supportFragmentManager)
  }

  private fun refreshMenuItems() {
    // this calls both onCreateOptionsMenu and onPrepareOptionsMenu
    invalidateOptionsMenu()
  }

  private fun showAbout() {
    val intent = Intent(this, AboutAppActivity::class.java)
    startActivity(intent)
  }

  private fun pagedEntriesControlObserver(pagedEntriesControl: PagedEntriesControl) {
    if (!isLaunchingWithEntry) {
      clearDefinitionBackstack()
    }
    isLaunchingWithEntry = false
    setToolbarTitle(viewModel.titleIdForSearchType())
    refreshMenuItems()
    analyticsLogger.logSearchResultsOrViewItemList(pagedEntriesControl)
  }

  private fun setToolbarTitle(@StringRes resId: Int) {
    binding.appBarDrawer.toolbarTitle.text = resources.getString(resId)
  }

  private fun jlptMenuIds(): List<Int> {
    return listOf(R.id.nav_jlptn1, R.id.nav_jlptn2, R.id.nav_jlptn3, R.id.nav_jlptn4, R.id.nav_jlptn5)
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    val id = item.itemId
    val jlptIds = jlptMenuIds()

    when {
      id == R.id.nav_search -> {
        openSearch()
      }
      id == R.id.nav_browse -> {
        viewModel.setPagedEntriesControl(PagedEntriesControl.Browse)
      }
      id == R.id.nav_favorites -> {
        viewModel.setPagedEntriesControl(PagedEntriesControl.Favorites)
      }
      jlptIds.indexOf(id) > -1 -> {
        viewModel.setPagedEntriesControl(PagedEntriesControl.JLPT(jlptIds.indexOf(id) + 1))
      }
      id == R.id.nav_about -> {
        showAbout()
      }
    }

    closeDrawer()
    return true
  }

  private fun openSearch() {
    searchViewMenuItem?.expandActionView()
  }

  private fun closeDrawer() {
    binding.drawerLayout.closeDrawer(GravityCompat.START)
  }

  companion object {
    private const val STATE_SEARCH_TYPE = "state_search_type"
    private const val STATE_SEARCH_TERM = "state_search_term"
    private const val STATE_JLPT_LEVEL = "state_jlpt_level"
    private const val STATE_LAST_ENTRY_VIEWED = "state_last_entry_viewed"
  }
}
