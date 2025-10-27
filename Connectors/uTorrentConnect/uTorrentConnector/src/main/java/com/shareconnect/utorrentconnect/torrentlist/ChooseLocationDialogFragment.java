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


package com.shareconnect.utorrentconnect.torrentlist;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.uTorrentRemote;
import com.shareconnect.utorrentconnect.databinding.SetLocationDialogBinding;
import com.shareconnect.utorrentconnect.model.json.FreeSpace;
import com.shareconnect.utorrentconnect.transport.BaseSpiceActivity;
import com.shareconnect.utorrentconnect.transport.TransportManager;
import com.shareconnect.utorrentconnect.transport.request.FreeSpaceRequest;
import com.shareconnect.utorrentconnect.transport.request.ResponseFailureException;
import com.shareconnect.utorrentconnect.utils.SimpleTextWatcher;
import com.shareconnect.utorrentconnect.utils.TextUtils;

public class ChooseLocationDialogFragment extends DialogFragment {

    public static final String ARG_INITIAL_LOCATION = "arg_initial_location";

    private OnLocationSelectedListener listener;
    private SetLocationDialogBinding binding;
    private FreeSpaceRequest runningFreeSpaceRequest;
    private String initialLocation;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof OnLocationSelectedListener) {
            listener = (OnLocationSelectedListener) targetFragment;
        } else {
            Activity activity = getActivity();
            if (activity instanceof OnLocationSelectedListener) {
                listener = (OnLocationSelectedListener) activity;
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            initialLocation = args.getString(ARG_INITIAL_LOCATION);
        }
        if (initialLocation == null) {
            initialLocation = uTorrentRemote.getInstance().defaultDownloadDir;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.set_location_dialog, null, false);
        binding.locationEdit.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updateFreeSpace(s.toString());
            }
        });
        binding.locationEdit.setText(initialLocation);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.choose_location);
        builder.setView(binding.getRoot());
        builder.setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listener != null) {
                        listener.onLocationSelected(binding.locationEdit.getText().toString(),
                                binding.moveDataCheckbox.isChecked());
                    }
                }
            });
        builder.setNegativeButton(android.R.string.cancel, null);

        return builder.create();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (runningFreeSpaceRequest != null) runningFreeSpaceRequest.cancel();
    }

    private void updateFreeSpace(String path) {
        if (runningFreeSpaceRequest != null) runningFreeSpaceRequest.cancel();
        binding.setLoadingInProgress(true);
        runningFreeSpaceRequest = new FreeSpaceRequest(path);

        getTransportManager().doRequest(runningFreeSpaceRequest, new RequestListener<FreeSpace>() {
            @Override
            public void onRequestSuccess(FreeSpace freeSpace) {
                runningFreeSpaceRequest = null;
                binding.setLoadingInProgress(false);
                binding.freeSpaceText.setText(getString(R.string.free_space,
                        TextUtils.displayableSize(freeSpace.getSizeInBytes())));
            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                if (spiceException.getCause() instanceof ResponseFailureException) {
                    runningFreeSpaceRequest = null;
                    binding.setLoadingInProgress(false);
                    String failureMessage = ((ResponseFailureException) spiceException.getCause()).getFailureMessage();
                    binding.freeSpaceText.setText(failureMessage);
                } else if (!(spiceException instanceof RequestCancelledException)) { // Retry if not canceled
                    getTransportManager().doRequest(runningFreeSpaceRequest, this);
                }
            }
        });
    }

    private TransportManager getTransportManager() {
        return ((BaseSpiceActivity) getActivity()).getTransportManager();
    }

    public interface OnLocationSelectedListener {
        void onLocationSelected(String path, boolean moveData);
    }
}
