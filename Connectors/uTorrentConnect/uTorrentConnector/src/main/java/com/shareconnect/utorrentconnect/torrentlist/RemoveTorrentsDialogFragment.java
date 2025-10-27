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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.shareconnect.utorrentconnect.R;

public class RemoveTorrentsDialogFragment extends DialogFragment {

    public static final String TAG_REMOVE_TORRENTS_DIALOG = "tag_remove_torrents_dialog";

    private static final String KEY_TORRENTS_TO_REMOVE = "key_torrents_to_remove";

    private OnRemoveTorrentSelectionListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.remove_selected_torrents);
        builder.setItems(R.array.remove_torrents_entries, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int[] torrentsToRemove = getArguments().getIntArray(KEY_TORRENTS_TO_REMOVE);
                boolean removeData = which == 1;
                if (removeData) {
                    askRemoveDataConfirmation(torrentsToRemove);
                } else {
                    listener.onRemoveTorrentsSelected(torrentsToRemove, false);
                }
            }
        });
        return builder.create();
    }

    private void askRemoveDataConfirmation(final int[] torrentsToRemove) {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.remove_data_confirmation)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onRemoveTorrentsSelected(torrentsToRemove, true);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnRemoveTorrentSelectionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + OnRemoveTorrentSelectionListener.class.getSimpleName());
        }
    }

    public static RemoveTorrentsDialogFragment newInstance(int... torrentsToRemove) {
        Bundle args = new Bundle();
        args.putIntArray(KEY_TORRENTS_TO_REMOVE, torrentsToRemove);
        RemoveTorrentsDialogFragment dialog = new RemoveTorrentsDialogFragment();
        dialog.setArguments(args);
        return dialog;
    }

    public interface OnRemoveTorrentSelectionListener {
        void onRemoveTorrentsSelected(int[] torrentsToRemove, boolean removeData);
    }
}
