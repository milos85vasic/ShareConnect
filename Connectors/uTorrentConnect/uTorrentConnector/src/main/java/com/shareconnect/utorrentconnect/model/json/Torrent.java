package com.shareconnect.utorrentconnect.model.json;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.api.client.util.Key;

import com.shareconnect.utorrentconnect.model.ID;

public class Torrent implements ID, Parcelable {
    @Key private int id;
    @Key private String hash; // uTorrent uses hash strings as primary identifier
    @Key private String name;
    @Key private long addedDate;
    @Key private long totalSize;
    @Key private double percentDone;
    @Key private int status;
    @Key("rateDownload") private int downloadRate;
    @Key("rateUpload") private int uploadRate;
    @Key private long eta;
    @Key("uploadedEver") private long uploadedSize;
    @Key private double uploadRatio;
    @Key("error") private int errorId;
    private Error error;
    @Key private String errorString;
    @Key private boolean isFinished;
    @Key private long sizeWhenDone;
    @Key private long leftUntilDone;
    @Key private int peersGettingFromUs;
    @Key private int peersSendingToUs;
    @Key private int webseedsSendingToUs;
    @Key private int queuePosition;
    @Key private double recheckProgress;
    @Key private long doneDate;
    @Key private long activityDate;

    public Torrent() {}

    private Torrent(Parcel in) {
        id = in.readInt();
        hash = in.readString();
        name = in.readString();
        addedDate = in.readLong();
        totalSize = in.readLong();
        percentDone = in.readDouble();
        recheckProgress = in.readDouble();
        status = in.readInt();
        downloadRate = in.readInt();
        uploadRate = in.readInt();
        eta = in.readLong();
        uploadedSize = in.readLong();
        uploadRatio = in.readDouble();
        errorId = in.readInt();
        errorString = in.readString();
        isFinished = in.readInt() != 0;
        sizeWhenDone = in.readLong();
        leftUntilDone = in.readLong();
        peersGettingFromUs = in.readInt();
        peersSendingToUs = in.readInt();
        webseedsSendingToUs = in.readInt();
        queuePosition = in.readInt();
        doneDate = in.readLong();
        activityDate = in.readLong();
    }

    public int getId() {
        return id;
    }

    /**
     * Get the torrent hash (uTorrent's primary identifier).
     * This is a 40-character SHA-1 hash string used by uTorrent Web API.
     * @return The torrent hash string, or null if not available
     */
    public String getHash() {
        return hash;
    }

    /**
     * Set the torrent hash (used when parsing uTorrent responses).
     * @param hash The 40-character SHA-1 hash string
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getName() {
        return name;
    }

    public long getAddedDate() {
        return addedDate;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public double getPercentDone() {
        return percentDone;
    }

    public Status getStatus() {
        return Status.fromValue(status);
    }

    public int getDownloadRate() {
        return downloadRate;
    }

    public int getUploadRate() {
        return uploadRate;
    }

    public long getEta() {
        return eta;
    }

    public long getUploadedSize() {
        return uploadedSize;
    }

    public double getUploadRatio() {
        return uploadRatio;
    }

    public int getQueuePosition() {
        return queuePosition;
    }

    public int getErrorId() {
        return errorId;
    }

    public Error getError() {
        if (error == null) error = Error.getById(errorId);
        return error;
    }

    public String getErrorMessage() {
        return errorString;
    }

    public boolean isActive() {
        return peersGettingFromUs > 0
                || peersSendingToUs > 0
                || webseedsSendingToUs > 0
                || isChecking()
                || isDownloading();
    }

    public boolean isChecking() {
        return status == Status.CHECK.value;
    }

    public boolean isSeeding() {
        return status == Status.SEED.value || status == Status.SEED_WAIT.value;
    }

    public boolean isDownloading() {
        return status == Status.DOWNLOAD.value || status == Status.DOWNLOAD_WAIT.value;
    }

    public boolean isPaused() {
        return status == Status.STOPPED.value;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public boolean isCompleted() {
        return leftUntilDone <= 0 && sizeWhenDone > 0;
    }

    public long getSizeWhenDone() {
        return sizeWhenDone;
    }

    public long getLeftUntilDone() {
        return leftUntilDone;
    }

    public double getRecheckProgress() {
        return recheckProgress;
    }

    public long getDoneDate() {
        return doneDate;
    }

    public int getActivity() {
        return getDownloadRate() + getUploadRate();
    }

    public long getActivityDate() {
        return activityDate;
    }

    public int getPeersGettingFromUs() {
        return peersGettingFromUs;
    }

    public int getPeersSendingToUs() {
        return peersSendingToUs;
    }

    public int getWebseedsSendingToUs() {
        return webseedsSendingToUs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(hash);
        out.writeString(name);
        out.writeLong(addedDate);
        out.writeLong(totalSize);
        out.writeDouble(percentDone);
        out.writeDouble(recheckProgress);
        out.writeInt(status);
        out.writeInt(downloadRate);
        out.writeInt(uploadRate);
        out.writeLong(eta);
        out.writeLong(uploadedSize);
        out.writeDouble(uploadRatio);
        out.writeInt(errorId);
        out.writeString(errorString);
        out.writeInt(isFinished ? 1 : 0);
        out.writeLong(sizeWhenDone);
        out.writeLong(leftUntilDone);
        out.writeInt(peersGettingFromUs);
        out.writeInt(peersSendingToUs);
        out.writeInt(webseedsSendingToUs);
        out.writeInt(queuePosition);
        out.writeLong(doneDate);
        out.writeLong(activityDate);
    }

    public static final Creator<Torrent> CREATOR = new Creator<>() {
        @Override
        public Torrent createFromParcel(Parcel in) {
            return new Torrent(in);
        }

        @Override
        public Torrent[] newArray(int size) {
            return new Torrent[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "Torrent{" +
                "id=" + id +
                ", hash='" + hash + '\'' +
                ", name='" + name + '\'' +
                ", addedDate=" + addedDate +
                ", totalSize=" + totalSize +
                ", percentDone=" + percentDone +
                ", status=" + status +
                ", downloadRate=" + downloadRate +
                ", uploadRate=" + uploadRate +
                ", uploadedSize=" + uploadedSize +
                ", uploadRatio=" + uploadRatio +
                ", errorId=" + errorId +
                ", error=" + error +
                ", errorString='" + errorString + '\'' +
                ", isFinished=" + isFinished +
                ", doneDate=" + doneDate +
                '}';
    }

    public enum Status {
        UNKNOWN(-1),
        STOPPED(0),
        CHECK_WAIT(1),
        CHECK(2),
        DOWNLOAD_WAIT(3),
        DOWNLOAD(4),
        SEED_WAIT(5),
        SEED(6);

        public final int value;

        Status(int value) {
            this.value = value;
        }

        public static Status fromValue(int value) {
            for (Status status : values()) {
                if (status.value == value)
                    return status;
            }
            return UNKNOWN;
        }
    }

    public enum Error {
        UNKNOWN(-1, false),
        NONE(0, false),
        TRACKER_WARNING(1, true),
        TRACKER_ERROR(2, false),
        LOCAL_ERROR(3, false);

        private final int id;
        private final boolean isWarning;

        Error(int id, boolean isWarning) {
            this.id = id;
            this.isWarning = isWarning;
        }

        public boolean isWarning() {
            return isWarning;
        }

        public static Error getById(int id) {
            for (Error e : Error.values()) {
                if (e.id == id) return e;
            }
            return UNKNOWN;
        }
    }

    public static class Builder {

        private final Torrent torrent;

        public Builder() {
            torrent = new Torrent();
        }

        public Builder id(int id) {
            torrent.id = id;
            return this;
        }

        public Builder hash(String hash) {
            torrent.hash = hash;
            return this;
        }

        public Builder name(String name) {
            torrent.name = name;
            return this;
        }

        public Builder addedDate(long date) {
            torrent.addedDate = date;
            return this;
        }

        public Builder totalSize(long size) {
            torrent.totalSize = size;
            return this;
        }

        public Builder percentDone(double percent) {
            torrent.percentDone = percent;
            return this;
        }

        public Builder status(int status) {
            torrent.status = status;
            return this;
        }

        public Builder downloadRate(int rate) {
            torrent.downloadRate = rate;
            return this;
        }

        public Builder uploadRate(int rate) {
            torrent.uploadRate = rate;
            return this;
        }

        public Builder eta(long eta) {
            torrent.eta = eta;
            return this;
        }

        public Builder uploadedSize(long size) {
            torrent.uploadedSize = size;
            return this;
        }

        public Builder uploadRatio(double ratio) {
            torrent.uploadRatio = ratio;
            return this;
        }

        public Builder errorId(int errorId) {
            torrent.errorId = errorId;
            return this;
        }

        public Builder errorString(String errorString) {
            torrent.errorString = errorString;
            return this;
        }

        public Builder isFinished(boolean finished) {
            torrent.isFinished = finished;
            return this;
        }

        public Builder sizeWhenDone(long size) {
            torrent.sizeWhenDone = size;
            return this;
        }

        public Builder leftUntilDone(long left) {
            torrent.leftUntilDone = left;
            return this;
        }

        public Builder peersGettingFromUs(int peers) {
            torrent.peersGettingFromUs = peers;
            return this;
        }

        public Builder peersSendingToUs(int peers) {
            torrent.peersSendingToUs = peers;
            return this;
        }

        public Builder webseedsSendingToUs(int webseeds) {
            torrent.webseedsSendingToUs = webseeds;
            return this;
        }

        public Builder queuePosition(int position) {
            torrent.queuePosition = position;
            return this;
        }

        public Builder recheckProgress(double progress) {
            torrent.recheckProgress = progress;
            return this;
        }

        public Builder doneDate(long date) {
            torrent.doneDate = date;
            return this;
        }

        public Builder activityDate(long date) {
            torrent.activityDate = date;
            return this;
        }

        public Torrent build() {
            return torrent;
        }
    }
}
