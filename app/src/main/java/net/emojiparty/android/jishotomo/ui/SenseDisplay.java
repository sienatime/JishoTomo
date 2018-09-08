package net.emojiparty.android.jishotomo.ui;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import java.util.LinkedHashSet;
import java.util.List;
import net.emojiparty.android.jishotomo.data.SemicolonSplit;
import net.emojiparty.android.jishotomo.data.room.Sense;

public class SenseDisplay {
  public static String formatPartsOfSpeech(Sense sense, Context context) {
    Resources resources = context.getResources();
    String packageName = context.getPackageName();
    List<String> partsOfSpeech = SemicolonSplit.split(sense.getPartsOfSpeech());

    LinkedHashSet<String> localizedPartsOfSpeech = new LinkedHashSet<>();
    for (String partOfSpeech : partsOfSpeech) {
      String key = getPartOfSpeechKey(partOfSpeech);
      int stringId = resources.getIdentifier(key, "string", packageName);
      localizedPartsOfSpeech.add(resources.getString(stringId));
    }
    return TextUtils.join(", ", localizedPartsOfSpeech);
  }

  private static String getPartOfSpeechKey(String partOfSpeech) {
    if (partOfSpeech.equals("int")) {
      return "intj";
    } else {
      return partOfSpeech.replace("-", "_");
    }
  }
}
