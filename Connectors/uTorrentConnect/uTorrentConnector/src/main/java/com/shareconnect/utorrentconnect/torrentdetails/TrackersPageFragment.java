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

import static com.shareconnect.utorrentconnect.transport.SpiceExceptionExtensionsKt.getResponseFailureMessage;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.uTorrentRemote;
import com.shareconnect.utorrentconnect.databinding.TorrentDetailsTrackersPageFragmentBinding;
import com.shareconnect.utorrentconnect.model.json.TorrentInfo;
import com.shareconnect.utorrentconnect.model.json.TrackerStats;
import com.shareconnect.utorrentconnect.sorting.SortOrder;
import com.shareconnect.utorrentconnect.sorting.TrackersSortedBy;
import com.shareconnect.utorrentconnect.transport.BaseSpiceActivity;
import com.shareconnect.utorrentconnect.transport.TransportManager;
import com.shareconnect.utorrentconnect.transport.request.TrackerAddRequest;
import com.shareconnect.utorrentconnect.transport.request.TrackerRemoveRequest;
import com.shareconnect.utorrentconnect.transport.request.TrackerReplaceRequest;
import com.shareconnect.utorrentconnect.utils.DividerItemDecoration;
import com.shareconnect.utorrentconnect.utils.MetricsUtils;
import com.shareconnect.utorrentconnect.utils.Size;

import org.apache.commons.lang3.StringUtils;

public class TrackersPageFragment extends BasePageFragment implements TrackersAdapter.TrackerActionListener,
        TrackerUrlDialog.OnTrackerUrlEnteredListener {

    private static final String TAG_EDIT_URL_DIALOG = "tag_edit_url_dialog";
    private static final String KEY_SORT_BY = "key_sort_by";
    private static final String KEY_SORT_ORDER = "key_sort_order";

    private final TrackersAdapter adapter = new TrackersAdapter(this);
    private TorrentDetailsTrackersPageFragmentBinding binding;
    private boolean viewCreated;
    private ClipboardManager clipboardManager;
    private TransportManager transportManager;
    private SharedPreferences preferences;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        if (activity instanceof BaseSpiceActivity) {
            transportManager = ((BaseSpiceActivity) activity).getTransportManager();
        } else {
            throw new IllegalStateException("Fragment must be used with BaseSpiceActivity");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(
                inflater, R.layout.torrent_details_trackers_page_fragment, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(container.getContext()));
        binding.recyclerView.setItemAnimator(null);
        binding.recyclerView.setAdapter(adapter);

        if (getActivity() instanceof SwipeRefreshLayout.OnRefreshListener) {
            binding.swipeRefresh.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) getActivity());
        }

        TorrentInfo torrentInfo = getTorrentInfo();
        if (torrentInfo != null) {
            TrackerStats[] trackerStats = torrentInfo.getTrackerStats();
            adapter.setTrackerStats(trackerStats);
            binding.emptyText.setVisibility(trackerStats.length > 0 ? View.GONE : View.VISIBLE);
        } else {
            binding.swipeRefresh.setRefreshing(true);
        }

        adapter.setSorting(currentSorting(), currentSortOrder());

        viewCreated = true;

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        uTorrentRemote.getInstance().getAnalytics().logScreenView(
                "Trackers page",
                TrackersPageFragment.class
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewCreated = false;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.torrent_trackers_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            showEditTrackerUrlDialog(null);
            return true;
        } else if (id == R.id.action_sort_trackers) {
            showSortingList();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showSortingList() {
        ListPopupWindow popup = new ListPopupWindow(requireContext());
        popup.setModal(true);

        final TrackersSortingListAdapter sortingAdapter = new TrackersSortingListAdapter();
        final TrackersSortedBy sortedBy = currentSorting();
        SortOrder sortOrder = currentSortOrder();
        sortingAdapter.setCurrentSorting(sortedBy, sortOrder);
        popup.setAdapter(sortingAdapter);
        popup.setOnItemClickListener((parent, view, position, id) -> {
            TrackersSortedBy sorting = sortingAdapter.getItem(position);
            SortOrder order = SortOrder.ASCENDING;
            if (sorting == currentSorting()) {
                order = currentSortOrder().reversed();
            }
            setCurrentSorting(sorting, order);
            sortingAdapter.setCurrentSorting(sorting, order);
            adapter.setSorting(sorting, order);
        });

        popup.setAnchorView(binding.getRoot());
        Size popupSize = MetricsUtils.measurePopupSize(getContext(), sortingAdapter);
        popup.setContentWidth(popupSize.width);
        popup.setHeight(popupSize.height);
        popup.setHorizontalOffset(binding.getRoot().getWidth() - popupSize.width
                - getResources().getDimensionPixelOffset(R.dimen.sort_popup_offset_horizontal));
        popup.setVerticalOffset(-getResources().getDimensionPixelOffset(R.dimen.sort_popup_offset_vertical));
        popup.show();
    }

    private TrackersSortedBy currentSorting() {
        try {
            return TrackersSortedBy.valueOf(preferences.getString(KEY_SORT_BY, TrackersSortedBy.TIERS.toString()));
        } catch (IllegalArgumentException e) {
            // Unknown sorting, return default
            return TrackersSortedBy.TIERS;
        }
    }

    private void setCurrentSorting(TrackersSortedBy sorting, SortOrder sortOrder) {
        preferences.edit()
                .putString(KEY_SORT_BY, sorting.toString())
                .putString(KEY_SORT_ORDER, sortOrder.toString())
                .apply();
    }

    private SortOrder currentSortOrder() {
        try {
            return SortOrder.valueOf(preferences.getString(KEY_SORT_ORDER, SortOrder.ASCENDING.toString()));
        } catch (IllegalArgumentException e) {
            // Unknown order, return default
            return SortOrder.ASCENDING;
        }
    }

    @Override
    public void setTorrentInfo(TorrentInfo torrentInfo) {
        super.setTorrentInfo(torrentInfo);
        if (viewCreated) {
            TrackerStats[] trackerStats = torrentInfo.getTrackerStats();
            adapter.setTrackerStats(trackerStats);
            binding.emptyText.setVisibility(trackerStats.length > 0 ? View.GONE : View.VISIBLE);
            binding.swipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onRemoveTrackerClicked(final TrackerStats tracker) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.trackers_remove_confirmation_title)
                .setMessage(R.string.trackers_remove_confirmation_message)
                .setPositiveButton(R.string.remove, (dialog, which) -> removeTracker(tracker.id))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onEditTrackerUrlClicked(TrackerStats tracker) {
        showEditTrackerUrlDialog(tracker);
    }

    @Override
    public void onCopyTrackerUrlClicked(TrackerStats tracker) {
        String url = StringUtils.isNotEmpty(tracker.announce) ? tracker.announce : tracker.host;
        ClipData clip = ClipData.newPlainText(url, url);
        clipboardManager().setPrimaryClip(clip);
        Toast.makeText(getContext(), getString(R.string.copied), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTrackerUrlEntered(@Nullable TrackerStats tracker, String url) {
        if (tracker == null) {
            addTracker(url);
        } else {
            editTracker(tracker, url);
        }
    }

    private void removeTracker(int trackerId) {
        binding.swipeRefresh.setRefreshing(true);
        transportManager.doRequest(new TrackerRemoveRequest(getTorrent().getId(), trackerId), new RequestListener<Void>() {
            @Override
            public void onRequestSuccess(Void aVoid) {
                binding.swipeRefresh.setRefreshing(false);
                refresh();
            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                binding.swipeRefresh.setRefreshing(false);
                refresh();
            }
        });
    }

    private void addTracker(String url) {
        binding.swipeRefresh.setRefreshing(true);
        transportManager.doRequest(new TrackerAddRequest(getTorrent().getId(), url), new RequestListener<>() {
            @Override
            public void onRequestSuccess(Void aVoid) {
                binding.swipeRefresh.setRefreshing(false);
                refresh();
            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                @Nullable final String responseFailureMessage = getResponseFailureMessage(spiceException);
                final String failureMessage;
                if (isNotBlank(responseFailureMessage)) {
                    failureMessage = responseFailureMessage;
                } else {
                    failureMessage = getString(R.string.trackers_failed_to_add_tracker);
                }
                Toast.makeText(requireContext(), failureMessage, Toast.LENGTH_LONG).show();
                binding.swipeRefresh.setRefreshing(false);
                refresh();
            }
        });
    }

    private void editTracker(@NonNull TrackerStats tracker, String url) {
        binding.swipeRefresh.setRefreshing(true);
        transportManager.doRequest(new TrackerReplaceRequest(getTorrent().getId(), tracker.id, url), new RequestListener<>() {
            @Override
            public void onRequestSuccess(Void aVoid) {
                binding.swipeRefresh.setRefreshing(false);
                refresh();
            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                @Nullable final String responseFailureMessage = getResponseFailureMessage(spiceException);
                final String failureMessage;
                if (isNotBlank(responseFailureMessage)) {
                    failureMessage = responseFailureMessage;
                } else {
                    failureMessage = getString(R.string.trackers_failed_to_edit_tracker);
                }
                Toast.makeText(requireContext(), failureMessage, Toast.LENGTH_LONG).show();
                binding.swipeRefresh.setRefreshing(false);
                refresh();
            }
        });
    }

    private void showEditTrackerUrlDialog(@Nullable TrackerStats tracker) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment prev = getParentFragmentManager().findFragmentByTag(TAG_EDIT_URL_DIALOG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        TrackerUrlDialog dialog = TrackerUrlDialog.newInstance(tracker);
        dialog.show(ft, TAG_EDIT_URL_DIALOG);
    }

    private ClipboardManager clipboardManager() {
        if (clipboardManager == null) {
            clipboardManager = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        }
        return clipboardManager;
    }

    private void refresh() {
        ((SwipeRefreshLayout.OnRefreshListener) requireActivity()).onRefresh();
    }
}
