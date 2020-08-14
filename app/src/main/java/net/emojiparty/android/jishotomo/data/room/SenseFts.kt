package net.emojiparty.android.jishotomo.data.room

import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = Sense::class)
@Entity(tableName = "sensesFts")
data class SenseFts(val glosses: String)
