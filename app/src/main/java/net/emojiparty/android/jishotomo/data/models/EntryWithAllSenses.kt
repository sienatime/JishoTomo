package net.emojiparty.android.jishotomo.data.models

import android.content.Context
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import net.emojiparty.android.jishotomo.JishoTomoApp
import net.emojiparty.android.jishotomo.analytics.AnalyticsLogger
import net.emojiparty.android.jishotomo.data.AppRepository
import net.emojiparty.android.jishotomo.data.SemicolonSplit
import net.emojiparty.android.jishotomo.data.room.Entry
import net.emojiparty.android.jishotomo.data.room.Sense

data class EntryWithAllSenses(
  @Embedded var entry: Entry,
  @Relation(parentColumn = "id", entityColumn = "entry_id", entity = Sense::class)
  var senses: List<SenseWithCrossReferences> = emptyList()
) {
  @Ignore
  private var analyticsLogger: AnalyticsLogger? = null

  @delegate:Ignore
  private val appRepository: AppRepository by lazy { AppRepository() }

  val kanjiOrReading: String
    get() = if (hasKanji()) entry.primaryKanji!! else entry.primaryReading

  val reading: String?
    get() = if (hasKanji()) entry.primaryReading else null

  fun hasKanji(): Boolean {
    return entry.primaryKanji != null
  }

  val alternateKanji: String
    get() = SemicolonSplit.splitAndJoin(entry.otherKanji)

  val alternateReadings: String
    get() = SemicolonSplit.splitAndJoin(entry.otherReadings)

  fun hasAlternateKanji(): Boolean {
    return entry.otherKanji != null
  }

  fun hasAlternateReadings(): Boolean {
    return entry.otherReadings != null
  }

  // lazily instantiate AppRepository and AnalyticsLogger so that we don't have to
  // always instantiate unless Favorite button is clicked,
  // and if it is clicked again, we already have the references.
  // TODO should probably move this
  fun toggleFavorite(context: Context) {
    appRepository.toggleFavorite(entry)
    getAnalyticsLogger(context).logToggleFavoriteEvent(
        entry.id,
        kanjiOrReading,
        !entry.isFavorited()
    )
  }

  private fun getAnalyticsLogger(context: Context): AnalyticsLogger {
    if (analyticsLogger == null) {
      val appContext = context.applicationContext as JishoTomoApp
      analyticsLogger = appContext.analyticsLogger
    }
    return analyticsLogger!!
  }
}
