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

import androidx.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.databinding.PeersSortListItemBinding;
import com.shareconnect.utorrentconnect.sorting.PeersSortedBy;
import com.shareconnect.utorrentconnect.sorting.SortOrder;

public class PeersSortingListAdapter extends BaseAdapter {

    private PeersSortedBy currentSorting;
    private SortOrder sortOrder;

    public void setCurrentSorting(PeersSortedBy sortedBy, SortOrder sortOrder) {
        this.currentSorting = sortedBy;
        this.sortOrder = sortOrder;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return PeersSortedBy.values().length;
    }

    @Override
    public PeersSortedBy getItem(int position) {
        return PeersSortedBy.values()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            PeersSortListItemBinding binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()), R.layout.peers_sort_list_item, parent, false);
            view = binding.getRoot();
            view.setTag(binding);
        }

        PeersSortListItemBinding binding = (PeersSortListItemBinding) view.getTag();

        PeersSortedBy sorting = getItem(position);
        binding.setSortedBy(sorting);
        binding.setSortOrder(sorting == currentSorting ? sortOrder : null);
        binding.executePendingBindings();

        return view;
    }
}
