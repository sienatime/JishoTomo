package net.emojiparty.android.jishotomo.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A CrossReference is just two sense IDs.
 */
@Entity(
  tableName = "cross_references",
  indices = [Index("sense_id")]
)
data class CrossReference(
  @PrimaryKey var id: Int = 0,
  @ColumnInfo(name = "sense_id") var senseId: Int = 0,
  @ColumnInfo(name = "cross_reference_sense_id") var crossReferenceSenseId: Int = 0
)
