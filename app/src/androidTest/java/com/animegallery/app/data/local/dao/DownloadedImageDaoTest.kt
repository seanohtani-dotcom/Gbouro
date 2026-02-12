package com.animegallery.app.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.animegallery.app.data.local.AppDatabase
import com.animegallery.app.data.local.entity.DownloadedImageEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DownloadedImageDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var downloadedImageDao: DownloadedImageDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        downloadedImageDao = database.downloadedImageDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertDownload_andRetrieve_returnsCorrectData() = runTest {
        // Given
        val downloadedImage = DownloadedImageEntity(
            id = "12345",
            localFilePath = "/storage/emulated/0/Pictures/image.jpg",
            timestamp = System.currentTimeMillis()
        )

        // When
        downloadedImageDao.insertDownload(downloadedImage)
        val retrieved = downloadedImageDao.getDownloadedImage("12345")

        // Then
        assertNotNull(retrieved)
        assertEquals("12345", retrieved?.id)
        assertEquals("/storage/emulated/0/Pictures/image.jpg", retrieved?.localFilePath)
    }

    @Test
    fun isDownloaded_returnsTrueForExistingDownload() = runTest {
        // Given
        val downloadedImage = DownloadedImageEntity(
            id = "12345",
            localFilePath = "/storage/path/image.jpg",
            timestamp = System.currentTimeMillis()
        )
        downloadedImageDao.insertDownload(downloadedImage)

        // When
        val isDownloaded = downloadedImageDao.isDownloaded("12345")

        // Then
        assertTrue(isDownloaded)
    }

    @Test
    fun isDownloaded_returnsFalseForNonExistingDownload() = runTest {
        // When
        val isDownloaded = downloadedImageDao.isDownloaded("99999")

        // Then
        assertFalse(isDownloaded)
    }

    @Test
    fun getDownloadedImage_returnsNullForNonExisting() = runTest {
        // When
        val retrieved = downloadedImageDao.getDownloadedImage("99999")

        // Then
        assertNull(retrieved)
    }

    @Test
    fun insertDownload_withSameId_replacesExisting() = runTest {
        // Given
        val download1 = DownloadedImageEntity(
            id = "12345",
            localFilePath = "/path/old.jpg",
            timestamp = 1000L
        )
        val download2 = DownloadedImageEntity(
            id = "12345",
            localFilePath = "/path/new.jpg",
            timestamp = 2000L
        )

        // When
        downloadedImageDao.insertDownload(download1)
        downloadedImageDao.insertDownload(download2)
        val retrieved = downloadedImageDao.getDownloadedImage("12345")

        // Then
        assertNotNull(retrieved)
        assertEquals("/path/new.jpg", retrieved?.localFilePath)
        assertEquals(2000L, retrieved?.timestamp)
    }
}
