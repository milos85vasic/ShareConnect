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


package com.shareconnect.utorrentconnect.model.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.client.util.Key;

public class Peer implements Parcelable {

    @Key public String address;
    @Key public String clientName;
    @Key public boolean clientIsChoked;
    @Key public boolean clientIsInterested;
    @Key public String flagStr;
    @Key public boolean isDownloadingFrom;
    @Key public boolean isEncrypted;
    @Key public boolean isIncoming;
    @Key public boolean isUploadingTo;
    @Key public boolean isUTP;
    @Key public boolean peerIsChoked;
    @Key public boolean peerIsInterested;
    @Key public int port;
    @Key public double progress;
    @Key public long rateToClient;
    @Key public long rateToPeer;

    public Peer() {
        // required by GSON
    }

    protected Peer(Parcel in) {
        address = in.readString();
        clientName = in.readString();
        clientIsChoked = in.readByte() != 0;
        clientIsInterested = in.readByte() != 0;
        flagStr = in.readString();
        isDownloadingFrom = in.readByte() != 0;
        isEncrypted = in.readByte() != 0;
        isIncoming = in.readByte() != 0;
        isUploadingTo = in.readByte() != 0;
        isUTP = in.readByte() != 0;
        peerIsChoked = in.readByte() != 0;
        peerIsInterested = in.readByte() != 0;
        port = in.readInt();
        progress = in.readDouble();
        rateToClient = in.readLong();
        rateToPeer = in.readLong();
    }

    public static final Creator<Peer> CREATOR = new Creator<Peer>() {
        @Override
        public Peer createFromParcel(Parcel in) {
            return new Peer(in);
        }

        @Override
        public Peer[] newArray(int size) {
            return new Peer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(address);
        parcel.writeString(clientName);
        parcel.writeByte((byte) (clientIsChoked ? 1 : 0));
        parcel.writeByte((byte) (clientIsInterested ? 1 : 0));
        parcel.writeString(flagStr);
        parcel.writeByte((byte) (isDownloadingFrom ? 1 : 0));
        parcel.writeByte((byte) (isEncrypted ? 1 : 0));
        parcel.writeByte((byte) (isIncoming ? 1 : 0));
        parcel.writeByte((byte) (isUploadingTo ? 1 : 0));
        parcel.writeByte((byte) (isUTP ? 1 : 0));
        parcel.writeByte((byte) (peerIsChoked ? 1 : 0));
        parcel.writeByte((byte) (peerIsInterested ? 1 : 0));
        parcel.writeInt(port);
        parcel.writeDouble(progress);
        parcel.writeLong(rateToClient);
        parcel.writeLong(rateToPeer);
    }
}
