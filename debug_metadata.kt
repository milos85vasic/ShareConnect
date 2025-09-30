import android.content.Context
import org.mockito.Mockito.mock

// Simple test to debug MetadataFetcher
fun main() {
    val mockContext = mock(Context::class.java)
    val fetcher = MetadataFetcher(mockContext)

    val magnetUrl = "magnet:?xt=urn:btih:fedcba9876543210&xl=2048000000"
    val metadata = runBlocking { fetcher.fetchMetadata(magnetUrl) }

    println("Title: ${metadata.title}")
    println("Description: ${metadata.description}")
    println("SiteName: ${metadata.siteName}")

    // Check what the test expects
    println("\nTest expectations:")
    println("Title should be: 'Magnet Link'")
    println("Description should contain: '1.9 GB'")
    println("Description should contain: 'fedcba98...'")
    println("SiteName should be: 'BitTorrent'")
}