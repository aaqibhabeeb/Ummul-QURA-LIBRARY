package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "issue_logs")
data class IssueLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long,
    val bookTitle: String,
    val bookTitleUrdu: String,
    val memberName: String,
    val memberPhone: String,
    val memberCardNo: String = "",
    val issueDate: String,
    val dueDate: String,
    val returnDate: String? = null,
    val status: String = "Issued", // "Issued", "Returned"
    val notes: String = ""
)
