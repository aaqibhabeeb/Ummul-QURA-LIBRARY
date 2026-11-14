package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titleUrdu: String,
    val titleEnglish: String,
    val authorUrdu: String,
    val authorEnglish: String,
    val category: String, // Tafseer, Hadith, Fiqh, Seerat, History, General Literature
    val categoryUrdu: String,
    val descriptionUrdu: String,
    val descriptionEnglish: String,
    val coverImageRes: Int? = null,
    val coverImageUrl: String? = null,
    val pdfUrl: String? = null,
    val sampleExcerptUrdu: String = "",
    val totalCopies: Int = 1,
    val availableCopies: Int = 1,
    val shelfLocation: String = "ریک A-1",
    val callNumber: String = "UQ-100",
    val publishedYear: String = "1440H / 2019",
    val publisher: String = "مکتبہ دار العلوم",
    val isAvailable: Boolean = true,
    val catalogueNumber: String = "",
    val serialNumber: String = "",
    val alphabeticalSection: String = "",
    val sourcePage: String = "ص 1"
)
