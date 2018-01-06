package com.sanjay.whatsappsaver;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.snatik.storage.Storage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.sanjay.whatsappsaver.util.util.sendFeedback;

public class Viewimage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "viewimage";
    ImageView iv2;
    String toPath = Environment.getExternalStorageDirectory() + "/whatsapp status saver/";
    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    private FloatingActionMenu menuRed;
    private com.github.clans.fab.FloatingActionButton fab1;
    private com.github.clans.fab.FloatingActionButton fab2;
    private com.github.clans.fab.FloatingActionButton fab3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewimage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final Storage storage = new Storage(getApplicationContext());
        //Intent i= getIntent();
        //File f= i.getExtras().getParcelable("img");
        //storage.copy(fromPath, toPath);
        final String f = getIntent().getStringExtra("img");
        Log.d("f", "onCreate: " + f);
        iv2 = findViewById(R.id.imageView4);
        iv2.setImageURI(Uri.parse(f));
        String extension = "";
        int i = f.lastIndexOf('.');
        if (i > 0) {
            extension = f.substring(i + 1);
        }


        String filename = new File(f).getName();
//fab
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
                                onBackPressed();
                            }
                        });
                snackbar.show();
                scanFile(topath1);
                Log.d("scan", "onClick: " + topath1);
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
                                onBackPressed();
                            }
                        });
                snackbar.show();
                scanFile(topath1);
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {

                 @Override
                public void onClick(View view) {
                     Bitmap b = BitmapFactory.decodeFile(f);
                     Intent share = new Intent(Intent.ACTION_SEND);
                     share.setType("image/jpeg");
                     ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                     b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                     String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                             b, "Title", null);
                     Uri imageUri =  Uri.parse(path);
                     share.putExtra(Intent.EXTRA_STREAM, imageUri);
                     startActivity(Intent.createChooser(share, "Select"));

                }
            });
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
