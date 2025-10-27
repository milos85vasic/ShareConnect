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

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.model.json.TransferPriority;

public class TransferPrioritySpinnerAdapter extends BaseAdapter {

    @Override
    public int getCount() {
        return TransferPriority.values().length;
    }

    @Override
    public TransferPriority getItem(int position) {
        return TransferPriority.values()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater li = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TransferPriority priority = getItem(position);

        TextView text = view.findViewById(android.R.id.text1);

        text.setText(getTextRes(priority));
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                parent.getResources().getDimensionPixelSize(R.dimen.tr_abc_text_size_subhead_material));
        text.setCompoundDrawablePadding(parent.getResources()
                .getDimensionPixelSize(R.dimen.default_text_margin_small));

        @DrawableRes final int iconRes;
        switch (priority) {
            case HIGH:
                iconRes = R.drawable.ic_priority_high;
                break;
            case NORMAL:
                iconRes = R.drawable.ic_priority_normal;
                break;
            case LOW:
                iconRes = R.drawable.ic_priority_low;
                break;
            default:
                iconRes = 0;
        }
        text.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0);

        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater li = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(android.R.layout.simple_spinner_item, parent, false);
        }
        TextView text = view.findViewById(android.R.id.text1);
        text.setText(getTextRes(getItem(position)));
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                parent.getResources().getDimensionPixelSize(R.dimen.tr_abc_text_size_subhead_material));

        return view;
    }

    public int getTextRes(TransferPriority priority) {
        switch (priority) {
            case HIGH:
                return R.string.priority_high;
            case NORMAL:
                return R.string.priority_normal;
            case LOW:
                return R.string.priority_low;
        }
        return 0;
    }
}
