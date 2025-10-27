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

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shareconnect.utorrentconnect.BaseFragment;
import com.shareconnect.utorrentconnect.model.json.Torrent;
import com.shareconnect.utorrentconnect.model.json.TorrentInfo;

public abstract class BasePageFragment extends BaseFragment implements OnDataAvailableListener<TorrentInfo> {

    private static final String KEY_TORRENT = "key_torrent";

    private Torrent torrent;
    private TorrentInfo torrentInfo;

    @CallSuper
    public void setTorrent(Torrent torrent) {
        this.torrent = torrent;
    }

    protected Torrent getTorrent() {
        return torrent;
    }

    @CallSuper
    public void setTorrentInfo(TorrentInfo torrentInfo) {
        this.torrentInfo = torrentInfo;
    }

    protected TorrentInfo getTorrentInfo() {
        return torrentInfo;
    }

    @Override
    public void onDataAvailable(TorrentInfo data) {
        setTorrentInfo(data);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_TORRENT, torrent);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (getActivity() instanceof TorrentDetailsActivity) {
            TorrentDetailsActivity activity = (TorrentDetailsActivity) getActivity();
            torrentInfo = activity.getTorrentInfo();
            activity.addTorrentInfoListener(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (getActivity() instanceof TorrentDetailsActivity) {
            TorrentDetailsActivity activity = (TorrentDetailsActivity) getActivity();
            activity.removeTorrentInfoListener(this);
        }
    }

    @Nullable
    @CallSuper
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (torrent == null) torrent = savedInstanceState.getParcelable(KEY_TORRENT);
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
