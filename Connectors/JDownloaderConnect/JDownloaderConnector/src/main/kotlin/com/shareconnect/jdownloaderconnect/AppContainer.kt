/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.shareconnect.jdownloaderconnect

import android.content.Context
import com.shareconnect.jdownloaderconnect.data.dao.*
import com.shareconnect.jdownloaderconnect.data.repository.JDownloaderRepository
import com.shareconnect.jdownloaderconnect.data.repository.MyJDownloaderRepository
import com.shareconnect.jdownloaderconnect.data.repository.ServerRepository
import com.shareconnect.jdownloaderconnect.network.api.MyJDownloaderApi
import com.shareconnect.jdownloaderconnect.presentation.viewmodel.AccountViewModel
import com.shareconnect.jdownloaderconnect.presentation.viewmodel.DownloadsViewModel
import com.shareconnect.jdownloaderconnect.presentation.viewmodel.LinkGrabberViewModel
import com.shareconnect.jdownloaderconnect.presentation.viewmodel.MyJDownloaderViewModel
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

    val myJDownloaderRepository: MyJDownloaderRepository by lazy {
        MyJDownloaderRepository(myJDownloaderApi, jDownloaderRepository)
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

    val myJDownloaderViewModel: MyJDownloaderViewModel by lazy {
        MyJDownloaderViewModel(myJDownloaderRepository)
    }
}