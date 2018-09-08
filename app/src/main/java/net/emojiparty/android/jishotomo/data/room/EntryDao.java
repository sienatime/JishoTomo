package net.emojiparty.android.jishotomo.data.room;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses;
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry;

@Dao
public interface EntryDao {
  @Query("SELECT id, primary_kanji, primary_reading FROM entries ORDER BY id ASC")
  DataSource.Factory<Integer, SearchResultEntry> getAll();

  @Query("SELECT id, primary_kanji, primary_reading FROM entries WHERE jlpt_level = :level")
  DataSource.Factory<Integer, SearchResultEntry> findByJlptLevel(int level);

  @Query("SELECT id, primary_kanji, primary_reading FROM entries WHERE primary_kanji LIKE :term")
  DataSource.Factory<Integer, SearchResultEntry> search(String term);

  @Query("SELECT * FROM entries WHERE entries.id = :id LIMIT 1")
  LiveData<EntryWithAllSenses> getEntryById(int id);
}
