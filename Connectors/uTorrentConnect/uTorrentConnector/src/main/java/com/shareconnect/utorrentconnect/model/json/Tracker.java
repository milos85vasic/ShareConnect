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

public class Tracker implements Parcelable {

    @Key public int id;
    @Key public int tier;
    @Key public String announce;
    @Key public String scrape;

    public static final Creator<Tracker> CREATOR = new Creator<Tracker>() {
        @Override
        public Tracker createFromParcel(Parcel in) {
            Tracker tracker = new Tracker();
            tracker.id = in.readInt();
            tracker.tier = in.readInt();
            tracker.announce = in.readString();
            tracker.scrape = in.readString();
            return tracker;
        }

        @Override
        public Tracker[] newArray(int size) {
            return new Tracker[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(tier);
        parcel.writeString(announce);
        parcel.writeString(scrape);
    }
}
