package net.emojiparty.android.jishotomo.ui.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog.Builder
import androidx.fragment.app.DialogFragment
import net.emojiparty.android.jishotomo.R.string

class CallbackDialog(
  private val dialogText: Int,
  private val confirmText: Int,
  private val onConfirm: () -> Unit
) : DialogFragment() {

  // https://developer.android.com/guide/topics/ui/dialogs
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return Builder(requireActivity())
        .setMessage(dialogText)
        .setPositiveButton(confirmText) { _: DialogInterface?, _: Int -> onConfirm() }
        .setNegativeButton(string.cancel, null)
        .create()
  }
}
