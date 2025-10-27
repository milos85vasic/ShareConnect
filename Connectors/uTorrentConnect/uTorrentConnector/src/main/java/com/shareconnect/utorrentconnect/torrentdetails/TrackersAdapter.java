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
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.databinding.TrackerItemLayoutBinding;
import com.shareconnect.utorrentconnect.model.json.TrackerStats;
import com.shareconnect.utorrentconnect.sorting.SortOrder;
import com.shareconnect.utorrentconnect.sorting.TrackersSortedBy;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class TrackersAdapter extends RecyclerView.Adapter<TrackersAdapter.ViewHolder> {
    private Comparator<TrackerStats> comparator;
    private SortOrder order;

    private TrackerStats[] trackerStats = {};
    private final TrackerActionListener listener;

    public TrackersAdapter(TrackerActionListener listener) {
        this.listener = listener;
        setHasStableIds(true);
    }

    public void setTrackerStats(@NonNull TrackerStats[] trackerStats) {
        this.trackerStats = trackerStats;
        sort();
        notifyDataSetChanged();
    }

    public void setSorting(TrackersSortedBy sorting, SortOrder order) {
        comparator = sorting.comparator;
        this.order = order;
        sort();
        notifyDataSetChanged();
    }

    private void sort() {
        if (ArrayUtils.isEmpty(trackerStats) || comparator == null) return;
        Arrays.sort(trackerStats, order == SortOrder.ASCENDING ? comparator : Collections.reverseOrder(comparator));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TrackerItemLayoutBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.tracker_item_layout, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.binding.setTrackerStats(trackerStats[position]);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return trackerStats.length;
    }

    @Override
    public long getItemId(int position) {
        return trackerStats[position].id;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {

        private final TrackerItemLayoutBinding binding;

        public ViewHolder(TrackerItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(v.getContext(), v);
                    menu.inflate(R.menu.tracker_menu);
                    menu.show();
                    menu.setOnMenuItemClickListener(ViewHolder.this);
                }
            });
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return false;

            int id = item.getItemId();
            if (id == R.id.remove) {
                listener.onRemoveTrackerClicked(trackerStats[position]);
                return true;
            } else if (id == R.id.edit) {
                listener.onEditTrackerUrlClicked(trackerStats[position]);
                return true;
            } else if (id == R.id.copy) {
                listener.onCopyTrackerUrlClicked(trackerStats[position]);
                return true;
            }
            return false;
        }
    }

    public interface TrackerActionListener {

        void onRemoveTrackerClicked(TrackerStats tracker);

        void onEditTrackerUrlClicked(TrackerStats tracker);

        void onCopyTrackerUrlClicked(TrackerStats tracker);
    }
}
