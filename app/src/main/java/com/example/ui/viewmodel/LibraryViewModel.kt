package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AdditionalCatalogData
import com.example.data.BookEntity
import com.example.data.BulkImportParser
import com.example.data.BulkImportRow
import com.example.data.IssueLogEntity
import com.example.data.LibraryDatabase
import com.example.data.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class AppDestination {
    HOME,
    CATALOG,
    BOOK_DETAIL,
    PDF_READER,
    ADMIN_DASHBOARD,
    ADD_EDIT_BOOK,
    ISSUE_LOG,
    GUIDELINES
}

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LibraryRepository
    private val prefs = application.getSharedPreferences("uql_library_prefs", Context.MODE_PRIVATE)

    init {
        val database = LibraryDatabase.getDatabase(application)
        repository = LibraryRepository(database.bookDao(), database.issueLogDao())
        val hasMerged = prefs.getBoolean("seeded_gemini_books_v6", false)
        viewModelScope.launch {
            repository.ensureInitialDataLoaded(
                hasMergedGeminiCatalog = hasMerged,
                markMerged = { prefs.edit().putBoolean("seeded_gemini_books_v6", true).apply() }
            )
        }
    }

    // Navigation and UI state
    private val _currentDestination = MutableStateFlow(AppDestination.HOME)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    private val _navigationStack = mutableListOf<AppDestination>(AppDestination.HOME)

    private val _selectedBook = MutableStateFlow<BookEntity?>(null)
    val selectedBook: StateFlow<BookEntity?> = _selectedBook.asStateFlow()

    private val _bookToEdit = MutableStateFlow<BookEntity?>(null)
    val bookToEdit: StateFlow<BookEntity?> = _bookToEdit.asStateFlow()

    private val _isRtlUrdu = MutableStateFlow(prefs.getBoolean("is_rtl_urdu", true))
    val isRtlUrdu: StateFlow<Boolean> = _isRtlUrdu.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedSection = MutableStateFlow("All")
    val selectedSection: StateFlow<String> = _selectedSection.asStateFlow()

    private val _userFeedbackMessage = MutableStateFlow<String?>(null)
    val userFeedbackMessage: StateFlow<String?> = _userFeedbackMessage.asStateFlow()

    // Admin authentication state
    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    private val _showAdminLoginDialog = MutableStateFlow(false)
    val showAdminLoginDialog: StateFlow<Boolean> = _showAdminLoginDialog.asStateFlow()

    private val _adminLoginError = MutableStateFlow<String?>(null)
    val adminLoginError: StateFlow<String?> = _adminLoginError.asStateFlow()

    private var pendingAdminDestination: AppDestination? = null

    // Issue Log filter
    private val _issueLogFilter = MutableStateFlow("All") // "All", "Issued", "Returned"
    val issueLogFilter: StateFlow<String> = _issueLogFilter.asStateFlow()

    // All books stream
    val allBooks: StateFlow<List<BookEntity>> = repository.allBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamic alphabetical sections from DB
    val availableSections: StateFlow<List<String>> = repository.allBooks
        .map { books ->
            books.map {
                it.alphabeticalSection.ifBlank { AdditionalCatalogData.deriveSectionLetter(it.titleUrdu) }
            }
                .filter { it.isNotBlank() }
                .distinct()
                .sorted()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard metrics
    val totalBooksCount: StateFlow<Int> = repository.totalBooksCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val availableBooksCount: StateFlow<Int> = repository.availableBooksCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val borrowedBooksCount: StateFlow<Int> = repository.borrowedBooksCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Filtered books stream (combines search, category, and alphabetical section)
    val filteredBooks: StateFlow<List<BookEntity>> = combine(
        repository.allBooks,
        _searchQuery,
        _selectedCategory,
        _selectedSection
    ) { books, query, category, section ->
        books.filter { book ->
            val matchesCategory = (category == "All" || category == "تمام") ||
                book.category.equals(category, ignoreCase = true) ||
                book.categoryUrdu.equals(category, ignoreCase = true)

            val bookSection = book.alphabeticalSection.ifBlank {
                AdditionalCatalogData.deriveSectionLetter(book.titleUrdu)
            }
            val matchesSection = section == "All" || bookSection == section

            val matchesQuery = query.isBlank() ||
                book.titleUrdu.contains(query, ignoreCase = true) ||
                book.titleEnglish.contains(query, ignoreCase = true) ||
                book.authorUrdu.contains(query, ignoreCase = true) ||
                book.authorEnglish.contains(query, ignoreCase = true) ||
                book.callNumber.contains(query, ignoreCase = true) ||
                book.catalogueNumber.contains(query, ignoreCase = true) ||
                book.serialNumber.contains(query, ignoreCase = true) ||
                book.shelfLocation.contains(query, ignoreCase = true)

            matchesCategory && matchesSection && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered issue logs stream
    val filteredIssueLogs: StateFlow<List<IssueLogEntity>> = combine(
        repository.allIssueLogs,
        _issueLogFilter
    ) { logs, filter ->
        when (filter) {
            "Issued", "جاری شدہ" -> logs.filter { it.status == "Issued" }
            "Returned", "واپس شدہ" -> logs.filter { it.status == "Returned" }
            else -> logs
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun navigateTo(destination: AppDestination) {
        if (destination == AppDestination.ADMIN_DASHBOARD && !_isAdminLoggedIn.value) {
            pendingAdminDestination = AppDestination.ADMIN_DASHBOARD
            _adminLoginError.value = null
            _showAdminLoginDialog.value = true
            return
        }
        if (_currentDestination.value != destination) {
            _navigationStack.add(destination)
            _currentDestination.value = destination
        }
    }

    fun navigateBack(): Boolean {
        if (_navigationStack.size > 1) {
            _navigationStack.removeAt(_navigationStack.size - 1)
            val prev = _navigationStack.last()
            if (prev == AppDestination.ADMIN_DASHBOARD && !_isAdminLoggedIn.value) {
                _navigationStack.clear()
                _navigationStack.add(AppDestination.HOME)
                _currentDestination.value = AppDestination.HOME
                return true
            }
            _currentDestination.value = prev
            return true
        }
        return false
    }

    fun authenticateAdmin(password: String): Boolean {
        val trimmed = password.trim()
        return if (trimmed == "admin123" || trimmed == "admin") {
            _isAdminLoggedIn.value = true
            _adminLoginError.value = null
            _showAdminLoginDialog.value = false
            val target = pendingAdminDestination ?: AppDestination.ADMIN_DASHBOARD
            pendingAdminDestination = null
            if (_currentDestination.value != target) {
                _navigationStack.add(target)
                _currentDestination.value = target
            }
            _userFeedbackMessage.value = if (_isRtlUrdu.value) {
                "ایڈمن پینل میں خوش آمدید!"
            } else {
                "Signed in to Admin Dashboard!"
            }
            true
        } else {
            _adminLoginError.value = if (_isRtlUrdu.value) {
                "غلط پاس ورڈ! برائے مہربانی دوبارہ کوشش کریں۔ (ڈیفالٹ: admin123)"
            } else {
                "Invalid password! Please try again. (default: admin123)"
            }
            false
        }
    }

    fun dismissAdminLoginDialog() {
        _showAdminLoginDialog.value = false
        _adminLoginError.value = null
        pendingAdminDestination = null
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
        _navigationStack.removeAll {
            it == AppDestination.ADMIN_DASHBOARD || it == AppDestination.ADD_EDIT_BOOK
        }
        if (_navigationStack.isEmpty()) {
            _navigationStack.add(AppDestination.HOME)
        }
        _currentDestination.value = _navigationStack.last()
        _userFeedbackMessage.value = if (_isRtlUrdu.value) {
            "ایڈمن لاگ آؤٹ مکمل ہو گیا۔"
        } else {
            "Logged out from Admin Dashboard."
        }
    }

    fun toggleLanguageDirection() {
        val next = !_isRtlUrdu.value
        _isRtlUrdu.value = next
        prefs.edit().putBoolean("is_rtl_urdu", next).apply()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSelectedSection(section: String) {
        _selectedSection.value = section
    }

    fun resetCatalogFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = "All"
        _selectedSection.value = "All"
    }

    fun setIssueLogFilter(filter: String) {
        _issueLogFilter.value = filter
    }

    fun selectBook(book: BookEntity) {
        _selectedBook.value = book
        navigateTo(AppDestination.BOOK_DETAIL)
    }

    fun openPdfReader(book: BookEntity) {
        _selectedBook.value = book
        navigateTo(AppDestination.PDF_READER)
    }

    fun startAddBook() {
        _bookToEdit.value = null
        navigateTo(AppDestination.ADD_EDIT_BOOK)
    }

    fun startEditBook(book: BookEntity) {
        _bookToEdit.value = book
        navigateTo(AppDestination.ADD_EDIT_BOOK)
    }

    fun saveBook(
        titleUrdu: String,
        titleEnglish: String,
        authorUrdu: String,
        authorEnglish: String,
        category: String,
        categoryUrdu: String,
        descriptionUrdu: String,
        descriptionEnglish: String,
        totalCopies: Int,
        shelfLocation: String,
        callNumber: String,
        pdfUrl: String?,
        catalogueNumber: String = "",
        serialNumber: String = "",
        alphabeticalSection: String = "",
        sourcePage: String = "ص 1",
        availableCopiesOverride: Int? = null
    ) {
        viewModelScope.launch {
            val existing = _bookToEdit.value
            val resolvedCatNum = catalogueNumber.trim().ifBlank { callNumber.trim() }
            val resolvedSection = alphabeticalSection.trim().ifBlank {
                AdditionalCatalogData.deriveSectionLetter(titleUrdu)
            }
            val resolvedSerial = serialNumber.trim().ifBlank {
                existing?.serialNumber?.ifBlank { "SN-${System.currentTimeMillis() % 100000}" }
                    ?: "SN-${System.currentTimeMillis() % 100000}"
            }
            val resolvedPage = sourcePage.trim().ifBlank { "ص 1" }

            val book = if (existing != null) {
                val computedAvail = availableCopiesOverride?.coerceIn(0, totalCopies)
                    ?: (existing.availableCopies + (totalCopies - existing.totalCopies)).coerceIn(0, totalCopies)
                existing.copy(
                    titleUrdu = titleUrdu,
                    titleEnglish = titleEnglish,
                    authorUrdu = authorUrdu,
                    authorEnglish = authorEnglish,
                    category = category,
                    categoryUrdu = categoryUrdu,
                    descriptionUrdu = descriptionUrdu,
                    descriptionEnglish = descriptionEnglish,
                    totalCopies = totalCopies,
                    availableCopies = computedAvail,
                    shelfLocation = shelfLocation,
                    callNumber = callNumber,
                    pdfUrl = pdfUrl,
                    isAvailable = computedAvail > 0,
                    catalogueNumber = resolvedCatNum,
                    serialNumber = resolvedSerial,
                    alphabeticalSection = resolvedSection,
                    sourcePage = resolvedPage
                )
            } else {
                val computedAvail = availableCopiesOverride?.coerceIn(0, totalCopies) ?: totalCopies
                BookEntity(
                    titleUrdu = titleUrdu,
                    titleEnglish = titleEnglish,
                    authorUrdu = authorUrdu,
                    authorEnglish = authorEnglish,
                    category = category,
                    categoryUrdu = categoryUrdu,
                    descriptionUrdu = descriptionUrdu,
                    descriptionEnglish = descriptionEnglish,
                    totalCopies = totalCopies,
                    availableCopies = computedAvail,
                    shelfLocation = shelfLocation,
                    callNumber = callNumber,
                    pdfUrl = pdfUrl,
                    isAvailable = computedAvail > 0,
                    catalogueNumber = resolvedCatNum,
                    serialNumber = resolvedSerial,
                    alphabeticalSection = resolvedSection,
                    sourcePage = resolvedPage
                )
            }
            repository.saveBook(book)
            val isUrdu = _isRtlUrdu.value
            _userFeedbackMessage.value = if (existing != null) {
                if (isUrdu) "کتاب کامیابی سے اپ ڈیٹ ہو گئی!" else "Book updated successfully!"
            } else {
                if (isUrdu) "نئی کتاب کامیابی سے شامل کر دی گئی!" else "New book added successfully!"
            }
            navigateBack()
        }
    }

    fun deleteBook(book: BookEntity) {
        viewModelScope.launch {
            repository.deleteBook(book)
            _userFeedbackMessage.value = if (_isRtlUrdu.value) {
                "کتاب کامیابی سے حذف کر دی گئی"
            } else {
                "Book deleted successfully"
            }
            if (_currentDestination.value == AppDestination.BOOK_DETAIL) {
                navigateBack()
            }
        }
    }

    fun importBulkBooks(rows: List<BulkImportRow>, onComplete: (savedCount: Int, skippedCount: Int) -> Unit) {
        viewModelScope.launch {
            val validRows = rows.filter { it.isValid }
            val skippedCount = rows.size - validRows.size
            val entities = BulkImportParser.toBookEntities(validRows)
            val savedCount = repository.saveBulkBooks(entities)
            _userFeedbackMessage.value = if (_isRtlUrdu.value) {
                "کامیابی! $savedCount کتب لائبریری ڈیٹابیس میں محفوظ کر دی گئیں!"
            } else {
                "Success! $savedCount imported books saved to database!"
            }
            onComplete(savedCount, skippedCount)
        }
    }

    fun resetToDefaultBooks() {
        viewModelScope.launch {
            repository.resetToDefaultBooks()
            prefs.edit().putBoolean("seeded_gemini_books_v6", true).apply()
            _userFeedbackMessage.value = if (_isRtlUrdu.value) {
                "ڈیٹابیس اصل ${com.example.data.InitialData.sampleBooks.size} کتب پر ری سیٹ کر دیا گیا!"
            } else {
                "Database reset to original ${com.example.data.InitialData.sampleBooks.size} default books!"
            }
        }
    }

    fun issueBook(
        book: BookEntity,
        memberName: String,
        memberPhone: String,
        memberCardNo: String,
        dueDays: Int = 14,
        notes: String
    ) {
        viewModelScope.launch {
            val isUrdu = _isRtlUrdu.value
            if (book.availableCopies <= 0) {
                _userFeedbackMessage.value = if (isUrdu) {
                    "معذرت! اس کتاب کی تمام کاپیاں جاری ہو چکی ہیں۔"
                } else {
                    "Sorry! All copies of this book are currently borrowed."
                }
                return@launch
            }

            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, dueDays)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dueDateStr = dateFormat.format(cal.time)

            repository.issueBook(
                bookId = book.id,
                bookTitle = book.titleEnglish,
                bookTitleUrdu = book.titleUrdu,
                memberName = memberName,
                memberPhone = memberPhone,
                memberCardNo = memberCardNo,
                dueDate = dueDateStr,
                notes = notes
            )
            _userFeedbackMessage.value = if (isUrdu) {
                "کتاب '${book.titleUrdu}' کامیابی سے جاری کر دی گئی!"
            } else {
                "Book '${book.titleEnglish}' issued successfully!"
            }
        }
    }

    fun returnBook(log: IssueLogEntity) {
        viewModelScope.launch {
            repository.returnBook(log)
            _userFeedbackMessage.value = if (_isRtlUrdu.value) {
                "کتاب '${log.bookTitleUrdu}' واپس موصول ہو گئی!"
            } else {
                "Book '${log.bookTitle}' marked as returned!"
            }
        }
    }

    fun clearFeedbackMessage() {
        _userFeedbackMessage.value = null
    }
}
