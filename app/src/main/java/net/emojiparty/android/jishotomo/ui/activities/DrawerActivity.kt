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
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import kotlinx.android.synthetic.main.activity_definition.toolbar
import kotlinx.android.synthetic.main.app_bar_drawer.toolbar_title
import kotlinx.android.synthetic.main.content_drawer.definition_fragment_container
import kotlinx.android.synthetic.main.content_drawer.loading
import kotlinx.android.synthetic.main.content_drawer.no_results
import kotlinx.android.synthetic.main.content_drawer.search_results_rv
import net.emojiparty.android.jishotomo.JishoTomoApp
import net.emojiparty.android.jishotomo.R
import net.emojiparty.android.jishotomo.R.id
import net.emojiparty.android.jishotomo.R.layout
import net.emojiparty.android.jishotomo.R.string
import net.emojiparty.android.jishotomo.analytics.AnalyticsLogger
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry
import net.emojiparty.android.jishotomo.ui.activities.DefinitionFragment.Companion.instance
import net.emojiparty.android.jishotomo.ui.adapters.PagedEntriesAdapter
import net.emojiparty.android.jishotomo.ui.presentation.FavoritesMenu
import net.emojiparty.android.jishotomo.ui.presentation.MenuButtons
import net.emojiparty.android.jishotomo.ui.presentation.StringForJlptLevel
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesControl
import net.emojiparty.android.jishotomo.ui.viewmodels.PagedEntriesViewModel

class DrawerActivity : AppCompatActivity(),
    OnNavigationItemSelectedListener {
  private lateinit var viewModel: PagedEntriesViewModel

  private var searchViewMenuItem: MenuItem? = null

  private var adapter: PagedEntriesAdapter? = null
  private lateinit var analyticsLogger: AnalyticsLogger
  private var lastEntryViewed = DefinitionFragment.ENTRY_EMPTY
  var fragmentContainer: FrameLayout? = null

  private val STATE_SEARCH_TYPE = "state_search_type"
  private val STATE_SEARCH_TERM = "state_search_term"
  private val STATE_JLPT_LEVEL = "state_jlpt_level"
  private val STATE_LAST_ENTRY_VIEWED = "state_last_entry_viewed"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layout.activity_drawer)
    setSupportActionBar(toolbar)
    supportActionBar!!.setDisplayShowTitleEnabled(false) // I handle the title separately

    fragmentContainer = definition_fragment_container

    val viewModel: PagedEntriesViewModel by viewModels()
    this.viewModel = viewModel

    analyticsLogger = (application as JishoTomoApp).analyticsLogger
    setRecyclerViewWithNewAdapter()

    if (savedInstanceState == null) {
      searchIntent(intent)
    } else {
      restoreFromBundle(savedInstanceState)
    }
    setupDrawer(toolbar)
    setupNavigationView()
    setupRecyclerView()

    toolbar_title.setOnClickListener { search_results_rv.scrollToPosition(0) }
  }

  override fun onDestroy() {
    searchViewMenuItem = null
    adapter = null
    fragmentContainer = null
    super.onDestroy()
  }

  private fun restoreFromBundle(bundle: Bundle) {
    val searchType = bundle.getString(STATE_SEARCH_TYPE)
    val searchTerm = bundle.getString(STATE_SEARCH_TERM)

    val pagedEntriesControl = if (bundle.containsKey(STATE_JLPT_LEVEL)) {
      PagedEntriesControl.JLPT(bundle.getInt(STATE_JLPT_LEVEL))
    } else if (searchTerm != null) {
      PagedEntriesControl.Search(searchTerm)
    } else if (searchType == PagedEntriesControl.Favorites.name) {
      PagedEntriesControl.Favorites
    } else {
      PagedEntriesControl.Browse
    }

    setPagedEntriesControl(pagedEntriesControl)
    val lastEntryViewedFromBundle = bundle.getInt(STATE_LAST_ENTRY_VIEWED)
    if (lastEntryViewedFromBundle != DefinitionFragment.ENTRY_EMPTY) {
      addDefinitionFragment(lastEntryViewedFromBundle)
    }
  }

  private fun setRecyclerViewWithNewAdapter() {
    adapter = PagedEntriesAdapter(layout.list_item_entry)
    search_results_rv.adapter = adapter
  }

  private fun clearDefinitionBackstack() {
    // non-null on tablet
    fragmentContainer?.let {
      val fragmentManager = supportFragmentManager
      fragmentManager.popBackStack(
          null, FragmentManager.POP_BACK_STACK_INCLUSIVE
      )
      if (fragmentManager.backStackEntryCount == 0) {
        val fragment =
          instance(DefinitionFragment.ENTRY_EMPTY)
        fragmentManager.beginTransaction()
            .replace(id.definition_fragment_container, fragment)
            .commitAllowingStateLoss()
      }
    }
  }

  fun addDefinitionFragment(entryId: Int) {
    lastEntryViewed = entryId
    val fragment = instance(entryId)
    supportFragmentManager.beginTransaction()
        .add(id.definition_fragment_container, fragment)
        .addToBackStack(null)
        .commit()
  }

  // https://developer.android.com/training/search/setup
  // https://developer.android.com/guide/topics/search/search-dialog
  private fun searchIntent(intent: Intent) {
    val pagedEntriesControl = if (Intent.ACTION_SEARCH == intent.action) {
      val query = intent.getStringExtra(SearchManager.QUERY)
      PagedEntriesControl.Search(query)
    } else {
      PagedEntriesControl.Browse
    }
    setPagedEntriesControl(pagedEntriesControl)
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    searchIntent(intent)
  }

  // Paging library reference https://developer.android.com/topic/libraries/architecture/paging
  private fun setupRecyclerView() {
    viewModel.entries.observe(
        this,
        Observer<PagedList<SearchResultEntry>> { entries: PagedList<SearchResultEntry> ->
        loading.visibility = View.INVISIBLE
        adapter?.submitList(entries)
        setNoResultsText(entries.size)
        invalidateOptionsMenu()
      }
    )
  }

  private fun setNoResultsText(size: Int) {
    if (size == 0) {
      no_results.visibility = View.VISIBLE
      no_results.text = noResultsText()
    } else {
      no_results.visibility = View.GONE
    }
  }

  private fun noResultsText(): String {
    return when (val control = viewModel.pagedEntriesControl) {
      is PagedEntriesControl.Favorites -> getString(string.no_favorites)
      is PagedEntriesControl.Search -> String.format(
          getString(string.no_search_results), control.searchTerm
      )
      else -> getString(string.nothing_here)
    }
  }

  private fun setupDrawer(toolbar: Toolbar) {
    val drawer = findViewById<DrawerLayout>(id.drawer_layout)
    val toggle = ActionBarDrawerToggle(
        this, drawer, toolbar, string.navigation_drawer_open,
        string.navigation_drawer_close
    )
    drawer.addDrawerListener(toggle)
    toggle.syncState()
  }

  private fun setupNavigationView() {
    val navigationView = findViewById<NavigationView>(id.nav_view)
    navigationView.setNavigationItemSelectedListener(this)
  }

  override fun onSaveInstanceState(outState: Bundle) {

    when (val control = viewModel.pagedEntriesControl) {
      is PagedEntriesControl.JLPT -> outState.putInt(STATE_JLPT_LEVEL, control.level)
      is PagedEntriesControl.Search -> outState.putString(STATE_SEARCH_TERM, control.searchTerm)
    }

    outState.putString(STATE_SEARCH_TYPE, viewModel.pagedEntriesControl.name)

    outState.putInt(STATE_LAST_ENTRY_VIEWED, lastEntryViewed)
    super.onSaveInstanceState(outState)
  }

  override fun onBackPressed() {
    val drawer = findViewById<DrawerLayout>(id.drawer_layout)
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START)
    } else {
      super.onBackPressed()
    }
  }

  override fun onPrepareOptionsMenu(menu: Menu): Boolean {
    val hasFavorites = viewModel.pagedEntriesControl is PagedEntriesControl.Favorites && adapter!!.itemCount > 0
    val isJlpt = viewModel.pagedEntriesControl is PagedEntriesControl.JLPT
    MenuButtons.setExportVisibility(menu, hasFavorites || isJlpt)
    MenuButtons.setUnfavoriteAllVisibility(menu, hasFavorites)
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
      }

      override fun onViewDetachedFromWindow(view: View) {
        if (viewModel.pagedEntriesControl is PagedEntriesControl.Search) {
          // we performed a search, so hide the other buttons
          // (have to do this here because calling invalidateOptionsMenu while search input is open
          // makes the search input close)
          MenuButtons.hideExtraButtons(menu)
        } else {
          // refresh other menu buttons visibility based on current state, since
          // we hid the button when we opened the search input
          invalidateOptionsMenu()
        }
      }
    })
    searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
    searchView.setIconifiedByDefault(false)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val favoritesMenu = FavoritesMenu(application as JishoTomoApp)
    if (item.itemId == id.menu_export) {
      favoritesMenu.explainCsvExport(this, viewModel.pagedEntriesControl)
      return true
    } else if (item.itemId == id.menu_remove_all_favorites) {
      favoritesMenu.explainUnfavoriteAll(this)
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  private fun showAbout() {
    val intent = Intent(this, AboutAppActivity::class.java)
    startActivity(intent)
  }

  private fun setPagedEntriesControl(pagedEntriesControl: PagedEntriesControl) {
    toolbar_title.text = resources.getString(titleIdForSearchType(pagedEntriesControl))
    // this is so that the PagedListAdapter does not try to perform a diff
    // against the two lists when changing search types. the app was really laggy
    // when changing lists without re-instantiating the adapter.
    setRecyclerViewWithNewAdapter()
    clearDefinitionBackstack()
    no_results.visibility = View.GONE
    loading.visibility = View.VISIBLE
    viewModel.pagedEntriesControlLiveData.value = pagedEntriesControl
    invalidateOptionsMenu()
    analyticsLogger.logSearchResultsOrViewItemList(pagedEntriesControl)
  }

  private fun jlptMenuIds(): List<Int> {
    return listOf(id.nav_jlptn1, id.nav_jlptn2, id.nav_jlptn3, id.nav_jlptn4, id.nav_jlptn5)
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    val id = item.itemId
    var pagedEntriesControl: PagedEntriesControl? = null
    val jlptIds = jlptMenuIds()

    if (id == R.id.nav_search) {
      searchViewMenuItem!!.expandActionView()
    } else if (id == R.id.nav_browse) {
      pagedEntriesControl = PagedEntriesControl.Browse
    } else if (id == R.id.nav_favorites) {
      pagedEntriesControl = PagedEntriesControl.Favorites
    } else if (jlptIds.indexOf(id) > -1) {
      pagedEntriesControl = PagedEntriesControl.JLPT(jlptIds.indexOf(id) + 1)
    } else if (id == R.id.nav_about) {
      showAbout()
    }

    pagedEntriesControl?.let {
      setPagedEntriesControl(it)
    }

    val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
    drawer.closeDrawer(GravityCompat.START)
    return true
  }

  private fun titleIdForSearchType(pagedEntriesControl: PagedEntriesControl): Int {
    return when (pagedEntriesControl) {
      is PagedEntriesControl.Browse -> string.app_name
      is PagedEntriesControl.Favorites -> string.favorites
      is PagedEntriesControl.JLPT -> StringForJlptLevel.getId(
          pagedEntriesControl.level, this
      )
      is PagedEntriesControl.Search -> string.search_results
    }
  }
}
