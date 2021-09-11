package net.emojiparty.android.jishotomo

import android.content.Intent
import android.widget.EditText
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import kotlinx.coroutines.runBlocking
import net.emojiparty.android.jishotomo.R.id
import net.emojiparty.android.jishotomo.R.string
import net.emojiparty.android.jishotomo.data.AppRepository
import net.emojiparty.android.jishotomo.ui.activities.DrawerActivity
import net.emojiparty.android.jishotomo.ui.fragments.DefinitionFragment
import net.emojiparty.android.jishotomo.utils.CustomMatchers
import net.emojiparty.android.jishotomo.utils.JishoTomoTestUtils
import net.emojiparty.android.jishotomo.utils.RecyclerViewItemCountAssertion.Companion.withItemCount
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit.MINUTES

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class DrawerActivityInstrumentedTests {
  @get:Rule
  var activityRule = ActivityTestRule(
    DrawerActivity::class.java,
    false,
    false
  )

  @get:Rule
  var mCountingTaskExecutorRule = CountingTaskExecutorRule()

  @Before
  fun checkAppLoaded() {
    runBlocking {
      AppRepository().unfavoriteAll()
    }
  }

  @After
  fun cleanup() {
    runBlocking {
      AppRepository().unfavoriteAll()
    }
  }

  private fun launchActivity() {
    activityRule.launchActivity(null)
    onView(withText("Jisho Tomo"))
      .check(matches(isDisplayed()))
  }

  @Test
  fun appLoads() {
    launchActivity()
    onView(withId(id.search_results_rv))
      .check(
        withItemCount(greaterThan(1))
      )
    onView(withId(id.search_results_rv))
      .perform(
        actionOnItemAtPosition<ViewHolder>(
          2, click()
        )
      )
    onView(withId(id.def_kanji))
      .check(matches(isDisplayed()))
  }

  // region BROWSE
  @Test
  fun itHasOnlySearchMenuItem() {
    launchActivity()
    onView(withId(id.menu_search))
      .check(matches(isDisplayed()))
    onView(withId(id.menu_export))
      .check(doesNotExist())
    onView(withId(id.menu_remove_all_favorites))
      .check(doesNotExist())
  }

  // endregion

  // region SEARCH
  @Test
  fun itCanPerformSearchFromToolbar() {
    launchActivity()
    onView(withId(id.menu_search))
      .perform(click())
    performSearch("朝")
    onView(withText("あさ"))
      .check(matches(isDisplayed()))
    onView(withText("morning"))
      .check(matches(isDisplayed()))
    onView(withId(id.no_results))
      .check(
        matches(
          not(isDisplayed())
        )
      )
  }

  @Test
  fun itCanPerformSearchFromDrawer() {
    launchActivity()
    JishoTomoTestUtils.clickDrawerItem(id.nav_search)
    performSearch("朝")
    onView(withText("あさ"))
      .check(matches(isDisplayed()))
    onView(withText("morning"))
      .check(matches(isDisplayed()))
  }

  @Test
  fun itCanPerformASearchWithNoResults() {
    launchActivity()
    val searchTerm = "alkdsjflksdjlf"
    JishoTomoTestUtils.clickDrawerItem(id.nav_search)
    performSearch(searchTerm, false)
    onView(withId(id.no_results))
      .check(
        matches(
          withText(
            "No results found for \"$searchTerm\". Try a different search."
          )
        )
      )
  }

  // endregion

  // region DRAWER
  @Test
  fun itHasItemsInTheDrawer() {
    launchActivity()
    JishoTomoTestUtils.openDrawer()
    onView(withText("Browse"))
      .check(matches(isDisplayed()))
    onView(withText("Search"))
      .check(matches(isDisplayed()))
    onView(withText("Favorites"))
      .check(matches(isDisplayed()))
    onView(withText("About"))
      .check(matches(isDisplayed()))
    onView(withText("JLPT Vocabulary Lists"))
      .check(matches(isDisplayed()))
  }

  @Test
  fun itClosesTheDrawerWhenBackIsPressed() {
    launchActivity()
    JishoTomoTestUtils.openDrawer()
    pressBack()
    onView(withId(id.drawer_layout))
      .check(matches(isClosed()))
  }

  // endregion

  // region FAVORITES
  @Test
  fun itShowsAllMenuItemsWhenThereAreFavorites() {
    JishoTomoTestUtils.addFavoriteEntry("七転び八起き")
    launchActivity()
    JishoTomoTestUtils.clickDrawerItem(id.nav_favorites)
    onView(withId(id.menu_search))
      .check(matches(isDisplayed()))
    onView(withId(id.menu_export))
      .check(matches(isDisplayed()))
    onView(withId(id.menu_remove_all_favorites))
      .check(matches(isDisplayed()))
    onView(withId(id.no_results))
      .check(
        matches(
          not(isDisplayed())
        )
      )
  }

  @Test
  fun itShowsOnlySearchMenuItemWhenThereAreNoFavorites() {
    launchActivity()
    JishoTomoTestUtils.clickDrawerItem(id.nav_favorites)
    onView(withId(id.menu_search))
      .check(matches(isDisplayed()))
    onView(withId(id.menu_export))
      .check(doesNotExist())
    onView(withId(id.menu_remove_all_favorites))
      .check(doesNotExist())
    onView(withId(id.no_results))
      .check(matches(withText(string.no_favorites)))
  }

  @Test
  fun itCanRemoveAllFavorites() {
    JishoTomoTestUtils.addFavoriteEntry("七転び八起き")
    launchActivity()
    JishoTomoTestUtils.clickDrawerItem(id.nav_favorites)
    onView(withId(id.search_results_rv))
      .check(withItemCount(1))
    onView(withId(id.menu_remove_all_favorites))
      .perform(click())
    onView(withText("OK"))
      .inRoot(isDialog())
      .check(matches(isDisplayed()))
    onView(withText("OK"))
      .perform(click())
    drain()
    onView(withId(id.search_results_rv))
      .check(withItemCount(0))
    onView(withId(id.no_results))
      .check(matches(withText(string.no_favorites)))
  }

  // endregion

  // region ABOUT
  @Test
  fun itCanOpenAboutActivity() {
    launchActivity()
    JishoTomoTestUtils.clickDrawerItem(id.nav_about)
    onView(withId(id.about_text))
      .check(matches(isDisplayed()))
  }

  // endregion

  // region JLPT LISTS
  @Test
  fun itShowsSearchAndExportMenuItems() {
    launchActivity()
    JishoTomoTestUtils.clickDrawerItem(id.nav_jlptn1)
    onView(withId(id.menu_search))
      .check(matches(isDisplayed()))
    onView(withId(id.menu_export))
      .check(matches(isDisplayed()))
    onView(withId(id.menu_remove_all_favorites))
      .check(doesNotExist())
  }

  // endregion

  // region ENTRY

  @Test
  fun itDisplaysAnEntry() {
    launchActivityWithEntry("七転び八起き")
    onView(withId(id.def_reading))
      .check(matches(withText("ななころびやおき")))
    onView(withText("NOUN, VERB"))
      .check(matches(isDisplayed()))
    onView(withText(CoreMatchers.containsString("not giving up")))
      .check(matches(isDisplayed()))
    onView(withText("NOUN"))
      .check(matches(isDisplayed()))
    onView(withText(CoreMatchers.containsString("ups and downs in life")))
      .check(matches(isDisplayed()))
    onView(withText("ALTERNATE KANJI"))
      .check(matches(isDisplayed()))
    onView(withText("ALTERNATE READINGS"))
      .check(matches(isDisplayed()))
  }

  @Test
  fun itCanNavigateToCrossReferences() {
    launchActivityWithEntry("漢数字ゼロ")
    onView(withText("See also:"))
      .check(matches(isDisplayed()))
    onView(withId(id.li_cross_reference_container))
      .check(matches(ViewMatchers.hasChildCount(2)))
    onView(
      CustomMatchers.nthChildOf(withId(id.li_cross_reference_container), 0)
    )
      .perform(click())
    onView(withText("circle (sometimes used for zero)"))
      .check(matches(isDisplayed()))
  }

  // flaky
  @Test
  fun itCanAddAndRemoveFavorite() {
    launchActivityWithEntry("七転び八起き")
    onView(withId(id.fab))
      .check(
        matches(ViewMatchers.withContentDescription("Add Entry to Favorites"))
      )
    onView(withId(id.fab))
      .perform(click())
    onView(withId(id.fab))
      .check(
        matches(
          ViewMatchers.withContentDescription("Remove Entry from Favorites")
        )
      )
    onView(withId(id.fab))
      .perform(click())
    onView(withId(id.fab))
      .check(
        matches(ViewMatchers.withContentDescription("Add Entry to Favorites"))
      )
  }

  // endregion

  // region HELPER METHODS
  private fun drain() {
    mCountingTaskExecutorRule.drainTasks(1, MINUTES)
  }

  private fun performSearch(
    searchTerm: String,
    resultsExpected: Boolean = true
  ) {
    onView(isAssignableFrom(EditText::class.java))
      .perform(
        replaceText(searchTerm),
        pressImeActionButton()
      )
    drain()
    if (resultsExpected) {
      onView(withId(id.search_results_rv))
        .check(
          withItemCount(
            greaterThan(1)
          )
        )
    }
  }

  private fun launchActivityWithEntry(kanji: String) {
    runBlocking {
      val entry = AppRepository().getEntryByKanji(kanji)
      val intent = Intent().apply {
        this.putExtra(DefinitionFragment.ENTRY_ID_EXTRA, entry.id)
      }

      activityRule.launchActivity(intent)

      onView(withText("Jisho Tomo"))
        .check(matches(isDisplayed()))
      onView(withId(id.def_kanji))
        .check(matches(withText(kanji)))
    }
  }

  // endregion
}
