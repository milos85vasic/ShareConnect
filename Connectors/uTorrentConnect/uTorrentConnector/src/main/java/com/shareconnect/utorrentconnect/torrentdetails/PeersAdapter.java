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
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.databinding.PeerItemLayoutBinding;
import com.shareconnect.utorrentconnect.model.json.Peer;
import com.shareconnect.utorrentconnect.sorting.PeersSortedBy;
import com.shareconnect.utorrentconnect.sorting.SortOrder;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class PeersAdapter extends RecyclerView.Adapter<PeersAdapter.ViewHolder> {

    private Peer[] peers = {};
    private Comparator<Peer> comparator;
    private SortOrder order;

    public PeersAdapter() {
        setHasStableIds(true);
    }

    public void setPeers(@NonNull Peer[] peers) {
        this.peers = peers;
        sort();
        notifyDataSetChanged();
    }

    public void setSorting(PeersSortedBy sorting, SortOrder order) {
        comparator = sorting.comparator;
        this.order = order;
        sort();
        notifyDataSetChanged();
    }

    private void sort() {
        if (ArrayUtils.isEmpty(peers) || comparator == null) return;
        Arrays.sort(peers, order == SortOrder.ASCENDING ? comparator : Collections.reverseOrder(comparator));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PeerItemLayoutBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.peer_item_layout, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.binding.setPeer(peers[position]);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return peers.length;
    }

    @Override
    public long getItemId(int position) {
        return peers[position].address.hashCode();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private PeerItemLayoutBinding binding;

        public ViewHolder(PeerItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // this listener is required for view selection background
                }
            });
        }
    }
}
