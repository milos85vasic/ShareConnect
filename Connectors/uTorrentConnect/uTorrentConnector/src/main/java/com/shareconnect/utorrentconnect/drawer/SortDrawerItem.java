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


package com.shareconnect.utorrentconnect.drawer;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import com.shareconnect.utorrentconnect.sorting.SortOrder;
import com.shareconnect.utorrentconnect.sorting.SortedBy;

public class SortDrawerItem extends PrimaryDrawerItem {

    private SortedBy sortedBy;
    private SortOrder sortOrder = null;

    public SortDrawerItem(SortedBy sortedBy) {
        this.sortedBy = sortedBy;
    }

    @Override
    public SortDrawerItem withName(@StringRes int nameRes) {
        super.withName(nameRes);
        return this;
    }

    public SortedBy getSortedBy() {
        return sortedBy;
    }

    public void setSortOrder(@Nullable SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        withBadge(sortOrder != null ? sortOrder.getSymbol() : "");
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }
}
