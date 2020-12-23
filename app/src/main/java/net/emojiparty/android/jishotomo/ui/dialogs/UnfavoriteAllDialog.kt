package net.emojiparty.android.jishotomo.ui.dialogs

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import net.emojiparty.android.jishotomo.R
import net.emojiparty.android.jishotomo.analytics.AnalyticsLogger
import net.emojiparty.android.jishotomo.data.AppRepository

class UnfavoriteAllDialog(
  private val analyticsLogger: AnalyticsLogger = AnalyticsLogger()
) : CallbackDialog(
  R.string.explain_unfavorite_all,
  R.string.okay
) {
  override fun onConfirm() {
    lifecycleScope.launch {
      AppRepository().unfavoriteAll()
      analyticsLogger.logUnfavoriteAll()
    }
  }

  override val dialogTag = "unfavorite_all_explain"
}
