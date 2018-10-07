package net.emojiparty.android.jishotomo.ui.presentation;

import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.LocaleSpan;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;
import net.emojiparty.android.jishotomo.R;
import net.emojiparty.android.jishotomo.data.SemicolonSplit;
import net.emojiparty.android.jishotomo.data.models.CrossReferencedEntry;
import net.emojiparty.android.jishotomo.ui.activities.DefinitionActivity;

import static net.emojiparty.android.jishotomo.ui.activities.DefinitionActivity.ENTRY_ID_EXTRA;

public class BindingMethods {
  @BindingAdapter({ "visibleOrGone" })
  public static void setVisibility(View view, boolean isVisible) {
    int visibility = isVisible ? View.VISIBLE : View.GONE;
    view.setVisibility(visibility);
  }

  @BindingAdapter({ "crossReferenceLinks" }) public static void setCrossReferenceLinks(View view,
      List<CrossReferencedEntry> crossReferencedEntries) {
    LinearLayout linearLayout = (LinearLayout) view;
    if (crossReferencedEntries == null || crossReferencedEntries.size() == 0 || linearLayout.getChildCount() > 0) {
      return;
    }

    Context context = view.getContext();
    int buttonStyle = R.style.xref_button;

    for (int i = 0; i < crossReferencedEntries.size(); i++) {
      CrossReferencedEntry crossReferencedEntry = crossReferencedEntries.get(i);
      Button button = new Button(new ContextThemeWrapper(context, buttonStyle), null, buttonStyle);
      SpannableString spannable = new SpannableString(crossReferencedEntry.getKanjiOrReading());
      spannable.setSpan(new LocaleSpan(Locale.JAPANESE), 0, spannable.length(), 0);
      button.setText(spannable);
      button.setOnClickListener((View clickView) -> {
        Intent intent = new Intent(context, DefinitionActivity.class);
        intent.putExtra(ENTRY_ID_EXTRA, crossReferencedEntry.id);
        context.startActivity(intent);
      });
      linearLayout.addView(button);
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

    SpannableString spannableString = MixedLocaleSpan.format(formatted, literalAppliesTo);
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
    if (text != null) {
      TextView textView = (TextView) view;
      SpannableString spannable = new SpannableString(text);
      spannable.setSpan(new LocaleSpan(Locale.JAPANESE), 0, spannable.length(), 0);
      textView.setText(spannable);
    }
  }
}
