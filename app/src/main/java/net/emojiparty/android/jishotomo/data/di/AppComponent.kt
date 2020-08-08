package net.emojiparty.android.jishotomo.data.di

import dagger.Component
import net.emojiparty.android.jishotomo.data.AppRepository
import net.emojiparty.android.jishotomo.data.room.EntryDao
import net.emojiparty.android.jishotomo.data.room.SenseDao
import javax.inject.Singleton

// https://medium.com/@marco_cattaneo/integrate-dagger-2-with-room-persistence-library-in-few-lines-abf48328eaeb
@Singleton
@Component(dependencies = [], modules = [RoomModule::class])
interface AppComponent {
  fun inject(appRepository: AppRepository?)
  fun entryDao(): EntryDao?
  fun senseDao(): SenseDao?
}
