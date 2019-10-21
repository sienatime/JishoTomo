package net.emojiparty.android.jishotomo.ui.presentation;

import android.content.Context;

public class StringForJlptLevel {
  public static int getId(Integer level, Context context) {
    return context.getResources()
        .getIdentifier("jlpt_n" + level, "string", context.getPackageName());
  }
}
