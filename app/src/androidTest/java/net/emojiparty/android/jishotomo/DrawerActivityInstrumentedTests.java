package net.emojiparty.android.jishotomo;

import androidx.test.espresso.IdlingResource;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import net.emojiparty.android.jishotomo.ui.activities.DrawerActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

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

  @Test
  public void appLoads() {
    onView(withText("Jisho Tomo")).check(matches(isDisplayed()));
    IdlingResource;
  }

  // drawer
  //  pressing back closes the drawer when it is open
  //  items in the drawer

  // search
  //   perform search from menu
  //      hides extra buttons
  //      shows search results
  //   cancel search

  // browse
  //   initial db load
  //   menu items

  // favorites
  //   menu items
  //   sort order
  //   exporting
  //   unfavorite all

  // about

  // jlpt list
  //   menu items
  //   export

  // definition
  //   all the states the view can be in
  //   cross-references
  //   add favorite
  //   remove favorite

  // restoring state? rotating?

  // widget
  //   can i test this? idk
}
