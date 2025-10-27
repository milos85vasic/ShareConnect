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


package com.shareconnect.paperlessngconnect.pdf

import android.content.Context
import android.util.Log
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import java.io.File
import java.io.InputStream

/**
 * PDF Viewer Manager using AndroidPdfViewer library
 * Provides PDF rendering, navigation, zoom, and search functionality
 */
class PdfViewerManager(
    private val context: Context,
    private val pdfView: PDFView
) {

    private val tag = "PdfViewerManager"

    private var currentPage: Int = 0
    private var pageCount: Int = 0
    private var searchResults: List<SearchResult> = emptyList()
    private var currentSearchIndex: Int = -1

    /**
     * Listener interface for PDF viewer events
     */
    interface PdfViewerListener {
        fun onPdfLoaded(pageCount: Int)
        fun onPageChanged(page: Int, pageCount: Int)
        fun onPdfError(error: Throwable)
        fun onSearchCompleted(results: List<SearchResult>)
    }

    private var listener: PdfViewerListener? = null

    /**
     * Set the PDF viewer listener
     */
    fun setListener(listener: PdfViewerListener) {
        this.listener = listener
    }

    /**
     * Load PDF from file
     * @param file PDF file to load
     * @param password Optional PDF password
     */
    fun loadPdfFromFile(file: File, password: String? = null) {
        try {
            Log.d(tag, "Loading PDF from file: ${file.absolutePath}")

            val configurator = pdfView.fromFile(file)

            if (password != null) {
                configurator.password(password)
            }

            configurator
                .defaultPage(currentPage)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAnnotationRendering(true)
                .scrollHandle(DefaultScrollHandle(context))
                .spacing(10)
                .onLoad(OnLoadCompleteListener { nbPages ->
                    pageCount = nbPages
                    Log.d(tag, "PDF loaded with $nbPages pages")
                    listener?.onPdfLoaded(nbPages)
                })
                .onPageChange(OnPageChangeListener { page, pageCount ->
                    currentPage = page
                    Log.d(tag, "Page changed to $page of $pageCount")
                    listener?.onPageChanged(page, pageCount)
                })
                .onError(OnErrorListener { t ->
                    Log.e(tag, "Error loading PDF", t)
                    listener?.onPdfError(t)
                })
                .onPageError(OnPageErrorListener { page, t ->
                    Log.e(tag, "Error loading page $page", t)
                })
                .load()

        } catch (e: Exception) {
            Log.e(tag, "Exception loading PDF from file", e)
            listener?.onPdfError(e)
        }
    }

    /**
     * Load PDF from input stream
     * @param inputStream PDF input stream
     * @param password Optional PDF password
     */
    fun loadPdfFromStream(inputStream: InputStream, password: String? = null) {
        try {
            Log.d(tag, "Loading PDF from stream")

            val configurator = pdfView.fromStream(inputStream)

            if (password != null) {
                configurator.password(password)
            }

            configurator
                .defaultPage(currentPage)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAnnotationRendering(true)
                .scrollHandle(DefaultScrollHandle(context))
                .spacing(10)
                .onLoad(OnLoadCompleteListener { nbPages ->
                    pageCount = nbPages
                    Log.d(tag, "PDF loaded with $nbPages pages")
                    listener?.onPdfLoaded(nbPages)
                })
                .onPageChange(OnPageChangeListener { page, pageCount ->
                    currentPage = page
                    Log.d(tag, "Page changed to $page of $pageCount")
                    listener?.onPageChanged(page, pageCount)
                })
                .onError(OnErrorListener { t ->
                    Log.e(tag, "Error loading PDF", t)
                    listener?.onPdfError(t)
                })
                .onPageError(OnPageErrorListener { page, t ->
                    Log.e(tag, "Error loading page $page", t)
                })
                .load()

        } catch (e: Exception) {
            Log.e(tag, "Exception loading PDF from stream", e)
            listener?.onPdfError(e)
        }
    }

    /**
     * Load PDF from byte array
     * @param bytes PDF byte array
     * @param password Optional PDF password
     */
    fun loadPdfFromBytes(bytes: ByteArray, password: String? = null) {
        try {
            Log.d(tag, "Loading PDF from byte array (${bytes.size} bytes)")

            val configurator = pdfView.fromBytes(bytes)

            if (password != null) {
                configurator.password(password)
            }

            configurator
                .defaultPage(currentPage)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAnnotationRendering(true)
                .scrollHandle(DefaultScrollHandle(context))
                .spacing(10)
                .onLoad(OnLoadCompleteListener { nbPages ->
                    pageCount = nbPages
                    Log.d(tag, "PDF loaded with $nbPages pages")
                    listener?.onPdfLoaded(nbPages)
                })
                .onPageChange(OnPageChangeListener { page, pageCount ->
                    currentPage = page
                    Log.d(tag, "Page changed to $page of $pageCount")
                    listener?.onPageChanged(page, pageCount)
                })
                .onError(OnErrorListener { t ->
                    Log.e(tag, "Error loading PDF", t)
                    listener?.onPdfError(t)
                })
                .onPageError(OnPageErrorListener { page, t ->
                    Log.e(tag, "Error loading page $page", t)
                })
                .load()

        } catch (e: Exception) {
            Log.e(tag, "Exception loading PDF from bytes", e)
            listener?.onPdfError(e)
        }
    }

    /**
     * Navigate to specific page
     * @param page Page number (0-based)
     */
    fun goToPage(page: Int) {
        if (page >= 0 && page < pageCount) {
            currentPage = page
            pdfView.jumpTo(page, true)
            Log.d(tag, "Navigated to page $page")
        } else {
            Log.w(tag, "Invalid page number: $page (page count: $pageCount)")
        }
    }

    /**
     * Navigate to next page
     */
    fun nextPage() {
        if (currentPage < pageCount - 1) {
            goToPage(currentPage + 1)
        }
    }

    /**
     * Navigate to previous page
     */
    fun previousPage() {
        if (currentPage > 0) {
            goToPage(currentPage - 1)
        }
    }

    /**
     * Navigate to first page
     */
    fun firstPage() {
        goToPage(0)
    }

    /**
     * Navigate to last page
     */
    fun lastPage() {
        goToPage(pageCount - 1)
    }

    /**
     * Get current page number
     * @return Current page (0-based)
     */
    fun getCurrentPage(): Int = currentPage

    /**
     * Get total page count
     * @return Total number of pages
     */
    fun getPageCount(): Int = pageCount

    /**
     * Zoom in
     */
    fun zoomIn() {
        val currentZoom = pdfView.zoom
        pdfView.zoomTo(currentZoom + 0.5f)
        Log.d(tag, "Zoomed in to ${currentZoom + 0.5f}")
    }

    /**
     * Zoom out
     */
    fun zoomOut() {
        val currentZoom = pdfView.zoom
        if (currentZoom > 1.0f) {
            pdfView.zoomTo(currentZoom - 0.5f)
            Log.d(tag, "Zoomed out to ${currentZoom - 0.5f}")
        }
    }

    /**
     * Reset zoom to default
     */
    fun resetZoom() {
        pdfView.resetZoom()
        Log.d(tag, "Zoom reset")
    }

    /**
     * Zoom to specific level
     * @param zoom Zoom level (1.0 = 100%)
     */
    fun zoomTo(zoom: Float) {
        pdfView.zoomTo(zoom)
        Log.d(tag, "Zoom set to $zoom")
    }

    /**
     * Get current zoom level
     * @return Current zoom level
     */
    fun getZoom(): Float = pdfView.zoom

    /**
     * Enable/disable zoom
     * @param enabled True to enable zoom
     */
    fun setZoomEnabled(enabled: Boolean) {
        // Note: AndroidPdfViewer doesn't have a direct method to disable zoom
        // This is a placeholder for future implementation
        Log.d(tag, "Zoom ${if (enabled) "enabled" else "disabled"}")
    }

    /**
     * Search for text in PDF
     * Note: This is a basic implementation. Full text search requires PDF text extraction
     * which is not directly supported by AndroidPdfViewer. This would need additional
     * libraries like Apache PDFBox or iText for Android.
     *
     * @param query Search query
     */
    fun search(query: String) {
        // This is a placeholder for search functionality
        // In a real implementation, you would need to:
        // 1. Extract text from PDF using a library like PDFBox
        // 2. Search for the query in the extracted text
        // 3. Store page numbers and positions of matches
        // 4. Highlight matches on the PDF

        Log.d(tag, "Search requested for: $query")
        Log.w(tag, "Full text search not implemented - requires additional PDF text extraction library")

        // For now, just notify that search completed with no results
        searchResults = emptyList()
        currentSearchIndex = -1
        listener?.onSearchCompleted(searchResults)
    }

    /**
     * Go to next search result
     */
    fun nextSearchResult() {
        if (searchResults.isNotEmpty() && currentSearchIndex < searchResults.size - 1) {
            currentSearchIndex++
            val result = searchResults[currentSearchIndex]
            goToPage(result.page)
            Log.d(tag, "Navigated to search result ${currentSearchIndex + 1} of ${searchResults.size}")
        }
    }

    /**
     * Go to previous search result
     */
    fun previousSearchResult() {
        if (searchResults.isNotEmpty() && currentSearchIndex > 0) {
            currentSearchIndex--
            val result = searchResults[currentSearchIndex]
            goToPage(result.page)
            Log.d(tag, "Navigated to search result ${currentSearchIndex + 1} of ${searchResults.size}")
        }
    }

    /**
     * Clear search results
     */
    fun clearSearch() {
        searchResults = emptyList()
        currentSearchIndex = -1
        Log.d(tag, "Search results cleared")
    }

    /**
     * Data class for search results
     */
    data class SearchResult(
        val page: Int,
        val text: String,
        val position: Position
    )

    /**
     * Data class for position in PDF
     */
    data class Position(
        val x: Float,
        val y: Float
    )

    /**
     * Release resources
     */
    fun release() {
        pdfView.recycle()
        searchResults = emptyList()
        currentSearchIndex = -1
        Log.d(tag, "Resources released")
    }
}
