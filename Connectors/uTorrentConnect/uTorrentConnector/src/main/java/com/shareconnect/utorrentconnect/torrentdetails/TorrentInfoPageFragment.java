package com.shareconnect.utorrentconnect.torrentdetails;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.uTorrentRemote;
import com.shareconnect.utorrentconnect.databinding.TorrentDetailsInfoPageFragmentBinding;
import com.shareconnect.utorrentconnect.model.json.Torrent;
import com.shareconnect.utorrentconnect.model.json.TorrentInfo;

import static com.google.common.base.Strings.nullToEmpty;

public class TorrentInfoPageFragment extends BasePageFragment {

    private TorrentDetailsInfoPageFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = DataBindingUtil.inflate(inflater, R.layout.torrent_details_info_page_fragment, container, false);

        binding.commentText.setOnLongClickListener(v -> {
            copyCommentToClipboard();
            return true;
        });

        binding.magnetText.setMovementMethod(LinkMovementMethod.getInstance());
        binding.magnetText.setOnLongClickListener(v -> {
            copyMagnetLinkToClipboard();
            return true;
        });

        binding.setTorrent(getTorrent());
        binding.setTorrentInfo(getTorrentInfo());

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        uTorrentRemote.getInstance().getAnalytics().logScreenView(
                "Torrent info page",
                TorrentInfoPageFragment.class
        );
    }

    @Override
    public void setTorrentInfo(TorrentInfo torrentInfo) {
        super.setTorrentInfo(torrentInfo);
        if (binding != null) {
            binding.setTorrentInfo(torrentInfo);
        }
    }

    @Override
    public void setTorrent(Torrent torrent) {
        super.setTorrent(torrent);
        if (binding != null) {
            binding.setTorrent(torrent);
        }
    }

    private void copyCommentToClipboard() {
        Torrent torrent = getTorrent();
        TorrentInfo torrentInfo = getTorrentInfo();
        if (torrent == null || torrentInfo == null) return;

        copyToClipboard(torrent.getName(), torrentInfo.getComment());
    }

    private void copyMagnetLinkToClipboard() {
        Torrent torrent = getTorrent();
        TorrentInfo torrentInfo = getTorrentInfo();
        if (torrent == null || torrentInfo == null) return;

        copyToClipboard(torrent.getName(), torrentInfo.getMagnetLink());
    }

    private void copyToClipboard(@Nullable String label, @Nullable String text) {
        ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText(nullToEmpty(label), nullToEmpty(text));
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), R.string.copied, Toast.LENGTH_SHORT).show();
        }
    }
}
