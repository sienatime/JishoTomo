package net.emojiparty.android.jishotomo.ui.presentation

import android.view.Menu
import androidx.core.view.size

object MenuButtons {
  private const val EXPORT_INDEX = 1
  private const val UNFAVORITE_ALL_INDEX = 2

  fun hideExtraButtons(menu: Menu) {
    setExportVisibility(menu, false)
    setUnfavoriteAllVisibility(menu, false)
  }

  fun setExportVisibility(
    menu: Menu,
    visible: Boolean
  ) {
    if (menu.size >= EXPORT_INDEX) {
      val exportIcon = menu.getItem(EXPORT_INDEX)
      exportIcon.isVisible = visible
    }
  }

  fun setUnfavoriteAllVisibility(
    menu: Menu,
    visible: Boolean
  ) {
    if (menu.size >= UNFAVORITE_ALL_INDEX) {
      val unfavoriteAllIcon = menu.getItem(UNFAVORITE_ALL_INDEX)
      unfavoriteAllIcon.isVisible = visible
    }
  }
}
