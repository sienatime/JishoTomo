package net.emojiparty.android.jishotomo.data;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

@Dao
public interface EntryDao {
  @Query("SELECT * FROM entries ORDER BY id ASC")
  DataSource.Factory<Integer, EntryWithAllSenses> getAll();

  @Query("SELECT * FROM entries WHERE jlpt_level = :level")
  DataSource.Factory<Integer, Entry> findByJlptLevel(int level);

  @Query("SELECT * FROM entries WHERE primary_kanji LIKE :term")
  DataSource.Factory<Integer, Entry> search(String term);

  @Query("SELECT * FROM entries WHERE entries.id = :id LIMIT 1")
  LiveData<EntryWithAllSenses> getEntryById(int id);
}
