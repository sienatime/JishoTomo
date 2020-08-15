package net.emojiparty.android.jishotomo.data.models

import androidx.room.Embedded
import androidx.room.Ignore
import net.emojiparty.android.jishotomo.data.room.Sense

data class SenseWithCrossReferences(
  @Embedded var sense: Sense
) {
  // TODO do i need this class??
  @Ignore
  var crossReferences: List<CrossReferencedEntry> = emptyList()
}
