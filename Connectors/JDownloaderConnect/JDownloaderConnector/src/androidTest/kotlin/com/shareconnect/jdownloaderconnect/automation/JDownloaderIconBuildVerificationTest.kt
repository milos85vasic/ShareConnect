package com.shareconnect.jdownloaderconnect.automation

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Build-time verification test for JDownloaderConnect icon assets.
 * This test verifies the actual generated icon files during the build process.
 */
@RunWith(AndroidJUnit4::class)
class JDownloaderIconBuildVerificationTest {

    @Test
    fun testAllMipmapDirectoriesExist() {
        val resDir = File("src/main/res")
        assertTrue("res directory should exist", resDir.exists())

        val requiredMipmapDirs = listOf(
            "mipmap-mdpi",
            "mipmap-hdpi",
            "mipmap-xhdpi",
            "mipmap-xxhdpi",
            "mipmap-xxxhdpi",
            "mipmap-anydpi-v26"
        )

        requiredMipmapDirs.forEach { dirName ->
            val dir = File(resDir, dirName)
            assertTrue("$dirName directory should exist", dir.exists())
            assertTrue("$dirName should be a directory", dir.isDirectory)
        }
    }

    @Test
    fun testMipmapIconFilesExist() {
        val resDir = File("src/main/res")

        val densities = listOf("mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi")
        val iconTypes = listOf("ic_launcher.png", "ic_launcher_round.png")

        densities.forEach { density ->
            val densityDir = File(resDir, "mipmap-$density")
            iconTypes.forEach { iconType ->
                val iconFile = File(densityDir, iconType)
                assertTrue("$iconType should exist in mipmap-$density", iconFile.exists())
                assertTrue("$iconType should be a file", iconFile.isFile)
                assertTrue("$iconType should not be empty", iconFile.length() > 0)
            }
        }
    }

    @Test
    fun testAdaptiveIconXmlFilesExist() {
        val resDir = File("src/main/res")
        val adaptiveDir = File(resDir, "mipmap-anydpi-v26")

        val xmlFiles = listOf("ic_launcher.xml", "ic_launcher_round.xml")
        xmlFiles.forEach { xmlFile ->
            val file = File(adaptiveDir, xmlFile)
            assertTrue("$xmlFile should exist", file.exists())
            assertTrue("$xmlFile should be a file", file.isFile)
            assertTrue("$xmlFile should not be empty", file.length() > 0)
        }
    }

    @Test
    fun testAdaptiveIconXmlContent() {
        val resDir = File("src/main/res")
        val adaptiveDir = File(resDir, "mipmap-anydpi-v26")

        val xmlFiles = listOf("ic_launcher.xml", "ic_launcher_round.xml")
        xmlFiles.forEach { xmlFile ->
            val file = File(adaptiveDir, xmlFile)
            val content = file.readText()

            assertTrue("$xmlFile should contain adaptive-icon tag", content.contains("<adaptive-icon"))
            assertTrue("$xmlFile should reference background", content.contains("@drawable/ic_launcher_background"))
            assertTrue("$xmlFile should reference foreground", content.contains("@drawable/ic_launcher_foreground"))
        }
    }

    @Test
    fun testDrawableIconFilesExist() {
        val resDir = File("src/main/res")
        val drawableDir = File(resDir, "drawable")

        val requiredDrawables = listOf(
            "ic_launcher_foreground.png",
            "ic_launcher_background.png",
            "splash_logo.png"
        )

        requiredDrawables.forEach { drawable ->
            val file = File(drawableDir, drawable)
            assertTrue("$drawable should exist", file.exists())
            assertTrue("$drawable should be a file", file.isFile)
            assertTrue("$drawable should not be empty", file.length() > 0)
        }
    }

    @Test
    fun testIconSourceFilesExist() {
        val assetsDir = File("../Assets")
        assertTrue("Assets directory should exist", assetsDir.exists())

        val logoFile = File(assetsDir, "Logo.jpeg")
        assertTrue("Logo.jpeg source file should exist", logoFile.exists())
        assertTrue("Logo.jpeg should not be empty", logoFile.length() > 0)

        val generateScript = File(assetsDir, "generate_icons.sh")
        assertTrue("generate_icons.sh script should exist", generateScript.exists())
        assertTrue("generate_icons.sh should be executable", generateScript.canExecute())
    }

    @Test
    fun testNoLegacyIconFilesExist() {
        val resDir = File("src/main/res")

        // Find all PNG files that are not our generated icons
        val allPngFiles = resDir.walk()
            .filter { it.isFile && it.extension == "png" }
            .map { it.name }
            .toList()

        val allowedIcons = setOf(
            "ic_launcher.png",
            "ic_launcher_round.png",
            "ic_launcher_foreground.png",
            "ic_launcher_background.png",
            "splash_logo.png"
        )

        val legacyIcons = allPngFiles.filter { !allowedIcons.contains(it) }
        assertTrue("No legacy icon files should exist. Found: $legacyIcons", legacyIcons.isEmpty())
    }

    @Test
    fun testIconFileSizesAreReasonable() {
        val resDir = File("src/main/res")

        // Check that icon files have reasonable sizes (not corrupted)
        val iconFiles = resDir.walk()
            .filter { it.isFile && it.extension == "png" }
            .filter { it.name.contains("ic_launcher") || it.name == "splash_logo.png" }

        iconFiles.forEach { file ->
            val size = file.length()
            assertTrue("${file.name} should not be empty", size > 0)
            assertTrue("${file.name} should not be too small (possible corruption)", size > 100)

            // Splash logo should be much larger than launcher icons
            if (file.name == "splash_logo.png") {
                assertTrue("splash_logo.png should be large (>100KB)", size > 100000)
            } else {
                // Launcher icons should be reasonable size
                assertTrue("${file.name} should be reasonable size (<50KB)", size < 50000)
            }
        }
    }

    @Test
    fun testGenerationScriptIsComplete() {
        val scriptFile = File("../Assets/generate_icons.sh")
        val content = scriptFile.readText()

        // Verify the script contains all necessary components
        val requiredContent = listOf(
            "ImageMagick",
            "convert",
            "mipmap-mdpi",
            "mipmap-hdpi",
            "mipmap-xhdpi",
            "mipmap-xxhdpi",
            "mipmap-xxxhdpi",
            "mipmap-anydpi-v26",
            "ic_launcher_foreground",
            "ic_launcher_background",
            "splash_logo",
            "adaptive-icon",
            "Lanczos"
        )

        requiredContent.forEach { required ->
            assertTrue("Script should contain '$required'", content.contains(required))
        }
    }
}