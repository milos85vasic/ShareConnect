package com.shareconnect.utorrentconnect.torrentdetails;

import android.content.Context;

import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.uTorrentRemote;
import com.shareconnect.utorrentconnect.model.json.Torrent;
import com.shareconnect.utorrentconnect.model.json.TorrentInfo;
import static com.shareconnect.utorrentconnect.utils.TextUtils.displayableSize;

public class InfoPageBindingUtils {

    public static String totalSize(Torrent torrent, TorrentInfo torrentInfo) {
        if (torrent == null || torrentInfo == null) return "";
        return uTorrentRemote.getInstance().getString(R.string.total_size_text,
                displayableSize(torrent.getTotalSize()),
                torrentInfo.getPieceCount(),
                displayableSize(torrentInfo.getPieceSize()));
    }

    public static String haveSize(TorrentInfo torrentInfo) {
        if (torrentInfo == null) return "";

        Context context = uTorrentRemote.getInstance();

        if (torrentInfo.getHaveUnchecked() == 0 && torrentInfo.getLeftUntilDone() == 0) {
            return context.getString(R.string.have_size_100p_text,
                    displayableSize(torrentInfo.getHaveValid()));
        } else {
            String text = context.getString(R.string.have_size_text,
                    displayableSize(torrentInfo.getHaveValid()),
                    displayableSize(torrentInfo.getSizeWhenDone()),
                    torrentInfo.getHavePercent());
            if (torrentInfo.getHaveUnchecked() > 0) {
                text += context.getString(R.string.have_unverified_size_text,
                        displayableSize(torrentInfo.getHaveUnchecked()));
            }
            return text;
        }
    }

    public static String downloadedSize(TorrentInfo torrentInfo) {
        if (torrentInfo == null) return "";

        String downloaded = displayableSize(torrentInfo.getDownloadedEver());
        long corrupt = torrentInfo.getCorruptEver();

        if (corrupt > 0) {
            return uTorrentRemote.getInstance().getString(R.string.downloaded_ever_text,
                    downloaded, displayableSize(corrupt));
        }
        return downloaded;
    }

    public static String uploadedSize(TorrentInfo torrentInfo) {
        if (torrentInfo == null) return "";

        long downloaded = torrentInfo.getDownloadedEver();
        if (downloaded == 0) downloaded = torrentInfo.getHaveValid();
        long uploaded = torrentInfo.getUploadedEver();
        double ratio = downloaded > 0 ? (double) uploaded / downloaded : 0;

        return uTorrentRemote.getInstance().getString(R.string.uploaded_ever_text,
                displayableSize(uploaded), ratio);
    }

    public static long transferSpeed(TorrentInfo torrentInfo) {
        if (torrentInfo == null) return 0;

        long duration = torrentInfo.getSecondsDownloading();
        if (duration <= 0) return 0;

        long downloaded = torrentInfo.getSizeWhenDone() - torrentInfo.getLeftUntilDone();
        return downloaded / duration;
    }

}
