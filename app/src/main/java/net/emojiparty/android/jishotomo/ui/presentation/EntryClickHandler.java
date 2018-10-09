package net.emojiparty.android.jishotomo.ui.presentation;

import android.content.Context;
import android.content.Intent;
import net.emojiparty.android.jishotomo.ui.activities.DefinitionActivity;
import net.emojiparty.android.jishotomo.ui.activities.DrawerActivity;

import static net.emojiparty.android.jishotomo.ui.activities.DefinitionFragment.ENTRY_ID_EXTRA;

public class EntryClickHandler {
  public static void open(Context context, int entryId) {
    if (isDrawerActivityWithFragment(context)) {
      ((DrawerActivity) context).addDefinitionFragment(entryId);
    } else {
      Intent intent = new Intent(context, DefinitionActivity.class);
      intent.putExtra(ENTRY_ID_EXTRA, entryId);
      context.startActivity(intent);
    }
  }

  private static boolean isDrawerActivityWithFragment(Context context) {
    return (context instanceof DrawerActivity)
        && ((DrawerActivity) context).fragmentContainer != null;
  }
}
