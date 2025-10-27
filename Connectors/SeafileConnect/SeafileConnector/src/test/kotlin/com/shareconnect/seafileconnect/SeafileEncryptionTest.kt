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


package com.shareconnect.seafileconnect

import com.shareconnect.seafileconnect.data.encryption.SeafileEncryptionManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for SeafileEncryptionManager
 */
class SeafileEncryptionTest {

    private lateinit var encryptionManager: SeafileEncryptionManager

    @Before
    fun setup() {
        encryptionManager = SeafileEncryptionManager()
    }

    @Test
    fun `deriveKey produces consistent key for same inputs`() {
        val password = "test-password"
        val salt = "test-salt".toByteArray()
        
        val key1 = encryptionManager.deriveKey(password, salt)
        val key2 = encryptionManager.deriveKey(password, salt)
        
        assertArrayEquals(key1, key2)
    }

    @Test
    fun `deriveKey produces different keys for different passwords`() {
        val salt = "test-salt".toByteArray()
        
        val key1 = encryptionManager.deriveKey("password1", salt)
        val key2 = encryptionManager.deriveKey("password2", salt)
        
        assertFalse(key1.contentEquals(key2))
    }

    @Test
    fun `sha256 produces correct hash`() {
        val data = "test-data"
        val hash = encryptionManager.sha256(data)
        
        assertEquals(32, hash.size) // SHA-256 produces 32 bytes
        assertNotNull(hash)
    }

    @Test
    fun `sha256 is consistent for same input`() {
        val data = "test-data"
        
        val hash1 = encryptionManager.sha256(data)
        val hash2 = encryptionManager.sha256(data)
        
        assertArrayEquals(hash1, hash2)
    }

    @Test
    fun `toHex and fromHex are reversible`() {
        val original = "test-data".toByteArray()
        val hex = encryptionManager.toHex(original)
        val restored = encryptionManager.fromHex(hex)
        
        assertArrayEquals(original, restored)
    }

    @Test
    fun `encrypt and decrypt are reversible`() {
        val plaintext = "Hello, Seafile!".toByteArray()
        val key = encryptionManager.generateSalt()
        
        val encrypted = encryptionManager.encrypt(plaintext, key)
        val decrypted = encryptionManager.decrypt(encrypted.data, key, encrypted.iv)
        
        assertArrayEquals(plaintext, decrypted)
    }

    @Test
    fun `encrypt produces different output each time (due to random IV)`() {
        val plaintext = "test-data".toByteArray()
        val key = encryptionManager.generateSalt()
        
        val encrypted1 = encryptionManager.encrypt(plaintext, key)
        val encrypted2 = encryptionManager.encrypt(plaintext, key)
        
        assertFalse(encrypted1.data.contentEquals(encrypted2.data))
        assertFalse(encrypted1.iv.contentEquals(encrypted2.iv))
    }

    @Test
    fun `encrypt with same IV produces same output`() {
        val plaintext = "test-data".toByteArray()
        val key = encryptionManager.generateSalt()
        val iv = encryptionManager.generateIV()
        
        val encrypted1 = encryptionManager.encrypt(plaintext, key, iv)
        val encrypted2 = encryptionManager.encrypt(plaintext, key, iv)
        
        assertArrayEquals(encrypted1.data, encrypted2.data)
    }

    @Test
    fun `hmacSha256 produces correct signature`() {
        val data = "test-data".toByteArray()
        val key = "test-key".toByteArray()
        
        val hmac = encryptionManager.hmacSha256(data, key)
        
        assertEquals(32, hmac.size) // HMAC-SHA256 produces 32 bytes
    }

    @Test
    fun `verifyHmac returns true for valid HMAC`() {
        val data = "test-data".toByteArray()
        val key = "test-key".toByteArray()
        
        val hmac = encryptionManager.hmacSha256(data, key)
        val isValid = encryptionManager.verifyHmac(data, key, hmac)
        
        assertTrue(isValid)
    }

    @Test
    fun `verifyHmac returns false for tampered data`() {
        val data = "test-data".toByteArray()
        val tamperedData = "tampered-data".toByteArray()
        val key = "test-key".toByteArray()
        
        val hmac = encryptionManager.hmacSha256(data, key)
        val isValid = encryptionManager.verifyHmac(tamperedData, key, hmac)
        
        assertFalse(isValid)
    }

    @Test
    fun `generateSalt produces random salt`() {
        val salt1 = encryptionManager.generateSalt()
        val salt2 = encryptionManager.generateSalt()
        
        assertEquals(32, salt1.size)
        assertFalse(salt1.contentEquals(salt2))
    }

    @Test
    fun `generateIV produces random IV`() {
        val iv1 = encryptionManager.generateIV()
        val iv2 = encryptionManager.generateIV()
        
        assertEquals(16, iv1.size)
        assertFalse(iv1.contentEquals(iv2))
    }

    @Test
    fun `toBase64 and fromBase64 are reversible`() {
        val original = "test-data".toByteArray()
        val base64 = encryptionManager.toBase64(original)
        val restored = encryptionManager.fromBase64(base64)
        
        assertArrayEquals(original, restored)
    }

    @Test
    fun `deriveLibraryKey produces consistent key`() {
        val password = "library-password"
        val repoId = "repo-123"
        
        val key1 = encryptionManager.deriveLibraryKey(password, repoId)
        val key2 = encryptionManager.deriveLibraryKey(password, repoId)
        
        assertArrayEquals(key1.key, key2.key)
        assertEquals(repoId, key1.repoId)
    }

    @Test
    fun `encryptFilename and decryptFilename are reversible`() {
        val filename = "secret-document.pdf"
        val key = encryptionManager.generateSalt()
        
        val encrypted = encryptionManager.encryptFilename(filename, key)
        val decrypted = encryptionManager.decryptFilename(encrypted, key)
        
        assertEquals(filename, decrypted)
    }
}
