package com.sanjay.whatsappsaver;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.snatik.storage.Storage;

import java.io.File;

import static com.sanjay.whatsappsaver.util.util.sendFeedback;

public class Video_activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public InterstitialAd interstitial;
    String toPath = Environment.getExternalStorageDirectory() + "/whatsapp status saver/";
    private FloatingActionMenu menuRed;
    private com.github.clans.fab.FloatingActionButton fab1;
    private com.github.clans.fab.FloatingActionButton fab2;
    private com.github.clans.fab.FloatingActionButton fab3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        interstitial.loadAd(adRequest);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final Storage storage = new Storage(getApplicationContext());
        Log.d("", "yessss");
        final String f = getIntent().getStringExtra("mp4");
        VideoView videoView = findViewById(R.id.video_view);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoPath(f);
        videoView.start();

        String filename = new File(f).getName();
        //path
        final String topath1 = toPath + filename;
        menuRed = findViewById(R.id.fab);
        fab1 = findViewById(R.id.fab_copy);
        fab2 = findViewById(R.id.fab_delete);
        fab3 = findViewById(R.id.fab_share);
        FloatingActionMenu fab = findViewById(R.id.fab);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("", "fab1 " + f + " topath  " + topath1);

                storage.copy(f, topath1);
                Snackbar snackbar = Snackbar
                        .make(view, "file has been saved", Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });
                snackbar.show();
                scanFile(topath1);
//                displayInterstitial();
//                interstitial.setAdListener(new AdListener() {
//                    @Override
//                    public void onAdLoaded() {
//
//                    }
//
//                    @Override
//                    public void onAdClosed() {
//                        // Proceed to the next activity.
//                        onBackPressed();
//                    }
//                });
//                Log.d("scan", "onClick: " + topath1);
                // sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(f)));
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("delete", "fab2 " + f);

                storage.deleteFile(f);
                Snackbar snackbar = Snackbar
                        .make(view, "file is deleted", Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });
                snackbar.show();
                scanFile(topath1);
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                ContentValues content = new ContentValues(4);
                content.put(MediaStore.Video.VideoColumns.DATE_ADDED,
                        System.currentTimeMillis() / 1000);
                content.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                content.put(MediaStore.Video.Media.DATA, f);

                ContentResolver resolver = getApplicationContext().getContentResolver();
                Uri uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, content);

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("video/*");
//                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Hey this is the video subject");
//                sharingIntent.putExtra(Intent.EXTRA_TEXT, "Hey this is the video text");
                sharingIntent.putExtra(Intent.EXTRA_STREAM,uri);
                startActivity(Intent.createChooser(sharingIntent,"Share Video"));

            }
        });

    }

    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }

    private void scanFile(String path) {

        MediaScannerConnection.scanFile(this,
                new String[]{path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, About_activity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        View parentLayout = findViewById(android.R.id.content);
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Snackbar snackbar = Snackbar
                    .make(parentLayout, "coming soon", Snackbar.LENGTH_LONG)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
            snackbar.show();
        } else if (id == R.id.nav_gallery) {
            Snackbar snackbar = Snackbar
                    .make(parentLayout, "coming soon", Snackbar.LENGTH_LONG)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
            snackbar.show();
        } else if (id == R.id.nav_slideshow) {

            Snackbar snackbar = Snackbar
                    .make(parentLayout, "coming soon", Snackbar.LENGTH_LONG)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
            snackbar.show();
        } else if (id == R.id.nav_manage) {
            Intent i = new Intent(this, About_activity.class);
            startActivity(i);
        } else if (id == R.id.nav_share) {
            sendFeedback(getApplicationContext());
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
