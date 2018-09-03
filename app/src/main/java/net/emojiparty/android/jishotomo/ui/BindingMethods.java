package net.emojiparty.android.jishotomo.ui;

import android.databinding.BindingAdapter;
import android.view.View;

public class BindingMethods {
  @BindingAdapter({"visibleOrGone"})
  public static void setVisibility(View view, boolean isVisible) {
    int visibility = isVisible ? View.VISIBLE : View.GONE;
    view.setVisibility(visibility);
  }
}
