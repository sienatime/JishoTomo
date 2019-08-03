package net.emojiparty.android.jishotomo;

import android.widget.EditText;
import androidx.arch.core.executor.testing.CountingTaskExecutorRule;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import net.emojiparty.android.jishotomo.data.AppRepository;
import net.emojiparty.android.jishotomo.ui.activities.DrawerActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static net.emojiparty.android.jishotomo.utils.JishoTomoTestUtils.addFavoriteEntry;
import static net.emojiparty.android.jishotomo.utils.JishoTomoTestUtils.clickDrawerItem;
import static net.emojiparty.android.jishotomo.utils.JishoTomoTestUtils.openDrawer;
import static net.emojiparty.android.jishotomo.utils.RecyclerViewItemCountAssertion.withItemCount;
import static org.hamcrest.Matchers.greaterThan;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class DrawerActivityInstrumentedTests {
  @Rule
  public ActivityTestRule<DrawerActivity> activityRule =
      new ActivityTestRule<>(DrawerActivity.class);

  @Rule
  public CountingTaskExecutorRule mCountingTaskExecutorRule = new CountingTaskExecutorRule();

  @Before
  public void checkAppLoaded() {
    new AppRepository().unfavoriteAll();
    onView(withText("Jisho Tomo")).check(matches(isDisplayed()));
  }

  @After
  public void cleanup() {
    new AppRepository().unfavoriteAll();
  }

  @Test
  public void appLoads() throws Throwable {
    onView(withId(R.id.search_results_rv)).check(withItemCount(greaterThan(1)));
    onView(withId(R.id.search_results_rv)).perform(
        RecyclerViewActions.actionOnItemAtPosition(2, click()));
    onView(withId(R.id.def_kanji)).check(matches(isDisplayed()));
  }

  // BROWSE

  @Test
  public void itHasOnlySearchMenuItem() {
    onView(withId(R.id.menu_search)).check(matches(isDisplayed()));
    onView(withId(R.id.menu_export)).check(doesNotExist());
    onView(withId(R.id.menu_remove_all_favorites)).check(doesNotExist());
  }

  // SEARCH

  @Test
  public void itCanPerformSearchFromToolbar() throws Throwable {
    onView(withId(R.id.menu_search)).perform(click());

    performSearch("朝");
    onView(withText("あさ")).check(matches(isDisplayed()));
    onView(withText("morning")).check(matches(isDisplayed()));
  }

  @Test
  public void itCanPerformSearchFromDrawer() throws Throwable {
    clickDrawerItem(R.id.nav_search);

    performSearch("朝");
    onView(withText("あさ")).check(matches(isDisplayed()));
    onView(withText("morning")).check(matches(isDisplayed()));
  }

  // DRAWER

  @Test
  public void itHasItemsInTheDrawer() {
    openDrawer();
    onView(withText("Browse")).check(matches(isDisplayed()));
    onView(withText("Search")).check(matches(isDisplayed()));
    onView(withText("Favorites")).check(matches(isDisplayed()));
    onView(withText("About")).check(matches(isDisplayed()));
    onView(withText("JLPT Vocabulary Lists")).check(matches(isDisplayed()));
  }

  @Test
  public void itClosesTheDrawerWhenBackIsPressed() {
    openDrawer();
    Espresso.pressBack();
    onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
  }

  // FAVORITES

  @Test
  public void itShowsAllMenuItemsWhenThereAreFavorites() {
    addFavoriteEntry("七転び八起き");
    clickDrawerItem(R.id.nav_favorites);
    onView(withId(R.id.menu_search)).check(matches(isDisplayed()));
    onView(withId(R.id.menu_export)).check(matches(isDisplayed()));
    onView(withId(R.id.menu_remove_all_favorites)).check(matches(isDisplayed()));
  }

  @Test
  public void itShowsOnlySearchMenuItemWhenThereAreNoFavorites() {
    clickDrawerItem(R.id.nav_favorites);
    onView(withId(R.id.menu_search)).check(matches(isDisplayed()));
    onView(withId(R.id.menu_export)).check(doesNotExist());
    onView(withId(R.id.menu_remove_all_favorites)).check(doesNotExist());
  }

  @Test
  public void itCanRemoveAllFavorites() throws Throwable {
    addFavoriteEntry("七転び八起き");
    clickDrawerItem(R.id.nav_favorites);
    onView(withId(R.id.search_results_rv)).check(withItemCount(1));
    onView(withId(R.id.menu_remove_all_favorites)).perform(click());
    onView(withText("OK")).perform(click());
    drain();
    onView(withId(R.id.search_results_rv)).check(withItemCount(0));
  }

  // ABOUT

  @Test
  public void itCanOpenAboutActivity() {
    clickDrawerItem(R.id.nav_about);
    onView(withId(R.id.about_text)).check(matches(isDisplayed()));
  }

  // JLPT LISTS

  @Test
  public void itShowsSearchAndExportMenuItems () {
    clickDrawerItem(R.id.nav_jlptn1);
    onView(withId(R.id.menu_search)).check(matches(isDisplayed()));
    onView(withId(R.id.menu_export)).check(matches(isDisplayed()));
    onView(withId(R.id.menu_remove_all_favorites)).check(doesNotExist());
  }

  // HELPER METHODS

  private void drain() throws TimeoutException, InterruptedException {
    mCountingTaskExecutorRule.drainTasks(1, TimeUnit.MINUTES);
  }

  private void performSearch(String searchTerm) throws Throwable {
    onView(isAssignableFrom(EditText.class)).perform(replaceText(searchTerm),
        pressImeActionButton());
    drain();
    onView(withId(R.id.search_results_rv)).check(withItemCount(greaterThan(1)));
  }
}
