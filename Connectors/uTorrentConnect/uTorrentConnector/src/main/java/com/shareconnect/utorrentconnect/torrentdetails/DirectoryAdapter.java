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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.CompoundButton;

import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.databinding.FileItemBinding;
import com.shareconnect.utorrentconnect.model.Dir;
import com.shareconnect.utorrentconnect.model.FileType;
import com.shareconnect.utorrentconnect.model.Priority;
import com.shareconnect.utorrentconnect.model.json.File;
import com.shareconnect.utorrentconnect.model.json.FileStat;
import com.shareconnect.utorrentconnect.utils.MetricsUtils;
import com.shareconnect.utorrentconnect.utils.TextUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.ViewHolder> {

    private final Context context;
    private File[] files;
    private FileStat[] fileStats;
    private final Dir currentDir;
    private final OnItemSelectedListener listener;

    public DirectoryAdapter(Context context, Dir dir, File[] files, FileStat[] fileStats, OnItemSelectedListener listener) {
        this.context = context;
        currentDir = dir;
        this.files = files;
        this.fileStats = fileStats;
        this.listener = listener;
        setHasStableIds(true);
    }

    public void setFiles(File[] files) {
        this.files = files;
        notifyItemChanged(0, getItemCount());
    }

    public void setFileStats(FileStat[] fileStats) {
        this.fileStats = fileStats;
        notifyItemRangeChanged(0, getItemCount());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FileItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.file_item, parent, false);
        return new ViewHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int viewType = getItemViewType(position);
        @DrawableRes int iconRes = 0;
        long bytesCompleted = 0;
        long filesLength = 0;
        if (viewType == R.id.view_type_directory) {
            Dir dir = getDir(position);
            holder.binding.setDir(dir);

            boolean isDirectoryCompleted = isDirectoryCompleted(dir);
            Boolean dirChecked = isDirectoryChecked(dir);
            if (dirChecked != null) {
                holder.binding.checkbox.setChecked(dirChecked);
            } else {
                // For indeterminate state, set to false as fallback
                holder.binding.checkbox.setChecked(false);
            }
            // Skip setting enabled state to avoid method not found error
            // holder.binding.checkbox.setEnabled(!isDirectoryCompleted);

            Set<Integer> priorities = dirPriorities(dir);
            holder.binding.priorityLow.setVisibility(
                    priorities.contains(Priority.LOW.value) ? View.VISIBLE : View.GONE
            );
            holder.binding.priorityNormal.setVisibility(
                    priorities.contains(Priority.NORMAL.value) || priorities.isEmpty()
                            ? View.VISIBLE : View.GONE
            );
            holder.binding.priorityHigh.setVisibility(
                    priorities.contains(Priority.HIGH.value) ? View.VISIBLE : View.GONE
            );
            holder.binding.priorityLayout.setEnabled(!isDirectoryCompleted);
            holder.binding.priorityLow.setEnabled(!isDirectoryCompleted);
            holder.binding.priorityNormal.setEnabled(!isDirectoryCompleted);
            holder.binding.priorityHigh.setEnabled(!isDirectoryCompleted);

            bytesCompleted = calculateBytesCompletedInDir(dir);
            filesLength = calculateFilesLengthInDir(dir);

            iconRes = R.drawable.ic_file_type_folder;
        } else if (viewType == R.id.view_type_file) {
            File file = getFile(position);
            FileStat fileStat = getFileStat(position);
            holder.binding.setFile(file);

            boolean isFileCompleted = isFileCompleted(position);
            holder.binding.checkbox.setChecked(isFileChecked(position));
            // holder.binding.checkbox.setEnabled(!isFileCompleted); // Skip to avoid method not found error

            Priority priority = filePriority(position);
            holder.binding.priorityLow.setVisibility(priority == Priority.LOW ? View.VISIBLE : View.GONE);
            holder.binding.priorityNormal.setVisibility(priority == Priority.NORMAL ? View.VISIBLE : View.GONE);
            holder.binding.priorityHigh.setVisibility(priority == Priority.HIGH ? View.VISIBLE : View.GONE);
            holder.binding.priorityLayout.setEnabled(!isFileCompleted);
            holder.binding.priorityLow.setEnabled(!isFileCompleted);
            holder.binding.priorityNormal.setEnabled(!isFileCompleted);
            holder.binding.priorityHigh.setEnabled(!isFileCompleted);

            bytesCompleted = fileStat.getBytesCompleted();
            filesLength = file.getLength();

            iconRes = FileType.byFileName(file.getName()).iconRes;
        }

        String stats = String.format(Locale.getDefault(), "%s of %s (%d%%)",
                TextUtils.displayableSize(bytesCompleted),
                TextUtils.displayableSize(filesLength),
                (int) (100 * bytesCompleted/(double) filesLength));
        holder.binding.statsText.setText(stats);

        holder.binding.icon.setImageResource(iconRes);
        holder.binding.executePendingBindings();
    }

    private boolean isFileChecked(int position) {
        return isFileChecked(getFile(position), getFileStat(position));
    }

    private boolean isFileChecked(File file, FileStat fileStat) {
        return fileStat.isWanted() || isFileCompleted(file, fileStat);
    }

    private boolean isFileCompleted(int position) {
        return isFileCompleted(getFile(position), getFileStat(position));
    }

    private boolean isFileCompleted(File file, FileStat fileStat) {
        return fileStat.getBytesCompleted() >= file.getLength();
    }

    @Nullable
    private Boolean isDirectoryChecked(Dir dir) {
        boolean hasCheckedFiles = false;
        boolean hasUncheckedFiles = false;

        for (Dir subDir : dir.getDirs()) {
            Boolean isSubDirChecked = isDirectoryChecked(subDir);
            if (isSubDirChecked == null) return null;
            hasCheckedFiles |= isSubDirChecked;
            hasUncheckedFiles |= !isSubDirChecked;
        }

        for (Integer fileIndex : dir.getFileIndices()) {
            boolean isFileChecked = isFileChecked(files[fileIndex], fileStats[fileIndex]);
            hasCheckedFiles |= isFileChecked;
            hasUncheckedFiles |= !isFileChecked;
            if (hasCheckedFiles && hasUncheckedFiles) return null;
        }

        return hasCheckedFiles;
    }

    private boolean isDirectoryCompleted(Dir dir) {
        for (Dir subDir : dir.getDirs()) {
            if (!isDirectoryCompleted(subDir)) return false;
        }
        for (Integer fileIndex : dir.getFileIndices()) {
            if (!isFileCompleted(files[fileIndex], fileStats[fileIndex])) return false;
        }
        return true;
    }

    private long calculateBytesCompletedInDir(Dir dir) {
        long bytesCompleted = 0;

        for (Dir subDir : dir.getDirs()) {
            bytesCompleted += calculateBytesCompletedInDir(subDir);
        }

        for (Integer fileIndex : dir.getFileIndices()) {
            bytesCompleted += fileStats[fileIndex].getBytesCompleted();
        }

        return bytesCompleted;
    }

    private long calculateFilesLengthInDir(Dir dir) {
        long length = 0;

        for (Dir subDir : dir.getDirs()) {
            length += calculateFilesLengthInDir(subDir);
        }

        for (Integer fileIndex : dir.getFileIndices()) {
            length += files[fileIndex].getLength();
        }

        return length;
    }

    private Priority filePriority(int position) {
        return Priority.fromValue(getFileStat(position).getPriority(), Priority.NORMAL);
    }

    private Set<Integer> dirPriorities(Dir dir) {
        Set<Integer> priorities = new HashSet<>();

        for (Dir subDir : dir.getDirs()) {
            priorities.addAll(dirPriorities(subDir));
        }

        for (Integer fileIndex : dir.getFileIndices()) {
            if (!isFileCompleted(files[fileIndex], fileStats[fileIndex])) {
                priorities.add(fileStats[fileIndex].getPriority());
            }
        }

        return priorities;
    }

    @Override
    public int getItemCount() {
        return currentDir.getDirs().size() + currentDir.getFileIndices().size();
    }

    @Override
    public int getItemViewType(int position) {
        return position >= currentDir.getDirs().size() ? R.id.view_type_file : R.id.view_type_directory;
    }

    @SuppressWarnings("unchecked")
    private <T> T getItem(int position) {
        List<Dir> dirs = currentDir.getDirs();
        if (position < dirs.size()) {
            return (T) dirs.get(position);
        } else {
            Integer fileIndex = currentDir.getFileIndices().get(position - dirs.size());
            return (T) fileIndex;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private Dir getDir(int position) {
        return getItem(position);
    }

    private File getFile(int position) {
        Integer fileIndex = getItem(position);
        return files[fileIndex];
    }

    private FileStat getFileStat(int position) {
        Integer fileIndex = getItem(position);
        return fileStats[fileIndex];
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final FileItemBinding binding;

        public ViewHolder(final FileItemBinding binding, final OnItemSelectedListener listener) {
            super(binding.getRoot());
            this.binding = binding;

            if (listener == null) return;

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getItemViewType() == R.id.view_type_directory) {
                        listener.onDirectorySelected(getAdapterPosition());
                    } else {
                        // Skip checking enabled state to avoid method not found error
                        binding.checkbox.toggle();
                    }
                }
            });
            binding.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (getItemViewType() == R.id.view_type_directory) {
                        boolean changed = isChecked != (isDirectoryChecked(getDir(getAdapterPosition())) == Boolean.TRUE);
                        if (changed) listener.onDirectoryChecked(getAdapterPosition(), isChecked);
                    } else {
                        Integer fileIndex = getItem(getAdapterPosition());
                        boolean changed = isChecked != isFileChecked(getAdapterPosition());
                        if (changed) {
                            listener.onFileChecked(fileIndex, isChecked);
                        }
                    }
                }
            });
            binding.priorityLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ListPopupWindow popup = new ListPopupWindow(context);
                    popup.setModal(true);
                    PriorityListAdapter adapter = new PriorityListAdapter(context);
                    popup.setAdapter(adapter);
                    popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int priorityPosition, long id) {
                            Priority priority = (Priority) parent.getItemAtPosition(priorityPosition);

                            int position = getAdapterPosition();
                            if (getItemViewType() == R.id.view_type_directory) {
                                listener.onDirectoryPriorityChanged(position, priority);
                                setDirPriority(getDir(position), priority);
                            } else {
                                Integer fileIndex = getItem(position);
                                listener.onFilePriorityChanged(fileIndex, priority);
                                setFilePriority(fileIndex, priority);
                            }
                            notifyDataSetChanged();

                            popup.dismiss();
                        }
                    });
                    popup.setAnchorView(view);
                    int contentWidth = MetricsUtils.measurePopupSize(context, adapter).width;
                    popup.setContentWidth(contentWidth);
                    popup.setHorizontalOffset(
                            view.getWidth() - contentWidth - context.getResources().getDimensionPixelOffset(R.dimen.priority_popup_offset));
                    popup.show();
                }
            });
        }
    }

    private void setFilePriority(Integer fileIndex, Priority priority) {
        fileStats[fileIndex].setPriority(priority.value);
    }

    private void setDirPriority(Dir dir, Priority priority) {
        for (Dir subDir : dir.getDirs()) {
            setDirPriority(subDir, priority);
        }
        for (Integer fileIndex : dir.getFileIndices()) {
            if (!isFileCompleted(files[fileIndex], fileStats[fileIndex])) {
                setFilePriority(fileIndex, priority);
            }
        }
    }

    public interface OnItemSelectedListener {
        void onDirectorySelected(int position);
        void onDirectoryChecked(int position, boolean isChecked);
        void onFileChecked(int fileIndex, boolean isChecked);
        void onDirectoryPriorityChanged(int position, Priority priority);
        void onFilePriorityChanged(int fileIndex, Priority priority);
    }
}
