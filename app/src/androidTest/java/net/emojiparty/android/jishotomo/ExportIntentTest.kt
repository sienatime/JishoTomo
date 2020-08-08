package net.emojiparty.android.jishotomo

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import net.emojiparty.android.jishotomo.R.id
import net.emojiparty.android.jishotomo.data.AppRepository
import net.emojiparty.android.jishotomo.ui.activities.DrawerActivity
import net.emojiparty.android.jishotomo.utils.JishoTomoTestUtils
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ExportIntentTest {
  @get:Rule
  var mCountingTaskExecutorRule = CountingTaskExecutorRule()

  @get:Rule
  var intentsTestRule = IntentsTestRule(
      DrawerActivity::class.java
  )

  @Before fun stubIntent() {
    Intents.intending(hasAction(Intent.ACTION_CHOOSER))
        .respondWith(ActivityResult(Activity.RESULT_OK, null))
  }

  @Before fun checkAppLoaded() {
    AppRepository().unfavoriteAll()
    onView(withText("Jisho Tomo"))
        .check(matches(isDisplayed()))
  }

  @After fun cleanup() {
    AppRepository().unfavoriteAll()
  }

  @Test fun itCanExportFavoritesToCsv() {
    JishoTomoTestUtils.addFavoriteEntry("七転び八起き")
    JishoTomoTestUtils.clickDrawerItem(id.nav_favorites)
    onView(withText("七転び八起き"))
        .check(matches(isDisplayed()))
    onView(withId(id.menu_export))
        .perform(click())
    onView(withText("PROCEED"))
        .inRoot(isDialog())
        .check(matches(isDisplayed()))
    onView(withText("PROCEED"))
        .perform(click())
  }

  @Test fun itCanExportJlptListToCsv() {
    JishoTomoTestUtils.clickDrawerItem(id.nav_jlptn1)
    onView(withText("明白"))
        .check(matches(isDisplayed()))
    onView(withId(id.menu_export))
        .perform(click())
    onView(withText("PROCEED"))
        .inRoot(isDialog())
        .check(matches(isDisplayed()))
    onView(withText("PROCEED"))
        .perform(click())
  }
}
