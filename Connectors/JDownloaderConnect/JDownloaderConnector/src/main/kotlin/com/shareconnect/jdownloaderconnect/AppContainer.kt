package com.shareconnect.jdownloaderconnect

import android.content.Context
import com.shareconnect.jdownloaderconnect.data.dao.*
import com.shareconnect.jdownloaderconnect.data.repository.JDownloaderRepository
import com.shareconnect.jdownloaderconnect.data.repository.ServerRepository
import com.shareconnect.jdownloaderconnect.network.api.MyJDownloaderApi
import com.shareconnect.jdownloaderconnect.presentation.viewmodel.AccountViewModel
import com.shareconnect.jdownloaderconnect.presentation.viewmodel.DownloadsViewModel
import com.shareconnect.jdownloaderconnect.presentation.viewmodel.LinkGrabberViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {
    
    // Database
    private val database = JDownloaderConnectApplication.database
    
    // DAOs
    private val accountDao: AccountDao = database.accountDao()
    private val downloadDao: DownloadDao = database.downloadDao()
    private val linkGrabberDao: LinkGrabberDao = database.linkGrabberDao()
    private val serverDao: ServerDao = database.serverDao()
    
    // Network
    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }
    
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.jdownloader.org")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    private val myJDownloaderApi: MyJDownloaderApi by lazy {
        retrofit.create(MyJDownloaderApi::class.java)
    }
    
    // Repositories
    val jDownloaderRepository: JDownloaderRepository by lazy {
        JDownloaderRepository(accountDao, downloadDao, linkGrabberDao, myJDownloaderApi)
    }
    
    val serverRepository: ServerRepository by lazy {
        ServerRepository(serverDao)
    }
    
    // ViewModels
    val accountViewModel: AccountViewModel by lazy {
        AccountViewModel(jDownloaderRepository)
    }
    
    val downloadsViewModel: DownloadsViewModel by lazy {
        DownloadsViewModel(jDownloaderRepository)
    }
    
    val linkGrabberViewModel: LinkGrabberViewModel by lazy {
        LinkGrabberViewModel(jDownloaderRepository)
    }
}