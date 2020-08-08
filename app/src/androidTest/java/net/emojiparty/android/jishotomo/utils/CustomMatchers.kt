package net.emojiparty.android.jishotomo.utils

import android.view.View
import android.view.ViewGroup
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

object CustomMatchers {
  fun nthChildOf(
    parentMatcher: Matcher<View?>,
    childPosition: Int
  ): Matcher<View> {
    return object : TypeSafeMatcher<View>() {
      override fun describeTo(description: Description) {
        description.appendText("with $childPosition child view of type parentMatcher")
      }

      public override fun matchesSafely(view: View): Boolean {
        if (view.parent !is ViewGroup) {
          return parentMatcher.matches(view.parent)
        }
        val group = view.parent as ViewGroup
        return parentMatcher.matches(view.parent) && group.getChildAt(childPosition) == view
      }
    }
  }
}