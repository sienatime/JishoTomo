package net.emojiparty.android.jishotomo.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "senses", indices = [Index("entry_id")])
data class Sense(
  @PrimaryKey val id: Int,
  @ColumnInfo(name = "entry_id") val entryId: Int,
  @ColumnInfo(name = "parts_of_speech") val partsOfSpeech: String? = null,
  val glosses: String,
  @ColumnInfo(name = "applies_to") val appliesTo: String? = null,
  @ColumnInfo(name = "cross_references") val crossReferences: String? = null
)
