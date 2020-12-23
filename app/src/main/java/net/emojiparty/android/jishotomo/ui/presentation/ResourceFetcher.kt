package net.emojiparty.android.jishotomo.ui.presentation

interface ResourceFetcher {
  fun getIdentifier(name: String, defType: String, defPackage: String): Int
  fun getString(id: Int): String
  fun stringForJlptLevel(level: Int): Int
}
