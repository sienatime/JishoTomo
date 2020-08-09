package net.emojiparty.android.jishotomo.data

import android.os.AsyncTask
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import net.emojiparty.android.jishotomo.JishoTomoApp
import net.emojiparty.android.jishotomo.data.CJKUtil.isJapanese
import net.emojiparty.android.jishotomo.data.models.CrossReferencedEntry
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry
import net.emojiparty.android.jishotomo.data.room.Entry
import net.emojiparty.android.jishotomo.data.room.EntryDao
import net.emojiparty.android.jishotomo.data.room.SenseDao
import java.util.ArrayList
import java.util.HashMap
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

  fun getEntryWithAllSenses(
    entryId: Int,
    lifecycleOwner: LifecycleOwner
  ): MutableLiveData<EntryWithAllSenses> {
    val liveData = MutableLiveData<EntryWithAllSenses>()
    entryDao.getEntryById(entryId)
        .observe(
            lifecycleOwner,
            Observer { entry: EntryWithAllSenses? ->
              entry?.let { setCrossReferences(it, liveData, lifecycleOwner) }
            }
        )
    return liveData
  }

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

  interface OnDataLoaded {
    fun success(entry: SearchResultEntry)
  }

  fun getRandomEntryByJlptLevel(
    level: Int,
    callback: OnDataLoaded
  ) {
    AsyncTask.execute {
      val jlptCount = entryDao.getJlptLevelCount(level)
      val entry = entryDao.randomByJlptLevel(level, randomOffset(jlptCount))
      callback.success(entry)
    }
  }

  private fun randomOffset(count: Int): Int {
    val max = count - 1
    return (Math.random() * max).toInt()
  }

  fun toggleFavorite(entry: Entry) {
    AsyncTask.execute {
      entry.toggleFavorited()
      entryDao.updateEntry(entry)
    }
  }

  fun unfavoriteAll() {
    AsyncTask.execute { entryDao.unfavoriteAll() }
  }

  fun getEntryByKanji(
    kanji: String,
    callback: (entry: Entry) -> Unit
  ) {
    AsyncTask.execute {
      val entry = entryDao.getEntryByKanji(kanji)
      callback(entry)
    }
  }

  private fun setCrossReferences(
    entry: EntryWithAllSenses,
    liveData: MutableLiveData<EntryWithAllSenses>,
    lifecycleOwner: LifecycleOwner
  ) {
    senseDao.getCrossReferencedEntries(
        entry.getEntry()
            .id
    ).observe(
        lifecycleOwner,
        Observer { crossReferencedEntries: List<CrossReferencedEntry> ->
            val hashMap = crossReferenceHash(crossReferencedEntries)
            for (sense in entry.getSenses()) {
              hashMap[sense.getSense().id]?.let {
                sense.setCrossReferences(it)
              }
            }
          liveData.setValue(entry)
        }
    )
  }

  private fun crossReferenceHash(
    senses: List<CrossReferencedEntry>
  ): HashMap<Int, MutableList<CrossReferencedEntry>> {
    val hashMap =
      HashMap<Int, MutableList<CrossReferencedEntry>>()
    for (crossReferencedEntry in senses) {
      val senseId = crossReferencedEntry.senseId
      if (hashMap[senseId] == null) {
        val xrefs =
          ArrayList<CrossReferencedEntry>()
        xrefs.add(crossReferencedEntry)
        hashMap[senseId] = xrefs
      } else {
        hashMap[senseId]!!
            .add(crossReferencedEntry)
      }
    }
    return hashMap
  }
}
