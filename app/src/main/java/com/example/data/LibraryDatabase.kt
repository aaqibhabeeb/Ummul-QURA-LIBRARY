package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [BookEntity::class, IssueLogEntity::class],
    version = 6,
    exportSchema = false
)
abstract class LibraryDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun issueLogDao(): IssueLogDao

    companion object {
        @Volatile
        private var INSTANCE: LibraryDatabase? = null

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("UPDATE books SET coverImageRes = NULL WHERE id <= 50")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("UPDATE books SET shelfLocation = REPLACE(shelfLocation, 'الرف', 'ریک')")
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE books ADD COLUMN catalogueNumber TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE books ADD COLUMN serialNumber TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE books ADD COLUMN alphabeticalSection TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE books ADD COLUMN sourcePage TEXT NOT NULL DEFAULT 'ص 1'")
            }
        }

        fun getDatabase(context: Context): LibraryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LibraryDatabase::class.java,
                    "ummul_qura_library.db"
                )
                .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Prepopulate database with authentic register books
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getDatabase(context)
                            database.bookDao().insertBooks(InitialData.sampleBooks)
                            database.issueLogDao().insertLogs(InitialData.sampleIssueLogs)
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
