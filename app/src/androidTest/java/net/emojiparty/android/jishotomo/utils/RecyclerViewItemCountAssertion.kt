package net.emojiparty.android.jishotomo.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher

// https://stackoverflow.com/questions/36399787/how-to-count-recyclerview-items-with-espresso
class RecyclerViewItemCountAssertion private constructor(private val matcher: Matcher<Int>) :
  ViewAssertion {
  override fun check(
    view: View,
    noViewFoundException: NoMatchingViewException?
  ) {
    noViewFoundException?.let {
      throw noViewFoundException
    }
    val recyclerView = view as RecyclerView
    val adapter = recyclerView.adapter
    ViewMatchers.assertThat(adapter!!.itemCount, matcher)
  }

  companion object {
    fun withItemCount(expectedCount: Int): RecyclerViewItemCountAssertion {
      return withItemCount(
        CoreMatchers.`is`(expectedCount)
      )
    }

    fun withItemCount(matcher: Matcher<Int>): RecyclerViewItemCountAssertion {
      return RecyclerViewItemCountAssertion(matcher)
    }
  }
}
