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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.model.json.ServerSettings;
import com.shareconnect.utorrentconnect.torrentdetails.BandwidthLimitFragment;
import com.shareconnect.utorrentconnect.transport.BaseSpiceActivity;
import com.shareconnect.utorrentconnect.transport.TransportManager;
import com.shareconnect.utorrentconnect.transport.request.SessionGetRequest;
import com.shareconnect.utorrentconnect.transport.request.SessionSetRequest;

public class ServerPreferencesFragment extends Fragment {

    private static final String TAG = ServerPreferencesFragment.class.getSimpleName();

    public static final String KEY_SERVER_SETTINGS = "extra_server_preferences";

    private ServerSettings serverSettings;

    private BandwidthLimitFragment globalBandwidthLimitFragment;
    private BandwidthLimitFragment altBandwidthLimitFragment;
    private TextView altLimitHeader;
    private TransportManager transportManager;
    private Menu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        if (activity instanceof BaseSpiceActivity) {
            transportManager = ((BaseSpiceActivity) activity).getTransportManager();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.server_preferences_fragment, container, false);

        globalBandwidthLimitFragment = (BandwidthLimitFragment)
                getChildFragmentManager().findFragmentById(R.id.global_bandwidth_limit_fragment);
        altBandwidthLimitFragment = (BandwidthLimitFragment)
                getChildFragmentManager().findFragmentById(R.id.alt_bandwidth_limit_fragment);
        altLimitHeader = view.findViewById(R.id.turtle_limit_header_text);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            serverSettings = args.getParcelable(KEY_SERVER_SETTINGS);
        }
        if (savedInstanceState != null) {
            serverSettings = savedInstanceState.getParcelable(KEY_SERVER_SETTINGS);
        }
        updateUi();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_SERVER_SETTINGS, serverSettings);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.server_preferences_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            SessionSetRequest.Builder builder = getPreferencesRequestBuilder();
            if (builder.isChanged()) {
                sendUpdateOptionsRequest(builder.build());
            }
            return true;
        }
        return false;
    }

    public SessionSetRequest.Builder getPreferencesRequestBuilder() {

        SessionSetRequest.Builder builder = SessionSetRequest.builder();

        boolean downloadLimited = globalBandwidthLimitFragment.isDownloadLimited();
        if (serverSettings.isSpeedLimitDownEnabled() != downloadLimited) {
            builder.setSpeedLimitDownEnabled(downloadLimited);
        }

        long downloadLimit = globalBandwidthLimitFragment.getDownloadLimit();
        if (serverSettings.getSpeedLimitDown() != downloadLimit) {
            builder.setSpeedLimitDown(downloadLimit);
        }

        boolean uploadLimited = globalBandwidthLimitFragment.isUploadLimited();
        if (serverSettings.isSpeedLimitUpEnabled() != uploadLimited) {
            builder.setSpeedLimitUpEnabled(uploadLimited);
        }

        long uploadLimit = globalBandwidthLimitFragment.getUploadLimit();
        if (serverSettings.getSpeedLimitUp() != uploadLimit) {
            builder.setSpeedLimitUp(uploadLimit);
        }

        long altDownloadLimit = altBandwidthLimitFragment.getDownloadLimit();
        if (serverSettings.getAltSpeedLimitDown() != altDownloadLimit) {
            builder.setAltSpeedLimitDown(altDownloadLimit);
        }

        long altUploadLimit = altBandwidthLimitFragment.getUploadLimit();
        if (serverSettings.getAltSpeedLimitUp() != altUploadLimit) {
            builder.setAltSpeedLimitUp(altUploadLimit);
        }

        return builder;
    }

    private void updateUi() {

        if (serverSettings == null) {
            throw new IllegalStateException("No server preferences set." +
                    " Ensure that setArguments(Bundle) called with bundle containing server preferences.");
        }

        View view = getView();
        if (view == null) {
            Log.e(TAG, "trying to update fragment before onCreateView()");
            return;
        }

        int globalLimitDown = serverSettings.getSpeedLimitDown();
        boolean isGlobalLimitDownEnabled = serverSettings.isSpeedLimitDownEnabled();
        int globalLimitUp = serverSettings.getSpeedLimitUp();
        boolean isGlobalLimitUpEnabled = serverSettings.isSpeedLimitUpEnabled();
        int altLimitDown = serverSettings.getAltSpeedLimitDown();
        int altLimitUp = serverSettings.getAltSpeedLimitUp();
        boolean isAltLimitEnabled = serverSettings.isAltSpeedLimitEnabled();

        globalBandwidthLimitFragment.setDownloadLimited(isGlobalLimitDownEnabled);
        globalBandwidthLimitFragment.setDownloadLimit(globalLimitDown);
        globalBandwidthLimitFragment.setUploadLimited(isGlobalLimitUpEnabled);
        globalBandwidthLimitFragment.setUploadLimit(globalLimitUp);

        altBandwidthLimitFragment.setDownloadLimit(altLimitDown);
        altBandwidthLimitFragment.setUploadLimit(altLimitUp);

        int turtleImage = isAltLimitEnabled ? R.drawable.ic_turtle_active : R.drawable.ic_turtle_black;
        altLimitHeader.setCompoundDrawablesWithIntrinsicBounds(turtleImage, 0, 0, 0);
    }

    private void sendUpdateOptionsRequest(SessionSetRequest request) {
        if (transportManager == null)
            throw new RuntimeException("ServerPreferencesFragment should be used with BaseSpiceActivity.");

        saveStarted();

        transportManager.doRequest(request, new RequestListener<Void>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Toast.makeText(getActivity(), getString(R.string.preferences_update_failed), Toast.LENGTH_LONG).show();
                saveFinished();
            }

            @Override
            public void onRequestSuccess(Void aVoid) {
                sendPreferencesUpdateRequest();
            }
        });
    }

    private void sendPreferencesUpdateRequest() {
        transportManager.doRequest(new SessionGetRequest(), new RequestListener<ServerSettings>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Toast.makeText(getActivity(), getString(R.string.preferences_update_failed), Toast.LENGTH_LONG).show();
                saveFinished();
            }

            @Override
            public void onRequestSuccess(ServerSettings settings) {
                serverSettings = settings;
                updateUi();
                Toast.makeText(getActivity(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
                saveFinished();
            }
        });
    }

    private void saveStarted() {
        menu.findItem(R.id.action_save).setEnabled(false);
    }

    private void saveFinished() {
        menu.findItem(R.id.action_save).setEnabled(true);
    }
}
