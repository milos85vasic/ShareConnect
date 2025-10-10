package com.shareconnect.utorrentconnect.preferences;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.shareconnect.utorrentconnect.BaseActivity;
import com.shareconnect.utorrentconnect.R;
import com.shareconnect.utorrentconnect.uTorrentRemote;
import com.shareconnect.utorrentconnect.server.AddServerActivity;
import com.shareconnect.utorrentconnect.server.Server;
import com.shareconnect.utorrentconnect.server.ServerDetailsFragment;

public class ServersActivity extends BaseActivity {

    private static final String TAG = ServersActivity.class.getSimpleName();

    private static final String TAG_SERVERS = "tag_servers";
    private static final String TAG_SERVER_DETAILS = "tag_server_details";

    private static final int REQUEST_CODE_NEW_SERVER = 1;

    public static final String KEY_SERVER_UUID = "key_server_uuid";

    private uTorrentRemote app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servers_activity_layout);

        app = uTorrentRemote.getApplication(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
        }

        FragmentManager fm = getSupportFragmentManager();
        if (savedInstanceState == null) {
            if (getIntent().hasExtra(KEY_SERVER_UUID)) {
                String id = getIntent().getStringExtra(KEY_SERVER_UUID);
                showServerDetails(app.getServerById(id), false);
            } else {
                ServersFragment serversFragment = (ServersFragment) fm.findFragmentByTag(TAG_SERVERS);
                if (serversFragment == null) {
                    serversFragment = new ServersFragment();
                }
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_container, serversFragment, TAG_SERVERS);
                ft.commit();

                serversFragment.setOnServerSelectedListener(new ServersFragment.OnServerSelectedListener() {
                    @Override
                    public void onServerSelected(Server server) {
                        showServerDetails(server);
                    }
                });
            }
        }

        invalidateOptionsMenu();

        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                invalidateOptionsMenu();
            }
        });
    }

    private void showServerDetails(Server server) {
        showServerDetails(server, true);
    }

    private void showServerDetails(Server server, boolean saveBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        ServerDetailsFragment serverDetailsFragment = (ServerDetailsFragment) fm.findFragmentByTag(TAG_SERVER_DETAILS);
        if (serverDetailsFragment == null) {
            serverDetailsFragment = new ServerDetailsFragment();
            Bundle arguments = new Bundle();
            arguments.putParcelable(ServerDetailsFragment.ARGUMENT_SERVER, server);
            serverDetailsFragment.setArguments(arguments);
        }

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, serverDetailsFragment, TAG_SERVER_DETAILS);
        if (saveBackStack) ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            startActivityForResult(new Intent(this, AddServerActivity.class), REQUEST_CODE_NEW_SERVER);
            return true;
        } else if (id == R.id.action_remove) {
            new AlertDialog.Builder(this)
                .setMessage(R.string.remove_server_confirmation)
                .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ServerDetailsFragment detailsFragment = (ServerDetailsFragment) getSupportFragmentManager().findFragmentByTag(TAG_SERVER_DETAILS);
                        if (detailsFragment != null) {
                            Server server = detailsFragment.getServerArgument();
                            if (server != null) {
                                app.removeServer(server);
                                onBackPressed();
                            }
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .create().show();
            return true;
        } else if (id == R.id.action_save) {
            ServerDetailsFragment detailsFragment = (ServerDetailsFragment) getSupportFragmentManager().findFragmentByTag(TAG_SERVER_DETAILS);
            if (detailsFragment != null) {
                detailsFragment.saveServer();
                app.updateServer(detailsFragment.getServerArgument());
                onBackPressed();
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "ServerDetailsFragment is not active while save server action performed");
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        boolean handled = handleBackPressByFragments();
        if (handled) {
            return false;
        } else {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
                return false;
            } else {
                finish();
                return true;
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_NEW_SERVER) {
            if (resultCode == RESULT_OK) {
                Server server = data.getParcelableExtra(AddServerActivity.EXTRA_SEVER);
                app.addServer(server);
                app.setActiveServer(server);
            }
        }
    }
}
