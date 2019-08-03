package net.emojiparty.android.jishotomo;

import android.content.Intent;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import net.emojiparty.android.jishotomo.data.AppRepository;
import net.emojiparty.android.jishotomo.data.room.Entry;
import net.emojiparty.android.jishotomo.ui.activities.DefinitionActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static net.emojiparty.android.jishotomo.utils.CustomMatchers.nthChildOf;
import static net.emojiparty.android.jishotomo.ui.activities.DefinitionFragment.ENTRY_ID_EXTRA;
import static org.hamcrest.CoreMatchers.containsString;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DefinitionActivityInstrumentedTests {
  @Rule
  public ActivityTestRule<DefinitionActivity> activityRule =
      new ActivityTestRule<>(DefinitionActivity.class, false, false);

  @Before
  public void setup() {
    new AppRepository().unfavoriteAll();
  }

  @After
  public void cleanup() {
    new AppRepository().unfavoriteAll();
  }

  @Test
  public void itDisplaysAnEntry() {
    launchActivityWithEntry("七転び八起き");
    onView(withId(R.id.def_reading)).check(matches(withText("ななころびやおき")));
    onView(withText("NOUN, VERB")).check(matches(isDisplayed()));
    onView(withText(containsString("not giving up"))).check(matches(isDisplayed()));
    onView(withText("NOUN")).check(matches(isDisplayed()));
    onView(withText(containsString("ups and downs in life"))).check(matches(isDisplayed()));
    onView(withText("ALTERNATE KANJI")).check(matches(isDisplayed()));
    onView(withText("ALTERNATE READINGS")).check(matches(isDisplayed()));
  }

  @Test
  public void itCanNavigateToCrossReferences() {
    launchActivityWithEntry("漢数字ゼロ");
    onView(withText("See also:")).check(matches(isDisplayed()));
    onView(withId(R.id.li_cross_reference_container)).check(matches(hasChildCount(2)));
    onView(nthChildOf(withId(R.id.li_cross_reference_container), 0)).perform(click());
    onView(withId(R.id.def_kanji)).check(matches(withText("○")));
  }

  @Test
  public void itCanAddAndRemoveFavorite() {
    launchActivityWithEntry("七転び八起き");
    onView(withId(R.id.fab)).check(matches(withContentDescription("Add Entry to Favorites")));
    onView(withId(R.id.fab)).perform(click());
    onView(withId(R.id.fab)).check(matches(withContentDescription("Remove Entry from Favorites")));
    onView(withId(R.id.fab)).perform(click());
    onView(withId(R.id.fab)).check(matches(withContentDescription("Add Entry to Favorites")));
  }

  private void launchActivityWithEntry(String kanji) {
    new AppRepository().getEntryByKanji(kanji, (Entry entry) -> {
      Intent intent = new Intent();
      intent.putExtra(ENTRY_ID_EXTRA, entry.getId());
      activityRule.launchActivity(intent);
    });
    onView(withText("Jisho Tomo")).check(matches(isDisplayed()));
    onView(withId(R.id.def_kanji)).check(matches(withText(kanji)));
  }
}
