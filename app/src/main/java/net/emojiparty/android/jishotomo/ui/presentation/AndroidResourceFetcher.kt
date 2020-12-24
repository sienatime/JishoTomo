package net.emojiparty.android.jishotomo.ui.presentation

import android.content.res.Resources

class AndroidResourceFetcher(
  private val resources: Resources,
  private val packageName: String
) : ResourceFetcher {

  override fun getIdentifier(
    name: String,
    defType: String
  ): Int {
    return resources.getIdentifier(name, defType, packageName)
  }

  override fun getString(id: Int): String {
    return resources.getString(id)
  }

  override fun stringForJlptLevel(level: Int): Int {
    return getIdentifier("jlpt_n$level", "string")
  }
}
