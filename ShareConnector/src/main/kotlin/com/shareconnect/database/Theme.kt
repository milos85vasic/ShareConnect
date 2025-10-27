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


package com.shareconnect.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "themes")
class Theme {
    @PrimaryKey
    var id: Int = 0

    var name: String? = null
    var colorScheme: String? = null
    var isDarkMode: Boolean = false
    var isDefault: Boolean = false
    var isCustom: Boolean = false

    constructor()

    @androidx.room.Ignore
    constructor(id: Int, name: String?, colorScheme: String?, isDarkMode: Boolean, isDefault: Boolean) {
        this.id = id
        this.name = name
        this.colorScheme = colorScheme
        this.isDarkMode = isDarkMode
        this.isDefault = isDefault
        this.isCustom = false
    }

    @androidx.room.Ignore
    constructor(id: Int, name: String?, colorScheme: String?, isDarkMode: Boolean, isDefault: Boolean, isCustom: Boolean) {
        this.id = id
        this.name = name
        this.colorScheme = colorScheme
        this.isDarkMode = isDarkMode
        this.isDefault = isDefault
        this.isCustom = isCustom
    }
}