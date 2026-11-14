package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.BulkImportDialog
import com.example.ui.components.LibraryBottomNavigation
import com.example.ui.components.LibraryTopAppBar
import com.example.ui.screens.AddEditBookScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.BookDetailScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.GuidelinesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.IssueLogScreen
import com.example.ui.screens.PdfReaderScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.LibraryViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LibraryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val isRtl by viewModel.isRtlUrdu.collectAsState()
                val layoutDirection = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    MainLibraryApp(viewModel = viewModel, isRtl = isRtl)
                }
            }
        }
    }
}

@Composable
fun MainLibraryApp(
    viewModel: LibraryViewModel,
    isRtl: Boolean
) {
    val currentDestination by viewModel.currentDestination.collectAsState()
    val totalBooksCount by viewModel.totalBooksCount.collectAsState()
    val availableBooksCount by viewModel.availableBooksCount.collectAsState()
    val borrowedBooksCount by viewModel.borrowedBooksCount.collectAsState()

    val allBooks by viewModel.allBooks.collectAsState()
    val filteredBooks by viewModel.filteredBooks.collectAsState()
    val filteredIssueLogs by viewModel.filteredIssueLogs.collectAsState()
    val activeIssueLogs = filteredIssueLogs.filter { it.status == "Issued" }

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedSection by viewModel.selectedSection.collectAsState()
    val availableSections by viewModel.availableSections.collectAsState()
    val issueLogFilter by viewModel.issueLogFilter.collectAsState()

    val selectedBook by viewModel.selectedBook.collectAsState()
    val bookToEdit by viewModel.bookToEdit.collectAsState()

    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()
    val showAdminLoginDialog by viewModel.showAdminLoginDialog.collectAsState()
    val adminLoginError by viewModel.adminLoginError.collectAsState()
    var showBulkImportDialog by remember { mutableStateOf(false) }

    val feedbackMessage by viewModel.userFeedbackMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedbackMessage()
        }
    }

    if (showAdminLoginDialog) {
        AdminLoginDialog(
            errorMessage = adminLoginError,
            onLogin = { password -> viewModel.authenticateAdmin(password) },
            onDismiss = { viewModel.dismissAdminLoginDialog() },
            isUrdu = isRtl
        )
    }

    if (showBulkImportDialog) {
        BulkImportDialog(
            existingBooks = allBooks,
            onImportBooks = { rows, onComplete ->
                viewModel.importBulkBooks(rows, onComplete)
            },
            onDismiss = { showBulkImportDialog = false },
            isUrdu = isRtl
        )
    }

    val canNavigateBack = currentDestination != AppDestination.HOME

    BackHandler(enabled = canNavigateBack) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentDestination != AppDestination.PDF_READER && currentDestination != AppDestination.ADD_EDIT_BOOK && currentDestination != AppDestination.GUIDELINES) {
                LibraryTopAppBar(
                    titleUrdu = "ام القریٰ لائبریری",
                    titleEnglish = "Ummul Qura Library",
                    isRtl = isRtl,
                    canNavigateBack = canNavigateBack,
                    onNavigateBack = { viewModel.navigateBack() },
                    onToggleLanguage = { viewModel.toggleLanguageDirection() },
                    isAdminLoggedIn = isAdminLoggedIn,
                    onAdminLogout = { viewModel.logoutAdmin() }
                )
            }
        },
        bottomBar = {
            if (currentDestination != AppDestination.PDF_READER && currentDestination != AppDestination.ADD_EDIT_BOOK) {
                LibraryBottomNavigation(
                    currentDestination = currentDestination,
                    onNavigate = { destination -> viewModel.navigateTo(destination) },
                    isUrdu = isRtl
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentDestination,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { destination ->
                when (destination) {
                    AppDestination.HOME -> {
                        HomeScreen(
                            totalBooksCount = totalBooksCount,
                            featuredBooks = filteredBooks,
                            onExploreCatalog = { viewModel.navigateTo(AppDestination.CATALOG) },
                            onBookClick = { book -> viewModel.selectBook(book) },
                            onViewGuidelines = { viewModel.navigateTo(AppDestination.GUIDELINES) },
                            isUrdu = isRtl
                        )
                    }

                    AppDestination.CATALOG -> {
                        CatalogScreen(
                            books = filteredBooks,
                            searchQuery = searchQuery,
                            selectedCategory = selectedCategory,
                            selectedSection = selectedSection,
                            availableSections = availableSections,
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            onCategorySelected = { viewModel.setSelectedCategory(it) },
                            onSectionSelected = { viewModel.setSelectedSection(it) },
                            onResetFilters = { viewModel.resetCatalogFilters() },
                            onBookClick = { book -> viewModel.selectBook(book) },
                            onAddBookClick = { viewModel.startAddBook() },
                            isUrdu = isRtl
                        )
                    }

                    AppDestination.BOOK_DETAIL -> {
                        selectedBook?.let { book ->
                            BookDetailScreen(
                                book = book,
                                onBack = { viewModel.navigateBack() },
                                onOpenPdfReader = { viewModel.openPdfReader(book) },
                                onEditBook = { viewModel.startEditBook(book) },
                                onDeleteBook = { viewModel.deleteBook(book) },
                                onIssueBookConfirm = { name, phone, card, days, notes ->
                                    viewModel.issueBook(book, name, phone, card, days, notes)
                                },
                                isUrdu = isRtl
                            )
                        } ?: run {
                            HomeScreen(
                                totalBooksCount = totalBooksCount,
                                featuredBooks = filteredBooks,
                                onExploreCatalog = { viewModel.navigateTo(AppDestination.CATALOG) },
                                onBookClick = { book -> viewModel.selectBook(book) },
                                onViewGuidelines = { viewModel.navigateTo(AppDestination.GUIDELINES) },
                                isUrdu = isRtl
                            )
                        }
                    }

                    AppDestination.PDF_READER -> {
                        selectedBook?.let { book ->
                            PdfReaderScreen(
                                book = book,
                                onBack = { viewModel.navigateBack() },
                                isUrdu = isRtl
                            )
                        }
                    }

                    AppDestination.ADMIN_DASHBOARD -> {
                        AdminDashboardScreen(
                            totalBooksCount = totalBooksCount,
                            availableBooksCount = availableBooksCount,
                            borrowedBooksCount = borrowedBooksCount,
                            activeIssueLogs = activeIssueLogs,
                            books = allBooks,
                            onAddBookClick = { viewModel.startAddBook() },
                            onBulkImportClick = { showBulkImportDialog = true },
                            onResetDatabaseClick = { viewModel.resetToDefaultBooks() },
                            onLogoutClick = { viewModel.logoutAdmin() },
                            onEditBookClick = { book -> viewModel.startEditBook(book) },
                            onDeleteBookClick = { book -> viewModel.deleteBook(book) },
                            onBookClick = { book -> viewModel.selectBook(book) },
                            onViewIssueLogClick = { viewModel.navigateTo(AppDestination.ISSUE_LOG) },
                            onViewCatalogClick = { viewModel.navigateTo(AppDestination.CATALOG) },
                            onReturnBookClick = { log -> viewModel.returnBook(log) },
                            isUrdu = isRtl
                        )
                    }

                    AppDestination.ADD_EDIT_BOOK -> {
                        AddEditBookScreen(
                            bookToEdit = bookToEdit,
                            onSave = { titleUrd, titleEng, authUrd, authEng, cat, catUrd, descUrd, descEng, copies, shelf, call, pdf, catNum, serialNum, section, page, availCopies ->
                                viewModel.saveBook(
                                    titleUrdu = titleUrd,
                                    titleEnglish = titleEng,
                                    authorUrdu = authUrd,
                                    authorEnglish = authEng,
                                    category = cat,
                                    categoryUrdu = catUrd,
                                    descriptionUrdu = descUrd,
                                    descriptionEnglish = descEng,
                                    totalCopies = copies,
                                    shelfLocation = shelf,
                                    callNumber = call,
                                    pdfUrl = pdf,
                                    catalogueNumber = catNum,
                                    serialNumber = serialNum,
                                    alphabeticalSection = section,
                                    sourcePage = page,
                                    availableCopiesOverride = availCopies
                                )
                            },
                            onCancel = { viewModel.navigateBack() },
                            isUrdu = isRtl
                        )
                    }

                    AppDestination.ISSUE_LOG -> {
                        IssueLogScreen(
                            issueLogs = filteredIssueLogs,
                            selectedFilter = issueLogFilter,
                            onFilterChange = { viewModel.setIssueLogFilter(it) },
                            onReturnBook = { log -> viewModel.returnBook(log) },
                            isUrdu = isRtl
                        )
                    }

                    AppDestination.GUIDELINES -> {
                        GuidelinesScreen(
                            onBack = { viewModel.navigateBack() },
                            isUrdu = isRtl
                        )
                    }
                }
            }
        }
    }
}
