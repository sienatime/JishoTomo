package net.emojiparty.android.jishotomo.utils;

import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import net.emojiparty.android.jishotomo.R;
import net.emojiparty.android.jishotomo.data.AppRepository;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerActions.open;
import static androidx.test.espresso.contrib.DrawerMatchers.isOpen;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class JishoTomoTestUtils {
  public static void openDrawer() {
    onView(ViewMatchers.withId(R.id.drawer_layout)).perform(open());
    onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
  }

  public static void clickDrawerItem(int nav_id) {
    openDrawer();
    onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(nav_id));
  }

  public static void addFavoriteEntry(String kanji) {
    AppRepository repo = new AppRepository();
    repo.getEntryByKanji(kanji, repo::toggleFavorite);
  }
}
