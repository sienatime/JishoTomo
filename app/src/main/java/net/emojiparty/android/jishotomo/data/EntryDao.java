package net.emojiparty.android.jishotomo.data;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import java.util.List;

@Dao
public interface EntryDao {
  @Query("SELECT id, primary_kanji AS primaryKanji, primary_reading AS primaryReading FROM entries ORDER BY id ASC")
  DataSource.Factory<Integer, SearchResultEntry> getAll();

  @Query("SELECT * FROM entries WHERE jlpt_level = :level")
  DataSource.Factory<Integer, Entry> findByJlptLevel(int level);

  @Query("SELECT * FROM entries WHERE primary_kanji LIKE :term")
  DataSource.Factory<Integer, Entry> search(String term);

  @Query("SELECT * FROM entries WHERE entries.id = :id LIMIT 1")
  LiveData<EntryWithAllSenses> getEntryById(int id);

  @Query("SELECT entries.id, primary_kanji AS primaryKanji, primary_reading AS primaryReading FROM entries JOIN senses ON senses.entry_id = entries.id WHERE senses.id IN (:senseIds)")
  LiveData<List<PrimaryOnlyEntry>> getEntriesBySenseId(List<Integer> senseIds);
}
