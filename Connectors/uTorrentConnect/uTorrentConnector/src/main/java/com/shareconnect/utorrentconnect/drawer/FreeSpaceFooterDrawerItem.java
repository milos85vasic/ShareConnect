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

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.materialdrawer.model.BaseDrawerItem;

import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.utils.TextUtils;

import java.util.List;

public class FreeSpaceFooterDrawerItem extends BaseDrawerItem<FreeSpaceFooterDrawerItem, FreeSpaceFooterDrawerItem.ViewHolder> {

    private long freeSpace = -1L;

    public FreeSpaceFooterDrawerItem withFreeSpace(long freeSpace) {
        this.freeSpace = freeSpace;
        return this;
    }

    @Override
    public int getType() {
        return R.id.drawer_item_free_space;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.drawer_footer_layout;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bindView(ViewHolder holder, List<Object> payloads) {
        super.bindView(holder, payloads);

        boolean freeSpaceAvailable = freeSpace >= 0L;
        holder.freeSpaceText.setText(holder.freeSpaceText.getResources()
                .getString(R.string.free_space_title, freeSpaceAvailable ? TextUtils.displayableSize(freeSpace) : "…"));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView freeSpaceText;

        ViewHolder(View view) {
            super(view);
            freeSpaceText = view.findViewById(R.id.free_space_text);
        }
    }
}
