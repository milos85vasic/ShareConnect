package com.shareconnect.utorrentconnect.transport.okhttp

import com.google.common.truth.Truth.assertThat
import org.jsoup.Jsoup
import org.junit.Test

/**
 * Unit tests for TokenInterceptor HTML parsing logic.
 * Tests token extraction from HTML responses.
 */
class TokenInterceptorTest {

    @Test
    fun extractTokenFromHtml_standardFormat_extractsCorrectly() {
        val html = """<html><div id='token'>EXTRACTED_TOKEN_123</div></html>"""

        val document = Jsoup.parse(html)
        val tokenElement = document.select("div#token").first()
        val token = tokenElement?.text()?.trim()

        assertThat(token).isEqualTo("EXTRACTED_TOKEN_123")
    }

    @Test
    fun extractTokenFromHtml_withWhitespace_trimsCorrectly() {
        val html = """<html><div id='token'>  TOKEN_WITH_SPACES  </div></html>"""

        val document = Jsoup.parse(html)
        val tokenElement = document.select("div#token").first()
        val token = tokenElement?.text()?.trim()

        assertThat(token).isEqualTo("TOKEN_WITH_SPACES")
    }

    @Test
    fun extractTokenFromHtml_complexHtml_findsTokenDiv() {
        val html = """
            <html>
                <head><title>uTorrent</title></head>
                <body>
                    <div class="header">Header Content</div>
                    <div id="token">COMPLEX_HTML_TOKEN</div>
                    <div class="footer">Footer Content</div>
                </body>
            </html>
        """

        val document = Jsoup.parse(html)
        val tokenElement = document.select("div#token").first()
        val token = tokenElement?.text()?.trim()

        assertThat(token).isEqualTo("COMPLEX_HTML_TOKEN")
    }

    @Test
    fun extractTokenFromHtml_emptyToken_returnsEmptyString() {
        val html = """<html><div id='token'></div></html>"""

        val document = Jsoup.parse(html)
        val tokenElement = document.select("div#token").first()
        val token = tokenElement?.text()?.trim()

        assertThat(token).isEmpty()
    }

    @Test
    fun extractTokenFromHtml_noTokenDiv_returnsNull() {
        val html = """<html><div class='not-token'>WRONG_DIV</div></html>"""

        val document = Jsoup.parse(html)
        val tokenElement = document.select("div#token").first()

        assertThat(tokenElement).isNull()
    }

    @Test
    fun extractTokenFromHtml_malformedHtml_handlesGracefully() {
        val html = """<html><div id='token'BROKEN_HTML>TOKEN</div>"""

        // JSoup is lenient with malformed HTML
        val document = Jsoup.parse(html)
        val tokenElement = document.select("div#token").first()
        val token = tokenElement?.text()?.trim()

        // JSoup should still extract the token despite malformed HTML
        assertThat(token).isNotEmpty()
    }

    @Test
    fun extractTokenFromHtml_nestedElements_extractsText() {
        val html = """<html><div id='token'><span>NESTED_TOKEN</span></div></html>"""

        val document = Jsoup.parse(html)
        val tokenElement = document.select("div#token").first()
        val token = tokenElement?.text()?.trim()

        assertThat(token).isEqualTo("NESTED_TOKEN")
    }

    @Test
    fun extractTokenFromHtml_multipleSpaces_normalizesToSingleSpace() {
        val html = """<html><div id='token'>TOKEN    WITH    SPACES</div></html>"""

        val document = Jsoup.parse(html)
        val tokenElement = document.select("div#token").first()
        val token = tokenElement?.text()?.trim()

        // JSoup normalizes multiple spaces to single space
        assertThat(token).isEqualTo("TOKEN WITH SPACES")
    }

    @Test
    fun extractTokenFromHtml_realWorldFormat_worksCorrectly() {
        // Actual format from uTorrent Web UI
        val html = """
            <!DOCTYPE html>
            <html id="utorrent">
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
                <title>uTorrent</title>
            </head>
            <body>
                <div id='token' style='display:none;'>AB1234CD5678EF90</div>
            </body>
            </html>
        """

        val document = Jsoup.parse(html)
        val tokenElement = document.select("div#token").first()
        val token = tokenElement?.text()?.trim()

        assertThat(token).isEqualTo("AB1234CD5678EF90")
    }

    @Test
    fun extractTokenFromHtml_unicodeToken_extractsCorrectly() {
        val html = """<html><div id='token'>TOKEN_WITH_UNICODE_™®©</div></html>"""

        val document = Jsoup.parse(html)
        val tokenElement = document.select("div#token").first()
        val token = tokenElement?.text()?.trim()

        assertThat(token).contains("TOKEN_WITH_UNICODE")
    }

    @Test
    fun extractTokenFromHtml_longToken_extractsCompletely() {
        val longToken = "A".repeat(100) // 100 character token
        val html = """<html><div id='token'>$longToken</div></html>"""

        val document = Jsoup.parse(html)
        val tokenElement = document.select("div#token").first()
        val token = tokenElement?.text()?.trim()

        assertThat(token).hasLength(100)
        assertThat(token).isEqualTo(longToken)
    }
}
