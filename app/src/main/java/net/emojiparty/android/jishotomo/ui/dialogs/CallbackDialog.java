package net.emojiparty.android.jishotomo.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import net.emojiparty.android.jishotomo.R;

public class CallbackDialog extends DialogFragment {
  private OnConfirm callback;
  private int confirmText;
  private int dialogText;

  public CallbackDialog(OnConfirm callback, int dialogText, int confirmText) {
    this.callback = callback;
    this.dialogText = dialogText;
    this.confirmText = confirmText;
  }

  public interface OnConfirm {
    void proceed();
  }


  // https://developer.android.com/guide/topics/ui/dialogs
  @NonNull @Override public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setMessage(dialogText)
        .setPositiveButton(confirmText, (DialogInterface dialog, int id) -> {
          callback.proceed();
        })
        .setNegativeButton(R.string.cancel, null);
    return builder.create();
  }
}
