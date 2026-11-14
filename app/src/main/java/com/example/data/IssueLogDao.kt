package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface IssueLogDao {
    @Query("SELECT * FROM issue_logs ORDER BY id DESC")
    fun getAllLogs(): Flow<List<IssueLogEntity>>

    @Query("SELECT * FROM issue_logs WHERE status = 'Issued' ORDER BY id DESC")
    fun getActiveIssuedLogs(): Flow<List<IssueLogEntity>>

    @Query("SELECT * FROM issue_logs WHERE status = 'Returned' ORDER BY id DESC")
    fun getReturnedLogs(): Flow<List<IssueLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: IssueLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<IssueLogEntity>)

    @Update
    suspend fun updateLog(log: IssueLogEntity)

    @Delete
    suspend fun deleteLog(log: IssueLogEntity)

    @Query("SELECT COUNT(*) FROM issue_logs WHERE status = 'Issued'")
    fun getBorrowedBooksCount(): Flow<Int>

    @Query("UPDATE issue_logs SET status = 'Returned', returnDate = :returnDate WHERE id = :id")
    suspend fun markAsReturned(id: Long, returnDate: String)
}
