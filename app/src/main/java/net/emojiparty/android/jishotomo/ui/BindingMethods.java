package net.emojiparty.android.jishotomo.ui;

import android.databinding.BindingAdapter;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.LocaleSpan;
import android.view.View;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;
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

  @BindingAdapter({ "jlptPill" }) public static void setJlptPill(View view, Integer jlptLevel) {
    if (jlptLevel != null) {
      int stringId = StringForJlptLevel.getId(jlptLevel, view.getContext());
      ((TextView) view).setText(stringId);
      view.setVisibility(View.VISIBLE);
    } else {
      view.setVisibility(View.GONE);
    }
  }

  // so that TalkBack will read these TextViews in Japanese
  @BindingAdapter({ "textInJapanese" }) public static void setTextWithJapanese(View view, String text) {
    if (text != null) {
      TextView textView = (TextView) view;
      SpannableString spannable = new SpannableString(text);
      spannable.setSpan(new LocaleSpan(Locale.JAPANESE), 0, spannable.length(), 0);
      textView.setText(spannable);
    }
  }
}
