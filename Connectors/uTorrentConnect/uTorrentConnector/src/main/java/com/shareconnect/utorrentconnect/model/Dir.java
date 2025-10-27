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


package com.shareconnect.utorrentconnect.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import com.shareconnect.utorrentconnect.model.json.File;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public final class Dir implements Comparable<Dir> {

    private String name;
    private List<Dir> dirs = new LinkedList<>();
    private List<Integer> fileIndices = new LinkedList<>();

    public Dir(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Dir> getDirs() {
        return dirs;
    }

    public List<Integer> getFileIndices() {
        return fileIndices;
    }

    @Nullable
    public Dir findDir(@NonNull String name) {
        for (Dir dir : dirs) {
            if (name.equals(dir.name)) return dir;
        }
        return null;
    }

    @NonNull
    public static Dir createFileTree(@NonNull File[] files) {
        Dir root = new Dir("/");

        for (int i=0; i<files.length; i++) {
            File file = files[i];
            List<String> pathParts = Arrays.asList(file.getPath().split("/"));
            if (pathParts.get(0).isEmpty()) pathParts = pathParts.subList(1, pathParts.size());
            parsePath(pathParts, root, i);
        }

        List<String> fileNames = FluentIterable.from(files)
                .transform(new Function<File, String>() {
                    @Override
                    public String apply(@NonNull File file) {
                        String[] segments = file.getPath().split("/");
                        if (segments.length == 0) return "";
                        return segments[segments.length - 1];
                    }
                }).toList();
        sortRecursively(root, fileNames);

        return root;
    }

    private static void sortRecursively(Dir dir, final List<String> fileNames) {
        Collections.sort(dir.dirs);

        Collections.sort(dir.fileIndices, new Comparator<Integer>() {
            @Override
            public int compare(Integer idx1, Integer idx2) {
                String fileName1 = fileNames.get(idx1);
                String fileName2 = fileNames.get(idx2);
                return fileName1.compareToIgnoreCase(fileName2);
            }
        });
        for (Dir subDir : dir.dirs) {
            sortRecursively(subDir, fileNames);
        }
    }

    public static List<Integer> filesInDirRecursively(Dir dir) {
        List<Integer> fileIndices = new LinkedList<>();
        collectFilesInDir(dir, fileIndices);
        return fileIndices;
    }

    private static void collectFilesInDir(Dir dir, List<Integer> fileIndices) {
        fileIndices.addAll(dir.getFileIndices());
        for (Dir subDir : dir.getDirs()) {
            collectFilesInDir(subDir, fileIndices);
        }
    }

    private static void parsePath(List<String> pathParts, Dir parentDir, int fileIndex) {
        if (pathParts.size() == 1) {
            parentDir.fileIndices.add(fileIndex);
            return;
        }

        String dirName = pathParts.get(0);
        Dir dir = findDirWithName(dirName, parentDir.dirs);
        if (dir == null) {
            dir = new Dir(dirName);
            parentDir.dirs.add(dir);
        }
        parsePath(pathParts.subList(1, pathParts.size()), dir, fileIndex);
    }

    private static Dir findDirWithName(String name, List<Dir> dirs) {
        for (Dir dir : dirs) {
            if (dir.name.equals(name)) return dir;
        }
        return null;
    }

    @Override
    public int compareTo(@NonNull Dir dir) {
        return name.compareToIgnoreCase(dir.name);
    }
}
