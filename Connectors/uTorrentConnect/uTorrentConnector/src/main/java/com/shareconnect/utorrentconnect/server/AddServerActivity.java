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


package com.shareconnect.utorrentconnect.server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.shareconnect.utorrentconnect.R;
import com.shareconnect.languagesync.utils.LocaleHelper;

public class AddServerActivity extends AppCompatActivity {

    public static final String PARAM_CANCELABLE = "param_cancelable";
    public static final String EXTRA_SEVER = "extra_server";

    private ServerDetailsFragment serverDetailsFragment;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_server_activity);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
        }

        FragmentManager fm = getSupportFragmentManager();
        serverDetailsFragment = (ServerDetailsFragment) fm.findFragmentById(R.id.add_server_fragment_container);
        if (serverDetailsFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            serverDetailsFragment = new ServerDetailsFragment();
            ft.add(R.id.add_server_fragment_container, serverDetailsFragment);
            ft.commit();
        }

        Button okButton = findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Server server = serverDetailsFragment.getNewServer();
                if (server != null) {
                    setResult(RESULT_OK, new Intent().putExtra(EXTRA_SEVER, server));
                    finish();
                }
            }
        });

        Button cancelButton = findViewById(R.id.cancel_button);
        boolean isCancelable = getIntent().getBooleanExtra(PARAM_CANCELABLE, true);
        cancelButton.setVisibility(isCancelable ? View.VISIBLE : View.GONE);
        if (isCancelable) {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancel();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cancel();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
