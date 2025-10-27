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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.uTorrentRemote;
import com.shareconnect.utorrentconnect.databinding.TorrentDetailsLayoutBinding;
import com.shareconnect.utorrentconnect.model.json.Torrent;
import com.shareconnect.utorrentconnect.model.json.TorrentInfo;
import com.shareconnect.utorrentconnect.model.json.Torrents;
import com.shareconnect.utorrentconnect.torrentlist.ChooseLocationDialogFragment;
import com.shareconnect.utorrentconnect.torrentlist.RemoveTorrentsDialogFragment;
import com.shareconnect.utorrentconnect.torrentlist.RenameDialogFragment;
import com.shareconnect.utorrentconnect.transport.BaseSpiceActivity;
import com.shareconnect.utorrentconnect.transport.request.ReannounceTorrentRequest;
import com.shareconnect.utorrentconnect.transport.request.RenameRequest;
import com.shareconnect.utorrentconnect.transport.request.SetLocationRequest;
import com.shareconnect.utorrentconnect.transport.request.StartTorrentRequest;
import com.shareconnect.utorrentconnect.transport.request.StopTorrentRequest;
import com.shareconnect.utorrentconnect.transport.request.TorrentGetRequest;
import com.shareconnect.utorrentconnect.transport.request.TorrentInfoGetRequest;
import com.shareconnect.utorrentconnect.transport.request.TorrentRemoveRequest;
import com.shareconnect.utorrentconnect.transport.request.TorrentSetRequest;
import com.shareconnect.utorrentconnect.transport.request.VerifyTorrentRequest;

import java.util.LinkedList;
import java.util.List;

public class TorrentDetailsActivity extends BaseSpiceActivity implements SaveChangesDialogFragment.SaveDiscardListener,
        RemoveTorrentsDialogFragment.OnRemoveTorrentSelectionListener, ChooseLocationDialogFragment.OnLocationSelectedListener,
        TorrentInfoUpdater.OnTorrentInfoUpdatedListener, SwipeRefreshLayout.OnRefreshListener, RenameDialogFragment.OnNameSelectedListener {

    private static final String TAG = TorrentDetailsActivity.class.getSimpleName();

    public static final String EXTRA_TORRENT = "extra_key_torrent";

    private static final String TAG_SAVE_CHANGES_DIALOG = "tag_save_changes_dialog";
    private static final String TAG_CHOOSE_LOCATION_DIALOG = "tag_choose_location_dialog";

    private static final String KEY_OPTIONS_CHANGE_REQUEST = "key_options_request";
    private static final String KEY_LAST_PAGE_POSITION = "key_last_position";

    private static final String RENAME_TORRENT_FRAGMENT_TAG = "rename_torrent_fragment_tag";

    private Torrent torrent;
    private TorrentInfo torrentInfo;
    private SparseArray<TorrentSetRequest> saveChangesRequests = new SparseArray<>();
    private final List<OnDataAvailableListener<TorrentInfo>> torrentInfoListeners = new LinkedList<>();
    private final List<OnActivityExitingListener<TorrentSetRequest.Builder>> activityExitingListeners = new LinkedList<>();
    private TorrentDetailsPagerAdapter pagerAdapter;
    private MenuItem setLocationMenuItem;
    private MenuItem shareMagnetMenuItem;
    private TorrentInfoUpdater torrentInfoUpdater;
    private TorrentDetailsLayoutBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.torrent_details_layout);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TorrentDetailsActivity.this);

        torrent = getIntent().getParcelableExtra(EXTRA_TORRENT);
        setupPager();

        binding.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                sharedPreferences.edit().putInt(KEY_LAST_PAGE_POSITION, position).apply();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        if (setLocationMenuItem != null) {
            setLocationMenuItem.setEnabled(torrentInfo != null);
        }
        if (shareMagnetMenuItem != null) {
            shareMagnetMenuItem.setEnabled(torrentInfo != null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        torrentInfoUpdater.start(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        torrentInfoUpdater.stop();
    }

    @Override
    public void onTorrentInfoUpdated(TorrentInfo torrentInfo) {
        // TorrentInfo may be empty if torrent is removed after request was sent.
        // Show content only if TorrentInfo contain files data.
        if (torrentInfo.getFiles() != null) {
            TorrentDetailsActivity.this.torrentInfo = torrentInfo;
            pagerAdapter.setTorrentInfo(torrentInfo);
            notifyTorrentInfoListeners();
            if (setLocationMenuItem != null) {
                setLocationMenuItem.setEnabled(true);
            }
            if (shareMagnetMenuItem != null) {
                shareMagnetMenuItem.setEnabled(true);
            }
        } else {
            Log.e(TAG, "Empty TorrentInfo");
            showErrorAndExit();
        }
    }

    private void showErrorAndExit() {
        Toast.makeText(TorrentDetailsActivity.this, R.string.error_retrieve_torrent_details, Toast.LENGTH_LONG).show();
        onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSparseParcelableArray(KEY_OPTIONS_CHANGE_REQUEST, saveChangesRequests);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        saveChangesRequests = savedInstanceState.getSparseParcelableArray(KEY_OPTIONS_CHANGE_REQUEST);
        super.onRestoreInstanceState(savedInstanceState);
    }

    public TorrentInfo getTorrentInfo() {
        return torrentInfo;
    }

    public void addTorrentInfoListener(OnDataAvailableListener<TorrentInfo> listener) {
        if (!torrentInfoListeners.contains(listener)) {
            torrentInfoListeners.add(listener);
        }
    }

    public void removeTorrentInfoListener(OnDataAvailableListener<TorrentInfo> listener) {
        torrentInfoListeners.remove(listener);
    }

    private void notifyTorrentInfoListeners() {
        for (OnDataAvailableListener<TorrentInfo> listener : torrentInfoListeners) {
            listener.onDataAvailable(torrentInfo);
        }
    }

    public void addOnActivityExitingListener(OnActivityExitingListener<TorrentSetRequest.Builder> listener) {
        if (!activityExitingListeners.contains(listener)) {
            activityExitingListeners.add(listener);
        }
    }

    public void removeOnActivityExitingListener(OnActivityExitingListener<TorrentSetRequest.Builder> listener) {
        activityExitingListeners.remove(listener);
    }

    public void addSaveChangesRequest(TorrentSetRequest.Builder requestBuilder) {
        saveChangesRequests.put(requestBuilder.getTorrentId(), requestBuilder.build());
    }

    private void setupPager() {
        pagerAdapter = new TorrentDetailsPagerAdapter(this, getSupportFragmentManager(), torrent);

        boolean restartUpdater = false;
        if (torrentInfoUpdater != null) {
            torrentInfoUpdater.stop();
            restartUpdater = true;
        }
        torrentInfoUpdater = new TorrentInfoUpdater(getTransportManager(), torrent.getId(),
                1000L * uTorrentRemote.getInstance().getUpdateInterval());
        if (restartUpdater) torrentInfoUpdater.start(this);

        if (torrentInfo != null) pagerAdapter.setTorrentInfo(torrentInfo);

        setupActionBar();

        binding.pager.setAdapter(pagerAdapter);

        int lastPagePosition = sharedPreferences.getInt(KEY_LAST_PAGE_POSITION, 0);
        binding.pager.setCurrentItem(lastPagePosition < pagerAdapter.getCount() ? lastPagePosition : 0);
    }

    private void setupActionBar() {
        binding.toolbar.setSubtitle(torrent.getName());
        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return handleExit();
    }

    @Override
    public void onBackPressed() {
        boolean handled = handleBackPressByFragments();
        if (handled) return;
        handleExit();
    }

    private boolean handleExit() {
        if (torrentInfo == null) {
            finish();
            return true;
        }

        for (OnActivityExitingListener<TorrentSetRequest.Builder> listener : activityExitingListeners) {
            TorrentSetRequest.Builder saveChangesRequestBuilder = listener.onActivityExiting();
            if (saveChangesRequestBuilder != null && saveChangesRequestBuilder.isChanged()) {
                addSaveChangesRequest(saveChangesRequestBuilder);
            }
        }

        if (saveChangesRequests.size() > 0) {
            new SaveChangesDialogFragment().show(getFragmentManager(), TAG_SAVE_CHANGES_DIALOG);
        } else {
            finish();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.torrent_details_actions_menu, menu);

        setLocationMenuItem = menu.findItem(R.id.action_set_location);
        setLocationMenuItem.setEnabled(torrentInfo != null);

        shareMagnetMenuItem = menu.findItem(R.id.action_share_magnet);
        shareMagnetMenuItem.setEnabled(torrentInfo != null);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_remove_torrents) {
            RemoveTorrentsDialogFragment.newInstance(torrent.getId())
                    .show(getSupportFragmentManager(), RemoveTorrentsDialogFragment.TAG_REMOVE_TORRENTS_DIALOG);
            return true;
        } else if (id == R.id.action_pause) {
            getTransportManager().doRequest(new StopTorrentRequest(torrent.getId()), null);
            return true;
        } else if (id == R.id.action_start) {
            getTransportManager().doRequest(new StartTorrentRequest(torrent.getId()), null);
            return true;
        } else if (id == R.id.action_start_now) {
            getTransportManager().doRequest(new StartTorrentRequest(new int[] { torrent.getId() }, true), null);
            return true;
        } else if (id == R.id.action_rename) {
            RenameDialogFragment dialogFragment = RenameDialogFragment.newInstance(torrent.getId(), torrent.getName(), torrent.getName());
            dialogFragment.show(getSupportFragmentManager(), RENAME_TORRENT_FRAGMENT_TAG);
            return true;
        } else if (id == R.id.action_set_location) {
            ChooseLocationDialogFragment dialog = new ChooseLocationDialogFragment();
            Bundle args = new Bundle();
            args.putString(ChooseLocationDialogFragment.ARG_INITIAL_LOCATION, torrentInfo.getDownloadDir());
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), TAG_CHOOSE_LOCATION_DIALOG);
            return true;
        } else if (id == R.id.action_verify) {
            getTransportManager().doRequest(new VerifyTorrentRequest(torrent.getId()), null);
            return true;
        } else if (id == R.id.action_reannounce) {
            getTransportManager().doRequest(new ReannounceTorrentRequest(torrent.getId()), null);
            return true;
        } else if (id == R.id.action_share_magnet) {
            shareMagnetLink();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSavePressed() {
        for (int i=0; i<saveChangesRequests.size(); i++) {
            TorrentSetRequest request = saveChangesRequests.valueAt(i);
            getTransportManager().doRequest(request, null);
        }
        super.onBackPressed();
    }

    @Override
    public void onDiscardPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRemoveTorrentsSelected(int[] torrentsToRemove, boolean removeData) {
        getTransportManager().doRequest(new TorrentRemoveRequest(torrentsToRemove, removeData), new RequestListener<Void>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Toast.makeText(TorrentDetailsActivity.this, R.string.remove_failed, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to remove torrent", spiceException);
            }

            @Override
            public void onRequestSuccess(Void aVoid) {
                TorrentDetailsActivity.super.onBackPressed();
            }
        });
    }

    @Override
    public void onLocationSelected(String path, boolean moveData) {
        getTransportManager().doRequest(new SetLocationRequest(path, moveData, torrent.getId()), new RequestListener<Void>() {
            @Override
            public void onRequestSuccess(Void aVoid) {
                torrentInfoUpdater.updateNow(TorrentDetailsActivity.this);
            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e(TAG, "Failed to set location", spiceException);
            }
        });
    }

    @Override
    public void onNameSelected(final int torrentId, String path, String name) {
        getTransportManager().doRequest(new RenameRequest(torrentId, path, name), new RequestListener<Void>() {
            @Override
            public void onRequestSuccess(Void aVoid) {
                updateTorrentAndTorrentInfo();
            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e(TAG, "Failed to rename torrent", spiceException);
            }

            private void updateTorrentAndTorrentInfo() {
                getTransportManager().doRequest(new TorrentGetRequest(torrentId), new RequestListener<Torrents>() {
                    @Override
                    public void onRequestSuccess(Torrents torrents) {
                        if (torrents.size() != 1) {
                            Log.e(TAG, "Wrong number of torrents");
                            return;
                        }

                        updateTorrentInfo(torrents.get(0));
                    }

                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        Log.e(TAG, "Failed to reload torrent", spiceException);
                    }
                });
            }

            private void updateTorrentInfo(final Torrent torrent) {
                getTransportManager().doRequest(new TorrentInfoGetRequest(torrent.getId()), new RequestListener<TorrentInfo>() {
                    @Override
                    public void onRequestSuccess(TorrentInfo torrentInfo) {
                        onTorrentInfoUpdated(torrentInfo);
                        TorrentDetailsActivity.this.torrent = torrent;
                        getIntent().putExtra(EXTRA_TORRENT, torrent);
                        setupPager();
                    }

                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        Log.e(TAG, "Failed to reload torrent", spiceException);
                    }
                });
            }
        });
    }

    @Override
    public void onRefresh() {
        torrentInfoUpdater.updateNow(this);
    }

    private void shareMagnetLink() {
        if (torrentInfo == null) return;

        String name = torrent.getName();
        String magnetLink = torrentInfo.getMagnetLink();
        String text = String.format("%s \n\n%s\n", name, magnetLink);

        Intent intent = new Intent()
                .setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, text)
                .setType("text/plain");

        startActivity(Intent.createChooser(intent, null));
    }
}
