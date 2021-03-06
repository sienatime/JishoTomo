package net.emojiparty.android.jishotomo.ui.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import kotlinx.android.synthetic.main.activity_drawer.drawer_layout
import kotlinx.android.synthetic.main.activity_drawer.nav_view
import kotlinx.android.synthetic.main.app_bar_drawer.drawer_toolbar
import kotlinx.android.synthetic.main.app_bar_drawer.toolbar_title
import kotlinx.android.synthetic.main.content_drawer.drawer_content_fragment
import kotlinx.android.synthetic.main.content_drawer.tablet_definition_fragment_container
import kotlinx.android.synthetic.main.fragment_entry_list.exporting
import net.emojiparty.android.jishotomo.JishoTomoApp
import net.emojiparty.android.jishotomo.R
import net.emojiparty.android.jishotomo.R.id
import net.emojiparty.android.jishotomo.R.layout
import net.emojiparty.android.jishotomo.R.string
import net.emojiparty.android.jishotomo.analytics.AnalyticsLogger
import net.emojiparty.android.jishotomo.data.csv.CsvExporter
import net.emojiparty.android.jishotomo.ui.csv.CsvExportUiCallbacks
import net.emojiparty.android.jishotomo.ui.dialogs.ExportDialog
import net.emojiparty.android.jishotomo.ui.dialogs.UnfavoriteAllDialog
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
  private var tabletFragmentContainer: FrameLayout? = null
  private var fragmentContainer: FragmentContainerView? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layout.activity_drawer)
    setSupportActionBar(drawer_toolbar)
    supportActionBar!!.setDisplayShowTitleEnabled(false) // I handle the title separately

    tabletFragmentContainer = tablet_definition_fragment_container
    fragmentContainer = drawer_content_fragment

    transactFragment(EntryListFragment())

    analyticsLogger = (application as JishoTomoApp).analyticsLogger

    if (savedInstanceState == null) {
      searchIntent(intent)
    } else {
      restoreFromBundle(savedInstanceState)
    }
    setupDrawer()
    setupNavigationView()

    viewModel.getEntries().observe(
      this,
      {
        refreshMenuItems()
      }
    )

    viewModel.getPagedEntriesControl().observe(
      this,
      {
        setPagedEntriesControl(it)
      }
    )

    // TODO add some callback to the EntryListFragment for this
//    toolbar_title.setOnClickListener { search_results_rv.scrollToPosition(0) }
  }

  override fun onDestroy() {
    searchViewMenuItem = null
    tabletFragmentContainer = null
    super.onDestroy()
  }

  fun isTablet(): Boolean {
    return tabletFragmentContainer != null
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
    // non-null on tablet
    tabletFragmentContainer?.let {
      val fragmentManager = supportFragmentManager
      fragmentManager.popBackStack(
        null, FragmentManager.POP_BACK_STACK_INCLUSIVE
      )
      if (fragmentManager.backStackEntryCount == 0) {
        val fragment = DefinitionFragment.instance(DefinitionFragment.ENTRY_EMPTY)
        fragmentManager.beginTransaction()
          .replace(id.tablet_definition_fragment_container, fragment)
          .commitAllowingStateLoss()
      }
    }
  }

  private fun transactFragmentOnTablet(fragment: Fragment) {
    supportFragmentManager.beginTransaction()
      .add(id.tablet_definition_fragment_container, fragment)
      .addToBackStack(null)
      .commit()
  }

  private fun transactFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction()
      .add(id.drawer_content_fragment, fragment)
      .addToBackStack(null)
      .commit()
  }

  fun addDefinitionFragment(entryId: Int) {
    lastEntryViewed = entryId
    val fragment = DefinitionFragment.instance(entryId)
    transactFragmentOnTablet(fragment)
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
      drawer_layout,
      drawer_toolbar,
      string.navigation_drawer_open,
      string.navigation_drawer_close
    )
    drawer_layout.addDrawerListener(toggle)
    toggle.syncState()
  }

  private fun setupNavigationView() {
    nav_view.setNavigationItemSelectedListener(this)
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
    if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
      closeDrawer()
    } else {
      super.onBackPressed()
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

    searchViewMenuItem = menu.findItem(id.menu_search)
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
    if (item.itemId == id.menu_export) {
      ExportDialog(
        viewModel.getEntriesForExportAsync(),
        csvExportUiCallbacks
      ).show(supportFragmentManager)
      return true
    } else if (item.itemId == id.menu_remove_all_favorites) {
      explainUnfavoriteAll()
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  private val csvExportUiCallbacks: CsvExportUiCallbacks = object : CsvExportUiCallbacks {
    override fun onProgressUpdate(progress: Int?) {
      exporting.progress = progress!!
    }

    override fun onPreExecute() {
      exporting.visibility = View.VISIBLE
    }

    override fun onPostExecute() {
      exporting.visibility = View.GONE
      val csv = File(CsvExporter.fileLocation(this@DrawerActivity))
      val csvUri = FileProvider.getUriForFile(
        this@DrawerActivity, getString(string.fileprovider_package), csv
      )
      val shareIntent = Intent().apply {
        this.action = Intent.ACTION_SEND
        this.putExtra(Intent.EXTRA_STREAM, csvUri)
        this.type = "text/csv"
      }
      startActivity(
        Intent.createChooser(shareIntent, getString(string.share_csv))
      )
      analyticsLogger.logCsvSuccess()
    }

    override fun onCancelled() {
      exporting.visibility = View.GONE
      Toast.makeText(this@DrawerActivity, string.csv_failed, Toast.LENGTH_SHORT).show()
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

  private fun setPagedEntriesControl(pagedEntriesControl: PagedEntriesControl) {
    clearDefinitionBackstack()
    toolbar_title.text = resources.getString(viewModel.titleIdForSearchType())
    refreshMenuItems()
    analyticsLogger.logSearchResultsOrViewItemList(pagedEntriesControl)
  }

  private fun jlptMenuIds(): List<Int> {
    return listOf(id.nav_jlptn1, id.nav_jlptn2, id.nav_jlptn3, id.nav_jlptn4, id.nav_jlptn5)
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
    drawer_layout.closeDrawer(GravityCompat.START)
  }

  companion object {
    private const val STATE_SEARCH_TYPE = "state_search_type"
    private const val STATE_SEARCH_TERM = "state_search_term"
    private const val STATE_JLPT_LEVEL = "state_jlpt_level"
    private const val STATE_LAST_ENTRY_VIEWED = "state_last_entry_viewed"
  }
}
