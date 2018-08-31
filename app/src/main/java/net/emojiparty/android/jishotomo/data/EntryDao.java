package net.emojiparty.android.jishotomo.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import java.util.List;

@Dao
public interface EntryDao {
  @Query("SELECT * FROM entries LIMIT 10") List<Entry> getAll();

  @Query("SELECT * FROM entries WHERE jlpt_level = :level")
  Entry findByJlptLevel(int level);
}
