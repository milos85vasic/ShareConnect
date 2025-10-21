package com.shareconnect.jdownloaderconnect.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shareconnect.jdownloaderconnect.data.dao.*
import com.shareconnect.jdownloaderconnect.data.model.*

@Database(
    entities = [
        JDownloaderAccount::class,
        DownloadPackage::class,
        DownloadLink::class,
        LinkGrabberPackage::class,
        LinkGrabberLink::class,
        ServerProfile::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(JDownloaderConverters::class)
abstract class JDownloaderDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun downloadDao(): DownloadDao
    abstract fun linkGrabberDao(): LinkGrabberDao
    abstract fun serverDao(): ServerDao
}