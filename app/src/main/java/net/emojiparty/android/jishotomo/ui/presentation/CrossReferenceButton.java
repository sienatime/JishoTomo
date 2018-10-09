package net.emojiparty.android.jishotomo.ui.presentation;

import android.content.Context;
import android.content.res.Resources;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import net.emojiparty.android.jishotomo.R;
import net.emojiparty.android.jishotomo.data.models.CrossReferencedEntry;

public class CrossReferenceButton {
  private Context context;
  private Resources resources;
  private float margin;

  public CrossReferenceButton(Context context) {
    this.context = context;
    this.resources = context.getResources();
    this.margin = resources.getDimension(R.dimen.cross_ref_button_margin);
  }

  public Button create(CrossReferencedEntry crossReferencedEntry) {
    int buttonStyleId = R.style.xref_button;
    Button button =
        new Button(new ContextThemeWrapper(context, buttonStyleId), null, buttonStyleId);
    button.setText(JapaneseLocaleSpan.all(crossReferencedEntry.getKanjiOrReading()));
    setMargins(button);
    button.setOnClickListener((View clickView) -> {
      EntryClickHandler.open(context, crossReferencedEntry.id);
    });
    return button;
  }

  private void setMargins(Button button) {
    LinearLayout.LayoutParams layoutParams =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    int marginInPixels = ScreenSize.dpsToPixels(margin, resources);
    layoutParams.setMarginEnd(marginInPixels);
    button.setLayoutParams(layoutParams);
  }
}
