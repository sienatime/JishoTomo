package net.emojiparty.android.jishotomo.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A Sense is like a definition, but is also self-referential,
 * because it can also cross-reference other Senses belonging to different Entries.
 *
 * In its most basic form, the cross references are stored as a semi-colon-separated
 * string of expressions. This is not used in this app, but was used to create the join
 * table represented by CrossReference.
 */
@Entity(tableName = "senses", indices = [Index("entry_id")])
data class Sense(
  @PrimaryKey val id: Int,
  @ColumnInfo(name = "entry_id") val entryId: Int,
  @ColumnInfo(name = "parts_of_speech") val partsOfSpeech: String? = null,
  val glosses: String,
  @ColumnInfo(name = "applies_to") val appliesTo: String? = null,
  @ColumnInfo(name = "cross_references") val crossReferences: String? = null
)
