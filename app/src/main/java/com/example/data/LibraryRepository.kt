package com.example.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LibraryRepository(
    private val bookDao: BookDao,
    private val issueLogDao: IssueLogDao
) {
    val allBooks: Flow<List<BookEntity>> = bookDao.getAllBooks()
    val allIssueLogs: Flow<List<IssueLogEntity>> = issueLogDao.getAllLogs()
    val activeIssuedLogs: Flow<List<IssueLogEntity>> = issueLogDao.getActiveIssuedLogs()
    val returnedLogs: Flow<List<IssueLogEntity>> = issueLogDao.getReturnedLogs()

    val totalBooksCount: Flow<Int> = bookDao.getTotalBooksCount()
    val availableBooksCount: Flow<Int> = bookDao.getAvailableBooksCount()
    val borrowedBooksCount: Flow<Int> = issueLogDao.getBorrowedBooksCount()

    fun getBookById(id: Long): Flow<BookEntity?> = bookDao.getBookById(id)

    fun searchBooks(query: String): Flow<List<BookEntity>> {
        return if (query.isBlank()) {
            bookDao.getAllBooks()
        } else {
            bookDao.searchBooks(query.trim())
        }
    }

    fun getBooksByCategory(category: String): Flow<List<BookEntity>> {
        return if (category == "All" || category == "تمام") {
            bookDao.getAllBooks()
        } else {
            bookDao.getBooksByCategory(category)
        }
    }

    suspend fun saveBook(book: BookEntity): Long {
        return if (book.id == 0L) {
            bookDao.insertBook(book)
        } else {
            bookDao.updateBook(book)
            book.id
        }
    }

    suspend fun deleteBook(book: BookEntity) {
        bookDao.deleteBook(book)
    }

    suspend fun issueBook(
        bookId: Long,
        bookTitle: String,
        bookTitleUrdu: String,
        memberName: String,
        memberPhone: String,
        memberCardNo: String,
        dueDate: String,
        notes: String
    ): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())

        val log = IssueLogEntity(
            bookId = bookId,
            bookTitle = bookTitle,
            bookTitleUrdu = bookTitleUrdu,
            memberName = memberName,
            memberPhone = memberPhone,
            memberCardNo = memberCardNo,
            issueDate = today,
            dueDate = dueDate,
            returnDate = null,
            status = "Issued",
            notes = notes
        )
        val id = issueLogDao.insertLog(log)
        // Decrement available copies by 1
        bookDao.updateAvailableCopies(bookId, -1)
        return id
    }

    suspend fun returnBook(log: IssueLogEntity) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())

        issueLogDao.markAsReturned(log.id, today)
        // Increment available copies by 1
        bookDao.updateAvailableCopies(log.bookId, +1)
    }

    suspend fun deleteIssueLog(log: IssueLogEntity) {
        issueLogDao.deleteLog(log)
    }

    suspend fun saveBulkBooks(books: List<BookEntity>): Int {
        if (books.isEmpty()) return 0
        bookDao.insertBooks(books)
        return books.size
    }

    suspend fun resetToDefaultBooks() {
        bookDao.deleteAllBooks()
        bookDao.insertBooks(InitialData.sampleBooks)
    }

    suspend fun ensureInitialDataLoaded(hasMergedGeminiCatalog: Boolean = false, markMerged: () -> Unit = {}) {
        val existingBooks = bookDao.getAllBooksDirect()
        if (existingBooks.isEmpty()) {
            bookDao.insertBooks(InitialData.sampleBooks)
            markMerged()
            return
        }

        // Backfill newly added catalogue columns on existing rows without overwriting user changes
        val sampleById = InitialData.sampleBooks.associateBy { it.id }
        existingBooks.forEach { existing ->
            if (existing.catalogueNumber.isBlank() || existing.alphabeticalSection.isBlank()) {
                val seed = sampleById[existing.id]
                val updated = existing.copy(
                    catalogueNumber = existing.catalogueNumber.ifBlank { seed?.catalogueNumber ?: existing.callNumber },
                    serialNumber = existing.serialNumber.ifBlank { seed?.serialNumber ?: "SN-REG-${existing.id.toString().padStart(3, '0')}" },
                    alphabeticalSection = existing.alphabeticalSection.ifBlank {
                        seed?.alphabeticalSection ?: AdditionalCatalogData.deriveSectionLetter(existing.titleUrdu)
                    },
                    sourcePage = existing.sourcePage.ifBlank { seed?.sourcePage ?: "ص 1" }
                )
                bookDao.updateBook(updated)
            }
        }

        // One-time non-destructive merge of missing Gemini classical books (IDs 51..92)
        if (!hasMergedGeminiCatalog) {
            val existingCatNums = existingBooks
                .map { it.catalogueNumber.ifBlank { it.callNumber }.trim().lowercase() }
                .filter { it.isNotEmpty() }
                .toSet()
            val existingIds = existingBooks.map { it.id }.toSet()

            val missingBooks = InitialData.sampleBooks.filter { seed ->
                !existingIds.contains(seed.id) &&
                    !existingCatNums.contains(seed.catalogueNumber.trim().lowercase())
            }
            if (missingBooks.isNotEmpty()) {
                bookDao.insertBooksIgnore(missingBooks)
            }
            markMerged()
        }
    }
}
