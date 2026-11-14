package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY id DESC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :id LIMIT 1")
    fun getBookById(id: Long): Flow<BookEntity?>

    @Query("""
        SELECT * FROM books 
        WHERE titleUrdu LIKE '%' || :query || '%' 
           OR titleEnglish LIKE '%' || :query || '%' 
           OR authorUrdu LIKE '%' || :query || '%' 
           OR authorEnglish LIKE '%' || :query || '%'
           OR categoryUrdu LIKE '%' || :query || '%'
           OR category LIKE '%' || :query || '%'
           OR catalogueNumber LIKE '%' || :query || '%'
           OR serialNumber LIKE '%' || :query || '%'
           OR callNumber LIKE '%' || :query || '%'
           OR alphabeticalSection LIKE '%' || :query || '%'
        ORDER BY id DESC
    """)
    fun searchBooks(query: String): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE category = :category ORDER BY id DESC")
    fun getBooksByCategory(category: String): Flow<List<BookEntity>>

    @Query("SELECT * FROM books ORDER BY id ASC")
    suspend fun getAllBooksDirect(): List<BookEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBooksIgnore(books: List<BookEntity>)

    @Update
    suspend fun updateBook(book: BookEntity)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("DELETE FROM books")
    suspend fun deleteAllBooks()

    @Query("SELECT COUNT(*) FROM books")
    fun getTotalBooksCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM books")
    suspend fun getDirectCount(): Int

    @Query("SELECT COUNT(*) FROM books WHERE availableCopies > 0")
    fun getAvailableBooksCount(): Flow<Int>

    @Query("UPDATE books SET availableCopies = availableCopies + :delta, isAvailable = CASE WHEN (availableCopies + :delta) > 0 THEN 1 ELSE 0 END WHERE id = :bookId")
    suspend fun updateAvailableCopies(bookId: Long, delta: Int)
}
