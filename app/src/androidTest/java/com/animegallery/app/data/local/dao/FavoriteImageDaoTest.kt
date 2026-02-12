package com.animegallery.app.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.animegallery.app.data.local.AppDatabase
import com.animegallery.app.data.local.entity.FavoriteImageEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteImageDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var favoriteImageDao: FavoriteImageDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        favoriteImageDao = database.favoriteImageDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertFavorite_andRetrieve_returnsCorrectData() = runTest {
        // Given
        val favoriteImage = FavoriteImageEntity(
            id = "12345",
            thumbnailUrl = "https://example.com/thumb.jpg",
            previewUrl = "https://example.com/preview.jpg",
            fileUrl = "https://example.com/file.jpg",
            width = 1920,
            height = 1080,
            rating = "safe",
            tags = "anime girl school",
            timestamp = System.currentTimeMillis()
        )

        // When
        favoriteImageDao.insertFavorite(favoriteImage)
        val favorites = favoriteImageDao.getAllFavorites().first()

        // Then
        assertEquals(1, favorites.size)
        assertEquals("12345", favorites[0].id)
        assertEquals("https://example.com/thumb.jpg", favorites[0].thumbnailUrl)
    }

    @Test
    fun deleteFavorite_removesFromDatabase() = runTest {
        // Given
        val favoriteImage = FavoriteImageEntity(
            id = "12345",
            thumbnailUrl = "https://example.com/thumb.jpg",
            previewUrl = "https://example.com/preview.jpg",
            fileUrl = "https://example.com/file.jpg",
            width = 1920,
            height = 1080,
            rating = "safe",
            tags = "anime girl",
            timestamp = System.currentTimeMillis()
        )
        favoriteImageDao.insertFavorite(favoriteImage)

        // When
        favoriteImageDao.deleteFavoriteById("12345")
        val favorites = favoriteImageDao.getAllFavorites().first()

        // Then
        assertEquals(0, favorites.size)
    }

    @Test
    fun isFavorite_returnsTrueForExistingFavorite() = runTest {
        // Given
        val favoriteImage = FavoriteImageEntity(
            id = "12345",
            thumbnailUrl = "https://example.com/thumb.jpg",
            previewUrl = "https://example.com/preview.jpg",
            fileUrl = "https://example.com/file.jpg",
            width = 1920,
            height = 1080,
            rating = "safe",
            tags = "anime",
            timestamp = System.currentTimeMillis()
        )
        favoriteImageDao.insertFavorite(favoriteImage)

        // When
        val isFavorite = favoriteImageDao.isFavorite("12345")

        // Then
        assertTrue(isFavorite)
    }

    @Test
    fun isFavorite_returnsFalseForNonExistingFavorite() = runTest {
        // When
        val isFavorite = favoriteImageDao.isFavorite("99999")

        // Then
        assertFalse(isFavorite)
    }

    @Test
    fun getAllFavorites_returnsInDescendingTimestampOrder() = runTest {
        // Given
        val favorite1 = FavoriteImageEntity(
            id = "1",
            thumbnailUrl = "url1",
            previewUrl = "url1",
            fileUrl = "url1",
            width = 100,
            height = 100,
            rating = "safe",
            tags = "tag1",
            timestamp = 1000L
        )
        val favorite2 = FavoriteImageEntity(
            id = "2",
            thumbnailUrl = "url2",
            previewUrl = "url2",
            fileUrl = "url2",
            width = 100,
            height = 100,
            rating = "safe",
            tags = "tag2",
            timestamp = 2000L
        )
        val favorite3 = FavoriteImageEntity(
            id = "3",
            thumbnailUrl = "url3",
            previewUrl = "url3",
            fileUrl = "url3",
            width = 100,
            height = 100,
            rating = "safe",
            tags = "tag3",
            timestamp = 1500L
        )

        // When
        favoriteImageDao.insertFavorite(favorite1)
        favoriteImageDao.insertFavorite(favorite2)
        favoriteImageDao.insertFavorite(favorite3)
        val favorites = favoriteImageDao.getAllFavorites().first()

        // Then
        assertEquals(3, favorites.size)
        assertEquals("2", favorites[0].id) // Most recent
        assertEquals("3", favorites[1].id)
        assertEquals("1", favorites[2].id) // Oldest
    }

    @Test
    fun insertFavorite_withSameId_replacesExisting() = runTest {
        // Given
        val favorite1 = FavoriteImageEntity(
            id = "12345",
            thumbnailUrl = "url1",
            previewUrl = "url1",
            fileUrl = "url1",
            width = 100,
            height = 100,
            rating = "safe",
            tags = "old_tags",
            timestamp = 1000L
        )
        val favorite2 = FavoriteImageEntity(
            id = "12345",
            thumbnailUrl = "url2",
            previewUrl = "url2",
            fileUrl = "url2",
            width = 200,
            height = 200,
            rating = "questionable",
            tags = "new_tags",
            timestamp = 2000L
        )

        // When
        favoriteImageDao.insertFavorite(favorite1)
        favoriteImageDao.insertFavorite(favorite2)
        val favorites = favoriteImageDao.getAllFavorites().first()

        // Then
        assertEquals(1, favorites.size)
        assertEquals("new_tags", favorites[0].tags)
        assertEquals(2000L, favorites[0].timestamp)
    }
}
