package com.sanjay.whatsappsaver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sanjay.whatsappsaver.app.Config;
import com.sanjay.whatsappsaver.util.NotificationUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import io.fabric.sdk.android.Fabric;

import static com.sanjay.whatsappsaver.util.util.getMimeType;
import static com.sanjay.whatsappsaver.util.util.sendFeedback;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    //File file;
    ArrayList<File> list;
    GridView gv;
    File location = new File(Environment.getExternalStorageDirectory() + "/whatsapp/media/.Statuses/");
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    private File[] listFile;
    private AdView mAdView;
    private FirebaseAnalytics mFirebaseAnalytics;
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        mAdView = findViewById(R.id.adView1);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        //Log.d("adrequest", "onCreate: " + adRequest);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //push notification
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };
//        Log.d(TAG, "onCreate: "+location);

        list = imageReader(location);
//         Log.d("", "location " + location);
        gv = findViewById(R.id.gridview);
        gv.setAdapter(new GridAdapter());

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                getMimeType(list.get(position).toString());
//                Log.d("", "meme" + getMimeType(list.get(position).toString()));
                if (Objects.equals(getMimeType(list.get(position).toString()), "video/mp4")) {
                    startActivity(new Intent(getApplicationContext(), Video_activity.class).putExtra("mp4", list.get(position).toString()));
                } else {
                    startActivity(new Intent(getApplicationContext(), Viewimage.class).putExtra("img", list.get(position).toString()));

                }
            }
        });
        // feedback preference click listener

    }

    ArrayList<File> imageReader(File dir) {
        ArrayList<File> a = new ArrayList<>();
        File[] files = dir.listFiles();
        try {
            for (File file : files) {
                if (file.isDirectory()) {
                    a.addAll(imageReader(file));
                } else {
                    if (file.getName().endsWith(".jpg") ||
                            file.getName().endsWith(".mp4")) {
                        a.add(file);
//                        Log.d("files", "imageReader: " + file);
                    }
                }
            }
            //Log.i("a", "imageReader: " + a);
        } catch (NullPointerException ex) {
            Toast.makeText(getApplicationContext(), "no images", Toast.LENGTH_LONG).show();

        }
        return a;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
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
            Intent i = new Intent(MainActivity.this, About_activity.class);
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

    public Bitmap putOverlay(Bitmap bmp1, Bitmap overlay) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmOverlay);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);

        canvas.drawBitmap(bmp1, 0, 0, null);
        canvas.drawBitmap(overlay, 0, 0, null);

        return bmOverlay;
    }

    class GridAdapter extends BaseAdapter {

        public Bitmap bitmap;
        private android.content.Context context;

        @Override
        public int getCount() {
//            Log.d("", "getCount: " + list.size());
            return list.size();
        }

        @Override
        public Object getItem(int position) {
//            Log.d("", "getItem: " + list.get(position));
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            holder holder;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.single_grid, parent, false);
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_play_arrow_white_24dp);
//            Log.d(TAG, "getView: " + getMimeType(list.get(position).toString()));
//            if ( convertView == null ) {
//                convertView = inflater.inflate(R.layout.single_grid, null);
//                holder=new holder();
//                holder.img = (ImageView) convertView.findViewById(R.id.image);
//                convertView.setTag(holder);
//            }
            if (convertView != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Objects.equals(getMimeType(list.get(position).toString()), "image/jpeg")) {
                        ImageView iv = convertView.findViewById(R.id.imageView3);
                        Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(list.get(position).toString()), 100, 100);
                        iv.setImageBitmap(ThumbImage);//Creation of Thumbnail of image
                    } else if (Objects.equals(getMimeType(list.get(position).toString()), "video/mp4")) {
                        Bitmap bMap = ThumbnailUtils.createVideoThumbnail(list.get(position).toString(), MediaStore.Video.Thumbnails.MICRO_KIND);
                        ImageView iv = convertView.findViewById(R.id.imageView3);
                        //                putOverlay(bMap,largeIcon);
                        iv.setImageBitmap(putOverlay(bMap, largeIcon));
                    }
                }
            }
//            Glide
//                    .with(this)
//                    .load(list.get(position))
//                    .into((ImageView) convertView );
//            GlideApp
//                    .with(context)
//                    .load(imageUrls[position])
//                    .into((ImageView) convertView);
            return convertView;
        }

        class holder {
            ImageView img;
        }


    }

}
