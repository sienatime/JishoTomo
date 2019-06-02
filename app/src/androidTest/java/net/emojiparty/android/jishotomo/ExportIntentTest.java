package net.emojiparty.android.jishotomo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import androidx.arch.core.executor.testing.CountingTaskExecutorRule;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.filters.LargeTest;
import androidx.test.runner.AndroidJUnit4;
import net.emojiparty.android.jishotomo.data.AppRepository;
import net.emojiparty.android.jishotomo.ui.activities.DrawerActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static net.emojiparty.android.jishotomo.utils.JishoTomoTestUtils.addFavoriteEntry;
import static net.emojiparty.android.jishotomo.utils.JishoTomoTestUtils.clickDrawerItem;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExportIntentTest {
  @Rule
  public CountingTaskExecutorRule mCountingTaskExecutorRule = new CountingTaskExecutorRule();

  @Rule
  public IntentsTestRule<DrawerActivity> intentsTestRule =
      new IntentsTestRule<>(DrawerActivity.class);

  @Before
  public void stubIntent() {
    // Stub the Intent.
    intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
  }

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
  public void itCanExportFavoritesToCsv() {
    addFavoriteEntry("七転び八起き");
    clickDrawerItem(R.id.nav_favorites);
    onView(withText("七転び八起き")).check(matches(isDisplayed()));
    onView(withId(R.id.menu_export)).perform(click());
    onView(withText("PROCEED"))
        .inRoot(isDialog())
        .check(matches(isDisplayed()));
    onView(withText("PROCEED")).perform(click());
  }

  @Test
  public void itCanExportJlptListToCsv() {
    // TODO
  }
}
