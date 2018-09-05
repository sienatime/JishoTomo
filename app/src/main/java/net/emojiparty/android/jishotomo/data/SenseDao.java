package net.emojiparty.android.jishotomo.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import java.util.List;

@Dao public interface SenseDao {
  @Query("SELECT senses.id, "
      + "entries.id AS entryId, entries.primary_kanji AS primaryKanji, entries.primary_reading AS primaryReading "
      + "FROM senses JOIN entries ON senses.entry_id = entries.id WHERE senses.id IN (:senseIds)")
  LiveData<List<SenseWithEntry>> getSenseWithEntry(List<Integer> senseIds);
}
