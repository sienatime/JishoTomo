package net.emojiparty.android.jishotomo.data

import androidx.paging.PagingSource
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

  suspend fun getEntryWithAllSenses(entryId: Int): EntryWithAllSenses {
    return entryDao.getEntryById(entryId)
  }

  fun search(term: String): PagingSource<Int, SearchResultEntry> {
    val unicodeCodePoint = Character.codePointAt(term, 0)
    return if (isJapanese(unicodeCodePoint)) {
      val wildcardQuery = String.format("*%s*", term)
      entryDao.searchByJapaneseTerm(wildcardQuery)
    } else {
      entryDao.searchByEnglishTerm(term)
    }
  }

  fun browse(): PagingSource<Int, SearchResultEntry> {
    return entryDao.browse()
  }

  fun getFavorites(): PagingSource<Int, SearchResultEntry> {
    return entryDao.getFavorites()
  }

  fun getByJlptLevel(level: Int): PagingSource<Int, SearchResultEntry> {
    return entryDao.findByJlptLevel(level)
  }

  suspend fun getAllFavorites(): List<EntryWithAllSenses> {
    return entryDao.getAllFavorites()
  }

  suspend fun getAllByJlptLevel(jlptLevel: Int): List<EntryWithAllSenses> {
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
