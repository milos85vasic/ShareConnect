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


package com.shareconnect.utorrentconnect.torrentdetails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.databinding.TrackersSortListItemBinding;
import com.shareconnect.utorrentconnect.sorting.TrackersSortedBy;
import com.shareconnect.utorrentconnect.sorting.SortOrder;

public class TrackersSortingListAdapter extends BaseAdapter {

    private TrackersSortedBy currentSorting;
    private SortOrder sortOrder;

    public void setCurrentSorting(TrackersSortedBy sortedBy, SortOrder sortOrder) {
        this.currentSorting = sortedBy;
        this.sortOrder = sortOrder;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return TrackersSortedBy.values().length;
    }

    @Override
    public TrackersSortedBy getItem(int position) {
        return TrackersSortedBy.values()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            TrackersSortListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.trackers_sort_list_item, parent, false);
            view = binding.getRoot();
            view.setTag(binding);
        }

        TrackersSortListItemBinding binding = (TrackersSortListItemBinding) view.getTag();

        TrackersSortedBy sorting = getItem(position);
        binding.setSortedBy(sorting);
        binding.setSortOrder(sorting == currentSorting ? sortOrder : null);
        binding.executePendingBindings();

        return view;
    }
}
