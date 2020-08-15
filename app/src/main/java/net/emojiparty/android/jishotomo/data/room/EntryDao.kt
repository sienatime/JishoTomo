package net.emojiparty.android.jishotomo.data.room

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry
import org.jetbrains.annotations.TestOnly

@Dao
interface EntryDao {
  @Update
  fun updateEntry(entry: Entry)

  @Transaction
  @Query("SELECT * FROM entries WHERE entries.id = :id LIMIT 1")
  fun getEntryById(id: Int): LiveData<EntryWithAllSenses>

  @Transaction
  @Query("SELECT id, primary_kanji, primary_reading FROM entries ORDER BY id ASC")
  fun browse(): DataSource.Factory<Int, SearchResultEntry>

  @Transaction
  @Query(
    "SELECT entries.id, entries.primary_kanji, entries.primary_reading FROM entries " +
      "JOIN entriesFts ON (entries.id = entriesFts.docid) WHERE entriesFts MATCH :term"
  )
  fun searchByJapaneseTerm(term: String): DataSource.Factory<Int, SearchResultEntry>

  @Transaction
  @Query(
    "SELECT entries.id, entries.primary_kanji, entries.primary_reading FROM entries " +
      "JOIN senses ON senses.entry_id = entries.id " +
      "JOIN sensesFts ON (senses.id = sensesFts.docid) WHERE sensesFts MATCH :term"
  )
  fun searchByEnglishTerm(term: String): DataSource.Factory<Int, SearchResultEntry>

  @Transaction
  @Query(
    "SELECT id, primary_kanji, primary_reading FROM entries WHERE favorited IS NOT NULL ORDER BY favorited DESC"
  )
  fun getFavorites(): DataSource.Factory<Int, SearchResultEntry>

  @Transaction
  @Query("SELECT * FROM entries WHERE favorited IS NOT NULL ORDER BY favorited DESC")
  fun getAllFavorites(): List<EntryWithAllSenses>

  @Query("UPDATE entries SET favorited = null WHERE favorited IS NOT NULL ")
  suspend fun unfavoriteAll(): Int

  @Transaction
  @Query("SELECT * FROM entries WHERE jlpt_level = :level ORDER BY id ASC")
  fun getAllByJlptLevel(level: Int): List<EntryWithAllSenses>

  @Transaction
  @Query(
    "SELECT id, primary_kanji, primary_reading FROM entries WHERE jlpt_level = :level ORDER BY id ASC"
  )
  fun findByJlptLevel(level: Int): DataSource.Factory<Int, SearchResultEntry>

  @TestOnly
  @Query("SELECT * FROM entries WHERE primary_kanji = :kanji LIMIT 1")
  suspend fun getEntryByKanji(kanji: String): Entry

  // WIDGET
  @Transaction
  @Query(
    "SELECT id, primary_kanji, primary_reading FROM entries WHERE jlpt_level = :level ORDER BY id ASC LIMIT 1 OFFSET :offset"
  )
  fun randomByJlptLevel(level: Int, offset: Int): SearchResultEntry

  @Query("SELECT COUNT(id) FROM entries WHERE jlpt_level = :level")
  fun getJlptLevelCount(level: Int): Int
}
