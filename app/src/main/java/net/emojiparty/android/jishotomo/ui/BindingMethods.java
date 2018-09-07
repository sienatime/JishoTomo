package net.emojiparty.android.jishotomo.ui;

import android.databinding.BindingAdapter;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import java.util.List;
import net.emojiparty.android.jishotomo.data.models.CrossReferencedEntry;

public class BindingMethods {
  @BindingAdapter({ "visibleOrGone" })
  public static void setVisibility(View view, boolean isVisible) {
    int visibility = isVisible ? View.VISIBLE : View.GONE;
    view.setVisibility(visibility);
  }

  @BindingAdapter({ "crossReferenceLinks" }) public static void setCrossReferenceLinks(View view,
      List<CrossReferencedEntry> crossReferencedEntries) {
    if (crossReferencedEntries == null || crossReferencedEntries.size() == 0) {
      view.setVisibility(View.GONE);
    } else {
      view.setVisibility(View.VISIBLE);

      String text = CrossReferenceText.format(view, crossReferencedEntries);
      TextView textView = (TextView) view;
      textView.setText(Html.fromHtml(text));
      textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
  }
}
