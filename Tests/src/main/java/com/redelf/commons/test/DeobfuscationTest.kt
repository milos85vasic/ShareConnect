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


package com.redelf.commons.test

abstract class DeobfuscationTest : BaseTest() {

    // FIXME: Fix the test

//    protected fun getObfuscatedString(): String {
//
//        val input = "test"
//        val obfuscated = input.obfuscate()
//        val deobfuscated = obfuscated.deobfuscate()
//
//        Assert.assertEquals(input, deobfuscated)
//        Assert.assertNotEquals(input, obfuscated)
//
//        return obfuscated
//    }
//
//    private fun getObfuscatedData(): String {
//
//        val resourceId = com.redelf.commons.R.string.ob_test
//
//        val obfuscated = applicationContext.getString(resourceId)
//
//        Assert.assertTrue(obfuscated.isNotEmpty())
//
//        return obfuscated
//    }
//
//    private fun getDeobfuscatedData(): String {
//
//        val resourceId = com.redelf.commons.R.string.ob_test
//
//        val obfuscated = applicationContext.getString(resourceId)
//
//        Assert.assertTrue(obfuscated.isNotEmpty())
//
//        val deobfuscated = applicationContext.deobfuscateString(resourceId)
//
//        Assert.assertTrue(deobfuscated.isNotEmpty())
//
//        return deobfuscated
//    }
//
//    private fun getSalt(): ObfuscatorSalt? {
//
//        val deobfuscator = DefaultObfuscator.getStrategy()
//
//        Assert.assertNotNull(deobfuscator)
//
//        val salt = deobfuscator.saltProvider.obtain()
//
//        Assert.assertNotNull(salt)
//        Assert.assertNull(salt?.error)
//        Assert.assertTrue(isNotEmpty(salt?.takeValue()))
//
//        return salt
//    }
//
//    private fun waitForObfuscator(timeoutInMilliseconds: Long = 5000L) {
//
//        yieldWhile(
//
//            timeoutInMilliseconds = timeoutInMilliseconds
//
//        ) {
//
//            DefaultObfuscator.isNotReady()
//        }
//
//        Assert.assertTrue(DefaultObfuscator.isReady())
//    }
//
//    @Before
//    fun cleanup() {
//
//        val cleared = SecretsManager.obtain().reset()
//
//        Assert.assertTrue(cleared)
//    }
//
//    fun testDeobfuscation(
//
//        expectedSalt: String,
//        expectedDeobfuscatedData: String,
//        expectedObfuscatorIdentity: String,
//        timeoutInMilliseconds: Long = 5000L
//
//    ) {
//
//        waitForObfuscator(timeoutInMilliseconds)
//
//        val obfuscator = DefaultObfuscator.getStrategy()
//
//        Assert.assertNotNull(obfuscator)
//        Assert.assertEquals(expectedObfuscatorIdentity, obfuscator.name())
//
//        var salt = getSalt()
//        val id = salt?.identifier
//        val hashCode = salt?.hashCode()
//
//        Assert.assertNotNull(id)
//        Assert.assertTrue((hashCode ?: 0) > 0)
//
//        // FIXME: Recheck these:
//        //        Assert.assertTrue(salt?.firstTimeObtained?.get() == true)
//        //        Assert.assertEquals(1, salt?.refreshCount?.get())
//        //        Assert.assertEquals(0, salt?.refreshSkipCount?.get())
//
//        Assert.assertEquals(expectedSalt, salt?.takeValue())
//
//        salt = getSalt()
//
//        Assert.assertEquals(id, salt?.identifier)
//        Assert.assertEquals(hashCode, salt?.hashCode())
//
//        // FIXME: Recheck these:
//        //        Assert.assertTrue(salt?.fromCache() == true)
//        //        Assert.assertEquals(1, salt?.refreshCount?.get())
//        //        Assert.assertEquals(1, salt?.refreshSkipCount?.get())
//
//        Assert.assertEquals(expectedSalt, salt?.takeValue())
//
//        val obfuscatedResource = getObfuscatedData()
//        val obfuscated = expectedDeobfuscatedData.obfuscate()
//
//        Assert.assertTrue(isNotEmpty(obfuscated))
//
//        Assert.assertEquals(obfuscated, obfuscatedResource)
//
//        val data = getDeobfuscatedData()
//
//        Assert.assertEquals(expectedDeobfuscatedData, data)
//    }
}