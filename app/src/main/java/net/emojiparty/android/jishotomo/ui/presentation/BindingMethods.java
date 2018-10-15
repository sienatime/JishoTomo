package net.emojiparty.android.jishotomo.ui.presentation;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.text.SpannableString;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import net.emojiparty.android.jishotomo.R;
import net.emojiparty.android.jishotomo.data.SemicolonSplit;
import net.emojiparty.android.jishotomo.data.models.CrossReferencedEntry;

public class BindingMethods {
  @BindingAdapter({ "visibleOrGone" })
  public static void setVisibility(View view, boolean isVisible) {
    int visibility = isVisible ? View.VISIBLE : View.GONE;
    view.setVisibility(visibility);
  }

  @BindingAdapter({ "crossReferenceLinks" }) public static void setCrossReferenceLinks(View view,
      List<CrossReferencedEntry> crossReferencedEntries) {
    LinearLayout linearLayout = (LinearLayout) view;
    if (crossReferencedEntries == null
        || crossReferencedEntries.size() == 0
        || linearLayout.getChildCount() > 0) {
      return;
    }

    Context context = view.getContext();
    CrossReferenceButton crossReferenceButton = new CrossReferenceButton(context);

    for (int i = 0; i < crossReferencedEntries.size(); i++) {
      CrossReferencedEntry crossReferencedEntry = crossReferencedEntries.get(i);
      linearLayout.addView(crossReferenceButton.create(crossReferencedEntry));
    }
  }

  @BindingAdapter({ "appliesToText" })
  public static void setAppliesToText(View view, String appliesTo) {
    TextView textView = (TextView) view;
    Context context = view.getContext();
    String literalAppliesTo = context.getString(R.string.applies_to);
    String allAppliesTo = SemicolonSplit.splitAndJoin(appliesTo);
    String formatted =
        String.format(context.getString(R.string.applies_to_format), literalAppliesTo,
            allAppliesTo);

    SpannableString spannableString = JapaneseLocaleSpan.mixed(formatted, literalAppliesTo);
    textView.setText(spannableString);
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
  @BindingAdapter({ "textInJapanese" }) public static void setTextWithJapanese(View view,
      String text) {
    if (text == null) {
      text = "";
    }
    TextView textView = (TextView) view;
    textView.setText(JapaneseLocaleSpan.all(text));
  }
}
