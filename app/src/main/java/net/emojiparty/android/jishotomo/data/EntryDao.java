package net.emojiparty.android.jishotomo.data;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Relation;
import java.util.List;

@Dao
public interface EntryDao {
  //@Query("SELECT * FROM entries LIMIT 10") List<Entry> getAll();

  @Query("SELECT * FROM entries WHERE jlpt_level = :level")
  Entry findByJlptLevel(int level);

  @Query("SELECT * FROM entries WHERE primary_kanji LIKE :term")
  DataSource.Factory<Integer, Entry> search(String term);

  @Query("SELECT * FROM entries WHERE entries.id = :id")
  List<EntryWithAllSenses> getPrimarySenseEntry(int id);
  //DataSource.Factory<Integer, EntryWithAllSenses> getPrimarySenseEntry(int id);

  public class EntryWithAllSenses {
    @Embedded Entry entry;
    @Relation(parentColumn = "id", entityColumn = "entry_id")
    public List<Sense> senses;
  }
}
