package net.emojiparty.android.jishotomo.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import net.emojiparty.android.jishotomo.R;

public class ExplainExportDialog extends DialogFragment {
  private OnExport callback;

  public interface OnExport {
    void proceed();
  }

  public void setCallback(OnExport callback) {
    this.callback = callback;
  }

  // https://developer.android.com/guide/topics/ui/dialogs
  @NonNull @Override public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setMessage(R.string.export_instructions)
        .setPositiveButton(R.string.export_yes, (DialogInterface dialog, int id) -> {
          callback.proceed();
        })
        .setNegativeButton(R.string.export_no, (DialogInterface dialog, int id) -> {

        });
    return builder.create();
  }
}
