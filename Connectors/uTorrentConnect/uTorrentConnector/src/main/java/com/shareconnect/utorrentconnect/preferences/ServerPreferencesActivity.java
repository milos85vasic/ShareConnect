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


package com.shareconnect.utorrentconnect.preferences;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import com.shareconnect.utorrentconnect.ProgressbarFragment;
import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.uTorrentRemote;
import com.shareconnect.utorrentconnect.model.json.ServerSettings;
import com.shareconnect.utorrentconnect.torrentdetails.SaveChangesDialogFragment;
import com.shareconnect.utorrentconnect.transport.BaseSpiceActivity;
import com.shareconnect.utorrentconnect.transport.request.SessionGetRequest;
import com.shareconnect.utorrentconnect.transport.request.SessionSetRequest;

public class ServerPreferencesActivity extends BaseSpiceActivity implements SaveChangesDialogFragment.SaveDiscardListener {

    private static final String TAG = ServerPreferencesActivity.class.getSimpleName();
    private static final String TAG_SERVER_PREFERENCES_FRAGMENT = "tag_server_preferences_fragment";
    private static final String TAG_SAVE_CHANGES_DIALOG = "tag_save_changes_dialog";

    private static final String KEY_SAVE_CHANGES_REQUEST = "key_save_changes_request";

    private SessionSetRequest saveChangesRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_preferences_activity);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
        }

        if (savedInstanceState == null) {
            showProgressbarFragment();

            getTransportManager().doRequest(new SessionGetRequest(), new RequestListener<ServerSettings>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    Log.e(TAG, "Failed to obtain server settings");
                }

                @Override
                public void onRequestSuccess(ServerSettings serverSettings) {
                    ((uTorrentRemote) getApplication()).setSpeedLimitEnabled(serverSettings.isAltSpeedLimitEnabled());
                    showPreferencesFragment(serverSettings);
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_SAVE_CHANGES_REQUEST, saveChangesRequest);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        saveChangesRequest = savedInstanceState.getParcelable(KEY_SAVE_CHANGES_REQUEST);
    }

    private void showProgressbarFragment() {
        FragmentManager fm = getSupportFragmentManager();

        ServerPreferencesFragment preferencesFragment = (ServerPreferencesFragment)
                fm.findFragmentById(R.id.server_preferences_fragment_container);

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        if (preferencesFragment != null) ft.remove(preferencesFragment);
        ft.add(R.id.progress_bar_fragment_container, new ProgressbarFragment());
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        ServerPreferencesFragment fragment = (ServerPreferencesFragment)
                getSupportFragmentManager().findFragmentByTag(TAG_SERVER_PREFERENCES_FRAGMENT);
        if (fragment == null) {
            super.onBackPressed();
            return;
        }

        SessionSetRequest.Builder requestBuilder = fragment.getPreferencesRequestBuilder();
        if (requestBuilder.isChanged()) {
            saveChangesRequest = requestBuilder.build();
            new SaveChangesDialogFragment().show(getFragmentManager(), TAG_SAVE_CHANGES_DIALOG);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onSavePressed() {
        getTransportManager().doRequest(saveChangesRequest, null);
        super.onBackPressed();
    }

    @Override
    public void onDiscardPressed() {
        super.onBackPressed();
    }

    private void showPreferencesFragment(ServerSettings settings) {
        FragmentManager fm = getSupportFragmentManager();

        ProgressbarFragment progressbarFragment = (ProgressbarFragment)
                fm.findFragmentById(R.id.progress_bar_fragment_container);

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (progressbarFragment != null) ft.remove(progressbarFragment);
        ServerPreferencesFragment fragment = new ServerPreferencesFragment();
        Bundle args = new Bundle();
        args.putParcelable(ServerPreferencesFragment.KEY_SERVER_SETTINGS, settings);
        fragment.setArguments(args);
        ft.add(R.id.server_preferences_fragment_container, fragment, TAG_SERVER_PREFERENCES_FRAGMENT);
        ft.commit();
    }
}
