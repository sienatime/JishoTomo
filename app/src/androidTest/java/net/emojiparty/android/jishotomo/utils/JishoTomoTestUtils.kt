package net.emojiparty.android.jishotomo.utils

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import net.emojiparty.android.jishotomo.R.id
import net.emojiparty.android.jishotomo.data.AppRepository
import net.emojiparty.android.jishotomo.data.room.Entry

object JishoTomoTestUtils {
  fun openDrawer() {
    onView(withId(id.drawer_layout))
        .perform(open())
    onView(withId(id.drawer_layout))
        .check(matches(isOpen()))
  }

  fun clickDrawerItem(nav_id: Int) {
    openDrawer()
    onView(withId(id.nav_view))
        .perform(NavigationViewActions.navigateTo(nav_id))
  }

  fun addFavoriteEntry(kanji: String) {
    val repo = AppRepository()
    repo.getEntryByKanji(kanji) { entry: Entry -> repo.toggleFavorite(entry) }
  }
}