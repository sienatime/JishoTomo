package net.emojiparty.android.jishotomo.data.room;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import java.util.List;
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses;
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry;

@Dao public interface EntryDao {
  @Update void updateEntry(Entry entry);

  @Transaction @Query("SELECT * FROM entries WHERE entries.id = :id LIMIT 1")
  LiveData<EntryWithAllSenses> getEntryById(int id);

  @Transaction @Query("SELECT id, primary_kanji, primary_reading FROM entries ORDER BY id ASC")
  DataSource.Factory<Integer, SearchResultEntry> browse();

  @Transaction @Query(
      "SELECT entries.id, entries.primary_kanji, entries.primary_reading FROM entries "
          + "JOIN entriesFts ON (entries.id = entriesFts.docid) WHERE entriesFts MATCH :term")
  DataSource.Factory<Integer, SearchResultEntry> searchByJapaneseTerm(String term);

  @Transaction @Query(
      "SELECT entries.id, entries.primary_kanji, entries.primary_reading FROM entries "
          + "JOIN senses ON senses.entry_id = entries.id "
          + "JOIN sensesFts ON (senses.id = sensesFts.docid) WHERE sensesFts MATCH :term")
  DataSource.Factory<Integer, SearchResultEntry> searchByEnglishTerm(String term);

  @Transaction @Query("SELECT id, primary_kanji, primary_reading FROM entries WHERE favorited IS NOT NULL ORDER BY favorited DESC")
  DataSource.Factory<Integer, SearchResultEntry> getFavorites();

  @Transaction @Query("SELECT * FROM entries WHERE favorited IS NOT NULL ORDER BY favorited DESC")
  List<EntryWithAllSenses> getAllFavorites();

  @Transaction @Query("SELECT * FROM entries WHERE jlpt_level = :level ORDER BY id ASC")
  List<EntryWithAllSenses> getAllByJlptLevel(Integer level);

  @Transaction
  @Query("SELECT id, primary_kanji, primary_reading FROM entries WHERE jlpt_level = :level ORDER BY id ASC")
  DataSource.Factory<Integer, SearchResultEntry> findByJlptLevel(Integer level);

  // WIDGET

  @Transaction
  @Query("SELECT id, primary_kanji, primary_reading FROM entries WHERE jlpt_level = :level ORDER BY id ASC LIMIT 1 OFFSET :offset")
  SearchResultEntry randomByJlptLevel(Integer level, int offset);

  @Query("SELECT COUNT(id) FROM entries WHERE jlpt_level = :level") int getJlptLevelCount(
      Integer level);
}
