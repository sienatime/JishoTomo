package net.emojiparty.android.jishotomo.data

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import net.emojiparty.android.jishotomo.JishoTomoApp
import net.emojiparty.android.jishotomo.data.CJKUtil.isJapanese
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry
import net.emojiparty.android.jishotomo.data.room.Entry
import net.emojiparty.android.jishotomo.data.room.EntryDao
import net.emojiparty.android.jishotomo.data.room.SenseDao
import org.jetbrains.annotations.TestOnly
import javax.inject.Inject

private const val PAGE_SIZE = 20

class AppRepository {
  init {
    JishoTomoApp.appComponent?.inject(this)
  }

  @Inject
  lateinit var entryDao: EntryDao

  @Inject
  lateinit var senseDao: SenseDao

  fun getEntryWithAllSenses(entryId: Int) = entryDao.getEntryById(entryId)

  fun search(term: String): LiveData<PagedList<SearchResultEntry>> {
    val unicodeCodePoint = Character.codePointAt(term, 0)
    return if (isJapanese(unicodeCodePoint)) {
      val wildcardQuery = String.format("*%s*", term)
      LivePagedListBuilder(
        entryDao.searchByJapaneseTerm(wildcardQuery), PAGE_SIZE
      ).build()
    } else {
      LivePagedListBuilder(
        entryDao.searchByEnglishTerm(term), PAGE_SIZE
      ).build()
    }
  }

  fun browse(): LiveData<PagedList<SearchResultEntry>> {
    return LivePagedListBuilder(entryDao.browse(), PAGE_SIZE).build()
  }

  fun getFavorites(): LiveData<PagedList<SearchResultEntry>> {
    return LivePagedListBuilder(entryDao.getFavorites(), PAGE_SIZE).build()
  }

  fun getByJlptLevel(level: Int): LiveData<PagedList<SearchResultEntry>> {
    return LivePagedListBuilder(
      entryDao.findByJlptLevel(level), PAGE_SIZE
    ).build()
  }

  fun getAllFavorites(): List<EntryWithAllSenses> {
    return entryDao.getAllFavorites()
  }

  fun getAllByJlptLevel(jlptLevel: Int): List<EntryWithAllSenses> {
    return entryDao.getAllByJlptLevel(jlptLevel)
  }

  suspend fun getRandomEntryByJlptLevel(level: Int): SearchResultEntry {
    val jlptCount = entryDao.getJlptLevelCount(level)
    return entryDao.randomByJlptLevel(level, randomOffset(jlptCount))
  }

  private fun randomOffset(count: Int): Int {
    val max = count - 1
    return (Math.random() * max).toInt()
  }

  suspend fun toggleFavorite(entry: Entry) {
    entry.toggleFavorited()
    entryDao.updateEntry(entry)
  }

  suspend fun unfavoriteAll() = entryDao.unfavoriteAll()

  @TestOnly
  suspend fun getEntryByKanji(kanji: String): Entry = entryDao.getEntryByKanji(kanji)

  suspend fun getCrossReferences(entryId: Int) = senseDao.getCrossReferencedEntries(entryId)
}
