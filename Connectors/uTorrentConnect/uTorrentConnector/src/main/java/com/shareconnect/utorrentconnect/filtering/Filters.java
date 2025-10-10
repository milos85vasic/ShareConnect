package com.shareconnect.utorrentconnect.filtering;

import com.shareconnect.utorrentconnect.model.json.Torrent;
import com.shareconnect.utorrentconnect.R;

public class Filters {
    public static final Filter ALL = new BaseFilter(R.string.filter_all, R.string.filter_empty_all) {
        @Override
        public boolean test(Torrent torrent) {
            return true;
        }
    };

    public static final Filter DOWNLOADING = new BaseFilter(R.string.filter_downloading, R.string.filter_empty_downloading) {
        @Override
        public boolean test(Torrent torrent) {
            return torrent.isDownloading();
        }
    };

    public static final Filter SEEDING = new BaseFilter(R.string.filter_seeding, R.string.filter_empty_seeding) {
        @Override
        public boolean test(Torrent torrent) {
            return torrent.isSeeding();
        }
    };

    public static final Filter ACTIVE = new BaseFilter(R.string.filter_active, R.string.filter_empty_active) {
        @Override
        public boolean test(Torrent torrent) {
            return torrent.isActive();
        }
    };

    public static final Filter PAUSED = new BaseFilter(R.string.filter_paused, R.string.filter_empty_paused) {
        @Override
        public boolean test(Torrent torrent) {
            return torrent.isPaused();
        }
    };

    public static final Filter FINISHED = new BaseFilter(R.string.filter_finished, R.string.filter_empty_finished) {
        @Override
        public boolean test(Torrent torrent) {
            return torrent.isFinished();
        }
    };

    public static final Filter DOWNLOAD_COMPLETED = new BaseFilter(R.string.filter_download_completed, R.string.filter_empty_download_completed) {
        @Override
        public boolean test(Torrent torrent) {
            return torrent.isCompleted();
        }
    };
}
