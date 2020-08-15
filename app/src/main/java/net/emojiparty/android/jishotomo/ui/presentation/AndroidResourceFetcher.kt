package net.emojiparty.android.jishotomo.ui.presentation

import android.content.res.Resources

class AndroidResourceFetcher(private val resources: Resources) : ResourceFetcher {
  override fun getIdentifier(
    name: String,
    defType: String,
    defPackage: String
  ): Int {
    return resources.getIdentifier(name, defType, defPackage)
  }

  override fun getString(id: Int): String {
    return resources.getString(id)
  }
}
