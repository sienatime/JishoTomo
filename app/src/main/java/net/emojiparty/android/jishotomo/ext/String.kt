package net.emojiparty.android.jishotomo.ext

fun String.semicolonSplit(): List<String> {
  return this.split(";".toRegex())
}

fun String.splitAndJoin(delimiter: String = ", "): String {
  val list: List<String> = this.semicolonSplit()
  return list.joinToString(delimiter)
}
