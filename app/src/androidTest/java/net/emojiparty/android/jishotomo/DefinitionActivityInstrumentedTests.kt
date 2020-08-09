package net.emojiparty.android.jishotomo

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import net.emojiparty.android.jishotomo.R.id
import net.emojiparty.android.jishotomo.data.AppRepository
import net.emojiparty.android.jishotomo.data.room.Entry
import net.emojiparty.android.jishotomo.ui.activities.DefinitionActivity
import net.emojiparty.android.jishotomo.ui.activities.DefinitionFragment
import net.emojiparty.android.jishotomo.utils.CustomMatchers.nthChildOf
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class DefinitionActivityInstrumentedTests {
  @get:Rule
  var activityRule = ActivityTestRule(
      DefinitionActivity::class.java, false, false
  )

  @Before
  fun setup() {
    AppRepository().unfavoriteAll()
  }

  @After
  fun cleanup() {
    AppRepository().unfavoriteAll()
  }

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
        .check(matches(hasChildCount(2)))
    onView(
        nthChildOf(withId(id.li_cross_reference_container), 0)
    )
        .perform(click())
    onView(withId(id.def_kanji))
        .check(matches(withText("○")))
  }

  // flaky
  @Test
  fun itCanAddAndRemoveFavorite() {
    launchActivityWithEntry("七転び八起き")
    onView(withId(id.fab))
        .check(
            matches(withContentDescription("Add Entry to Favorites"))
        )
    onView(withId(id.fab))
        .perform(click())
    onView(withId(id.fab))
        .check(
            matches(
                withContentDescription("Remove Entry from Favorites")
            )
        )
    onView(withId(id.fab))
        .perform(click())
    onView(withId(id.fab))
        .check(
            matches(withContentDescription("Add Entry to Favorites"))
        )
  }

  private fun launchActivityWithEntry(kanji: String) {
    AppRepository().getEntryByKanji(
        kanji
    ) { entry: Entry ->
      val intent = Intent()
      intent.putExtra(DefinitionFragment.ENTRY_ID_EXTRA, entry.id)
      activityRule.launchActivity(intent)
    }
    onView(withText("Jisho Tomo"))
        .check(matches(isDisplayed()))
    onView(withId(id.def_kanji))
        .check(matches(withText(kanji)))
  }
}
