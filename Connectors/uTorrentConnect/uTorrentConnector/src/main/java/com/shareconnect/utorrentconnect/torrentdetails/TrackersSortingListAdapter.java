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
