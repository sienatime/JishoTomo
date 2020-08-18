package net.emojiparty.android.jishotomo.data.room

import androidx.room.Dao
import androidx.room.Query
import net.emojiparty.android.jishotomo.data.models.CrossReferencedEntry

@Dao
interface SenseDao {
  @Query(
    "SELECT mainSenses.id AS senseId," +
      "  xRefEntries.id AS id, xRefEntries.primary_kanji, xRefEntries.primary_reading " +
      "  FROM senses AS mainSenses" +
      "  JOIN cross_references ON cross_references.sense_id = mainSenses.id" +
      "  JOIN senses AS xRefSenses ON xRefSenses.id = cross_references.cross_reference_sense_id" +
      "  JOIN entries AS xRefEntries ON xRefSenses.entry_id = xRefEntries.id" +
      "  WHERE mainSenses.entry_id = :entryId"
  )
  suspend fun getCrossReferencedEntries(entryId: Int): List<CrossReferencedEntry>
}
