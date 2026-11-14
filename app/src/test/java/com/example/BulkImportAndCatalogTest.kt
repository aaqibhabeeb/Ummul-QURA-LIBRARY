package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.BulkImportErrorType
import com.example.data.BulkImportParser
import com.example.data.InitialData
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.LibraryViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.nio.charset.StandardCharsets

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BulkImportAndCatalogTest {

    @Test
    fun `initial catalog preserves existing books and includes all UQL-1001 to UQL-1050`() {
        val books = InitialData.sampleBooks
        assertEquals(92, books.size)

        val uniqueIds = books.map { it.id }.toSet()
        assertEquals(92, uniqueIds.size)

        val catNumbers = books.map { it.catalogueNumber }.toSet()
        for (num in 1001..1050) {
            assertTrue("Missing UQL-$num", catNumbers.contains("UQL-$num"))
        }
    }

    @Test
    fun `sample xlsx roundtrip parses into valid rows`() {
        val xlsxBytes = BulkImportParser.generateSampleXlsxBytes()
        assertTrue(xlsxBytes.isNotEmpty())

        val existing = setOf("uql-1001", "uql-1002")
        val rows = BulkImportParser.parseFileBytes(
            bytes = xlsxBytes,
            fileName = "sample.xlsx",
            existingCatalogueNumbers = existing
        )

        assertEquals(3, rows.size)
        assertTrue(rows.all { it.isValid })
        assertEquals("UQL-1051", rows[0].catalogueNumber)
        assertEquals("سنن الدارمي", rows[0].title)
        assertEquals("س", rows[0].alphabeticalSection)
        assertEquals(5, rows[0].totalCopies)
        assertEquals(4, rows[0].availableCopies)
    }

    @Test
    fun `csv parser detects missing fields, db duplicates, and file duplicates`() {
        val csv = """
            کتاب کا نام,مصنف,فہرست نمبر,سیریل نمبر,الفبائی شعبہ,صفحہ,موضوع,کل نسخے,موجود نسخے
            صحیح البخاری,امام بخاری,UQL-1001,SN-1,ص,1,حدیث,5,5
            نئی کتاب اول,مصنف اول,UQL-2001,SN-2,ن,10,تفسیر,4,3
            نئی کتاب مکرر,مصنف دوم,UQL-2001,SN-3,ن,12,تفسیر,2,2
            ,مصنف سوم,UQL-2002,SN-4,م,15,فقہ,1,1
            کتاب بلا فہرست,مصنف چہارم,,SN-5,ک,20,سیرت,3,3
        """.trimIndent()

        val rows = BulkImportParser.parseFileBytes(
            bytes = csv.toByteArray(StandardCharsets.UTF_8),
            fileName = "books.csv",
            existingCatalogueNumbers = setOf("uql-1001")
        )

        assertEquals(5, rows.size)
        assertEquals(BulkImportErrorType.DUPLICATE_IN_DB, rows[0].errorType)
        assertTrue(rows[1].isValid)
        assertEquals(BulkImportErrorType.DUPLICATE_IN_FILE, rows[2].errorType)
        assertEquals(BulkImportErrorType.MISSING_TITLE, rows[3].errorType)
        assertEquals(BulkImportErrorType.MISSING_CATALOGUE_NUMBER, rows[4].errorType)

        val entities = BulkImportParser.toBookEntities(rows)
        assertEquals(1, entities.size)
        assertEquals("UQL-2001", entities.first().catalogueNumber)
        assertEquals("Tafseer", entities.first().category)
    }

    @Test
    fun `admin login gates admin dashboard and logout clears session`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = LibraryViewModel(app)

        assertFalse(viewModel.isAdminLoggedIn.value)

        // Attempt navigating to admin dashboard while logged out
        viewModel.navigateTo(AppDestination.ADMIN_DASHBOARD)
        assertTrue(viewModel.showAdminLoginDialog.value)
        assertEquals(AppDestination.HOME, viewModel.currentDestination.value)

        // Wrong password
        val failed = viewModel.authenticateAdmin("wrong_pass")
        assertFalse(failed)
        assertNotNull(viewModel.adminLoginError.value)
        assertFalse(viewModel.isAdminLoggedIn.value)

        // Valid password
        val succeeded = viewModel.authenticateAdmin("admin123")
        assertTrue(succeeded)
        assertNull(viewModel.adminLoginError.value)
        assertTrue(viewModel.isAdminLoggedIn.value)
        assertFalse(viewModel.showAdminLoginDialog.value)
        assertEquals(AppDestination.ADMIN_DASHBOARD, viewModel.currentDestination.value)

        // Logout
        viewModel.logoutAdmin()
        assertFalse(viewModel.isAdminLoggedIn.value)
        assertEquals(AppDestination.HOME, viewModel.currentDestination.value)
    }
}
