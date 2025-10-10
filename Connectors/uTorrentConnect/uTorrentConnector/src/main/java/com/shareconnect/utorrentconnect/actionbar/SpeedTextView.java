package com.shareconnect.utorrentconnect.actionbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.util.TypedValue;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.TextViewCompat;

import com.google.common.base.Strings;

import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.utils.CheatSheet;
import com.shareconnect.utorrentconnect.utils.MetricsUtils;
import com.shareconnect.utorrentconnect.utils.TextUtils;

public abstract class SpeedTextView extends AppCompatTextView {

    private static final int PADDING_LEFT = 5; // dp

    public SpeedTextView(Context context, @DrawableRes int iconRes, @ColorRes int iconColorRes) {
        super(context);
        int horPadding = (int) MetricsUtils.dp2px(context, PADDING_LEFT);
        setPadding(horPadding, 0, horPadding, 0);

        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.speed_text_size));
        setTextColor(context.getResources().getColor(R.color.text_primary_inverse));
        setTypeface(null, Typeface.BOLD);

        setSpeed(0);

        setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0);
        TextViewCompat.setCompoundDrawableTintList(this, ColorStateList.valueOf(context.getColor(iconColorRes)));
    }

    public void setSpeed(long speedInBytesPerSec) {
        setText(speedText(speedInBytesPerSec));
    }

    private String speedText(long bytes) {
        return Strings.padStart(TextUtils.displayableSize(bytes), 5, ' ') + "/s";
    }

    public static class DownloadSpeedTextView extends SpeedTextView {
        public DownloadSpeedTextView(Context context) {
            super(context, R.drawable.ic_download, android.R.color.holo_green_dark);

            CheatSheet.setup(this, R.string.tooltip_total_download_speed);
        }
    }

    public static class UploadSpeedTextView extends SpeedTextView {
        public UploadSpeedTextView(Context context) {
            super(context, R.drawable.ic_upload, android.R.color.holo_red_dark);

            CheatSheet.setup(this, R.string.tooltip_total_upload_speed);
        }
    }
}
