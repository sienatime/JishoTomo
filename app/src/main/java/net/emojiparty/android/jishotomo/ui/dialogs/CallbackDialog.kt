package net.emojiparty.android.jishotomo.ui.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog.Builder
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import net.emojiparty.android.jishotomo.R.string

open class CallbackDialog(
  private val dialogText: Int,
  private val confirmText: Int
) : DialogFragment() {

  // https://developer.android.com/guide/topics/ui/dialogs
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return Builder(requireActivity())
      .setMessage(dialogText)
      .setPositiveButton(confirmText) { _: DialogInterface?, _: Int -> onConfirm() }
      .setNegativeButton(string.cancel, null)
      .create()
  }

  open fun onConfirm() { }

  protected open val dialogTag: String = ""

  fun show(fragmentManager: FragmentManager) {
    show(fragmentManager, dialogTag)
  }
}
