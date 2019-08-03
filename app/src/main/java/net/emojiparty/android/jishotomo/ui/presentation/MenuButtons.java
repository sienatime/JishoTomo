package net.emojiparty.android.jishotomo.ui.presentation;

import android.view.Menu;
import android.view.MenuItem;

public class MenuButtons {
  public static void hideExtraButtons(Menu menu) {
    setExportVisibility(menu, false);
    setUnfavoriteAllVisibility(menu, false);
  }

  public static void setExportVisibility(Menu menu, boolean visible) {
    MenuItem exportIcon = menu.getItem(1);
    exportIcon.setVisible(visible);
  }

  public static void setUnfavoriteAllVisibility(Menu menu, boolean visible) {
    MenuItem unfavoriteAllIcon = menu.getItem(2);
    unfavoriteAllIcon.setVisible(visible);
  }
}
