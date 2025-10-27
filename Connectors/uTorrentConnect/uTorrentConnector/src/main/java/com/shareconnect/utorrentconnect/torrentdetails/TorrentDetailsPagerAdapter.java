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
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.model.json.Torrent;
import com.shareconnect.utorrentconnect.model.json.TorrentInfo;

public class TorrentDetailsPagerAdapter extends FragmentStatePagerAdapter {

    private Class<?>[] fragmentsClasses = {
            TorrentInfoPageFragment.class,
            FilesPageFragment.class,
            TrackersPageFragment.class,
            PeersPageFragment.class,
            OptionsPageFragment.class,
    };

    private int[] pageTitles = {
            R.string.info,
            R.string.files,
            R.string.trackers,
            R.string.peers,
            R.string.options
    };

    private Context context;
    private Torrent torrent;
    private TorrentInfo torrentInfo;

    public TorrentDetailsPagerAdapter(Context context, FragmentManager fragmentManager, @NonNull Torrent torrent) {
        super(fragmentManager);
        this.context = context;
        this.torrent = torrent;
    }

    public void setTorrentInfo(TorrentInfo torrentInfo) {
        this.torrentInfo = torrentInfo;
    }

    @Override
    public BasePageFragment getItem(int position) {
        BasePageFragment fragment = (BasePageFragment) Fragment.instantiate(context, fragmentsClasses[position].getName());
        fragment.setTorrent(torrent);
        if (torrentInfo != null) fragment.setTorrentInfo(torrentInfo);
        return fragment;
    }

    public BasePageFragment findFragment(FragmentManager fragmentManager, int position) {
        Class fragmentClass = fragmentsClasses[position];
        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragmentClass.isInstance(fragment)) {
                return (BasePageFragment) fragment;
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return fragmentsClasses.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(pageTitles[position]);
    }
}
