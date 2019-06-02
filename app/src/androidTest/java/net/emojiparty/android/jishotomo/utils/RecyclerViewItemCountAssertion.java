package net.emojiparty.android.jishotomo.utils;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import org.hamcrest.Matcher;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.CoreMatchers.is;

// https://stackoverflow.com/questions/36399787/how-to-count-recyclerview-items-with-espresso
public class RecyclerViewItemCountAssertion implements ViewAssertion {
  private final Matcher<Integer> matcher;

  public static RecyclerViewItemCountAssertion withItemCount(int expectedCount) {
    return withItemCount(is(expectedCount));
  }

  public static RecyclerViewItemCountAssertion withItemCount(Matcher<Integer> matcher) {
    return new RecyclerViewItemCountAssertion(matcher);
  }

  private RecyclerViewItemCountAssertion(Matcher<Integer> matcher) {
    this.matcher = matcher;
  }

  @Override
  public void check(View view, NoMatchingViewException noViewFoundException) {
    if (noViewFoundException != null) {
      throw noViewFoundException;
    }

    RecyclerView recyclerView = (RecyclerView) view;
    RecyclerView.Adapter adapter = recyclerView.getAdapter();
    assertThat(adapter.getItemCount(), matcher);
  }
}
