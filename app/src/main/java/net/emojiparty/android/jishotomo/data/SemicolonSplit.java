package net.emojiparty.android.jishotomo.data;

import androidx.annotation.Nullable;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SemicolonSplit {
  public static List<String> split(@Nullable String semicolonSeparatedString) {
    if (semicolonSeparatedString != null) {
      String[] list = semicolonSeparatedString.split(";");
      return new ArrayList<>(Arrays.asList(list));
    }
    return new ArrayList<>();
  }

  public static String join(List<String> list) {
    return TextUtils.join(", ", list);
  }

  public static String join(List<String> list, String delimiter) {
    return TextUtils.join(delimiter, list);
  }

  public static String splitAndJoin(@Nullable String semicolonSeparatedString) {
    ArrayList<String> list = (ArrayList<String>) split(semicolonSeparatedString);
    return join(list);
  }

  public static String splitAndJoin(@Nullable String semicolonSeparatedString, String delimiter) {
    ArrayList<String> list = (ArrayList<String>) split(semicolonSeparatedString);
    return join(list, delimiter);
  }
}
