package net.emojiparty.android.jishotomo.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import java.util.List;

@Dao
public interface SenseDao {
  @Query("SELECT * FROM senses WHERE entry_id = :entryId ORDER BY id ASC") List<Sense> getSensesByEntryId(int entryId);

}
