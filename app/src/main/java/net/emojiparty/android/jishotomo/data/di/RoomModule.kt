package net.emojiparty.android.jishotomo.data.di

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import net.emojiparty.android.jishotomo.Environment.Companion.isTest
import net.emojiparty.android.jishotomo.data.room.AppDatabase
import net.emojiparty.android.jishotomo.data.room.EntryDao
import net.emojiparty.android.jishotomo.data.room.SenseDao
import javax.inject.Singleton

// https://medium.com/@marco_cattaneo/integrate-dagger-2-with-room-persistence-library-in-few-lines-abf48328eaeb
@Module
class RoomModule @Singleton constructor(application: Application?) {
  private val db: AppDatabase
  private val databaseFileName = "jishotomo.db"

  // this is needed in order to provide the DAOs
  @Singleton @Provides fun providesRoomDatabase(): AppDatabase {
    return db
  }

  @Singleton @Provides fun providesEntryDao(db: AppDatabase): EntryDao {
    return db.entryDao()
  }

  @Singleton @Provides fun providesSenseDao(db: AppDatabase): SenseDao {
    return db.senseDao()
  }

  private fun databaseName(): String {
    return if (isTest) {
      "jishotomo_test.db"
    } else {
      databaseFileName
    }
  }

  init {
    db = Room.databaseBuilder(application!!, AppDatabase::class.java, databaseName())
        .createFromAsset("databases/$databaseFileName")
        .addMigrations(object : Migration(1, 2) {
          override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE entries ADD COLUMN favorited INTEGER")
          }
        })
        .addMigrations(object : Migration(2, 3) {
          override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE VIRTUAL TABLE IF NOT EXISTS `entriesFts` USING FTS4(`primary_kanji`, `primary_reading`, `other_kanji`, `other_readings`, content=`entries`)"
            )
            database.execSQL(
                "CREATE VIRTUAL TABLE IF NOT EXISTS `sensesFts` USING FTS4(`glosses`, content=`senses`)"
            )
            database.execSQL("INSERT INTO entriesFts(entriesFts) VALUES ('rebuild')")
            database.execSQL("INSERT INTO sensesFts(sensesFts) VALUES ('rebuild')")
          }
        })
        .build()
  }
}