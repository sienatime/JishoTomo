package net.emojiparty.android.jishotomo.data.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry
import org.jetbrains.annotations.TestOnly

/**
 * Functions that return a DataSource should not be `suspend`ing,
 * since they already have their own async stuff built in
 */
@Dao
interface EntryDao {
  @Update
  suspend fun updateEntry(entry: Entry)

  @Transaction
  @Query("SELECT * FROM entries WHERE entries.id = :id LIMIT 1")
  suspend fun getEntryById(id: Int): EntryWithAllSenses

  @Transaction
  @Query("SELECT id, primary_kanji, primary_reading FROM entries ORDER BY id ASC")
  fun browse(): PagingSource<Int, SearchResultEntry>

  @Transaction
  @Query(
    "SELECT entries.id, entries.primary_kanji, entries.primary_reading FROM entries " +
      "JOIN entriesFts ON (entries.id = entriesFts.docid) WHERE entriesFts MATCH :term"
  )
  fun searchByJapaneseTerm(term: String): PagingSource<Int, SearchResultEntry>

  @Transaction
  @Query(
    "SELECT entries.id, entries.primary_kanji, entries.primary_reading FROM entries " +
      "JOIN senses ON senses.entry_id = entries.id " +
      "JOIN sensesFts ON (senses.id = sensesFts.docid) WHERE sensesFts MATCH :term"
  )
  fun searchByEnglishTerm(term: String): PagingSource<Int, SearchResultEntry>

  @Transaction
  @Query(
    "SELECT id, primary_kanji, primary_reading FROM entries WHERE favorited IS NOT NULL ORDER BY favorited DESC"
  )
  fun getFavorites(): PagingSource<Int, SearchResultEntry>

  @Transaction
  @Query("SELECT * FROM entries WHERE favorited IS NOT NULL ORDER BY favorited DESC")
  suspend fun getAllFavorites(): List<EntryWithAllSenses>

  @Query("UPDATE entries SET favorited = null WHERE favorited IS NOT NULL ")
  suspend fun unfavoriteAll(): Int

  @Transaction
  @Query("SELECT * FROM entries WHERE jlpt_level = :level ORDER BY id ASC")
  suspend fun getAllByJlptLevel(level: Int): List<EntryWithAllSenses>

  @Transaction
  @Query(
    "SELECT id, primary_kanji, primary_reading FROM entries WHERE jlpt_level = :level ORDER BY id ASC"
  )
  fun findByJlptLevel(level: Int): PagingSource<Int, SearchResultEntry>

  @TestOnly
  @Query("SELECT * FROM entries WHERE primary_kanji = :kanji LIMIT 1")
  suspend fun getEntryByKanji(kanji: String): Entry

  // WIDGET
  @Transaction
  @Query(
    "SELECT id, primary_kanji, primary_reading FROM entries WHERE jlpt_level = :level ORDER BY id ASC LIMIT 1 OFFSET :offset"
  )
  suspend fun randomByJlptLevel(level: Int, offset: Int): SearchResultEntry

  @Query("SELECT COUNT(id) FROM entries WHERE jlpt_level = :level")
  suspend fun getJlptLevelCount(level: Int): Int
}
