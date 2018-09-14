package immanuel.co.browserview;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayOutputStream;

import immanuel.co.camerathumb.CameraIntent;
import immanuel.co.camerathumb.GlobalConfig;

public class BrowserActivity extends AppCompatActivity {

    public WebView webView;
    String deviceId = "";
    int appVersion = 0;
    int osVersion;
    String deviceModel;
    String deviceManufacturer;
    String device;
    String url;
    String deviceToken;
    static GpsTracker gpsloc;

    public  BrowserActivity(){
        this.url = "https://vload.in/index.html";
    }

    public  BrowserActivity(String url){
        this.url = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        osVersion = android.os.Build.VERSION.SDK_INT;
        deviceModel = Build.MODEL;
        deviceManufacturer = Build.MANUFACTURER;
        try{
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion  = pInfo.versionCode;
        }catch(Exception e){
            e.printStackTrace();
        }

        device = isTablet(BrowserActivity.this);
        deviceToken = SharedPref.getDeviceToken();
        url += "?DeviceToken=" + deviceId + "&DeviceType=android" +
                "&OsVersion=" +osVersion+  "&AppVersion=" +appVersion+ "&DeviceModel="+deviceModel+
                "&DeviceManufacturer=" +deviceManufacturer+"&Device="+device;
        webView = (WebView) findViewById(R.id.webView);

        if (Build.VERSION.SDK_INT < 19) {
            webView.getSettings().setSavePassword(false);
        }
        webView.getSettings().setSaveFormData(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(true);
        webView.addJavascriptInterface(new AndroidBridge(this), "Android");
        webView.getSettings().setBuiltInZoomControls(false);
        webView.setLongClickable(false);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setDomStorageEnabled(true);
        //this.webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        if (Build.VERSION.SDK_INT < 19) {
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        }
        webView.clearCache(true);
        webView.getSettings().setAppCacheEnabled(false);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        webView.loadUrl(url);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                BrowserActivity.this.setTitle("vLoad: Your Grocery Needs");
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {

                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
    }

    public static String isTablet(Context context) {
        boolean isTablet = (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        if(isTablet){
            return "TAB";
        }else{
            return "PHONE"  ;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("MyData")
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ndata = (intent.getExtras().getString("notification"));
            String ntitl = (intent.getExtras().getString("title"));
            webView.loadUrl("javascript:" + FcmPushNotif.jscallback + "(' { notif: \"" + ndata + "\", title: \"" + ntitl  + "\"}')");
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 110) {
            String requiredValue = data.getStringExtra("Key");
            Log.d("wewe1", requiredValue);
            //LinearLayout ll = (LinearLayout)findViewById(R.id.errt);
            for (Bitmap bm: GlobalConfig.imgs) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String imageData = Base64.encodeToString(byteArray, Base64.NO_WRAP);
                webView.loadUrl("javascript:" + SharedPref.cameracallback +"('" + imageData + "')");
            }
        }
    }

    public void StartPhotoIntent() {
        Intent i = new Intent(this, CameraIntent.class);
        startActivityForResult (i, 110);
    };

    public void UpdateLocation(String locJson, final boolean freq){
//        webView.post(new Runnable() {
//            @Override
//            public void run() {
//                webView.loadUrl("javascript:" + GpsTracker.locationCallback + "(" + locJson + ")");
//            }
//        });
        webView.post(new Runnable() {
            String str;
            @Override
            public void run() {
                if (freq){
                    webView.loadUrl("javascript:" + GpsTracker.freqcallback + "(" + str + ")");
                } else {
                    webView.loadUrl("javascript:" + GpsTracker.locationCallback + "(" + str + ")");
                }
            }
            public Runnable init(String pstr) {
                this.str=pstr;
                return(this);
            }
        }.init(locJson));
    }

    public class AndroidBridge {

        private BrowserActivity activity;

        public AndroidBridge(BrowserActivity activity) {
            this.activity = activity;
        }

        @JavascriptInterface
        public String startCamera(String cameracallback){
            SharedPref.cameracallback = cameracallback;
            StartPhotoIntent();
            return deviceToken;
        }

        @JavascriptInterface
        public String enableLocation(String callbackfunction, String freqcallback){
            GpsTracker.locationCallback = callbackfunction;
            GpsTracker.freqcallback = freqcallback;
            if (gpsloc == null)
                gpsloc = new GpsTracker(activity.getApplicationContext(), activity);
            String hh = gpsloc.getLocation();
            activity.UpdateLocation(hh, false);
            return deviceToken;
        }

        @JavascriptInterface
        public String enablePushNotification(String callbackfunction){
            FcmPushNotif.jscallback = callbackfunction;
            return deviceToken;
        }

        @JavascriptInterface
        public String getDeviceToken(){
            return deviceToken;
        }

        @JavascriptInterface
        public String getDeviceId(){
            return deviceId;
        }

        @JavascriptInterface
        public String getDeviceType(){
            return "android";
        }

        @JavascriptInterface
        public int getOsVersion(){
            return osVersion;
        }

        @JavascriptInterface
        public int getAppVersion(){
            return appVersion;
        }

        @JavascriptInterface
        public String getDeviceModel(){
            return deviceModel;
        }

        @JavascriptInterface
        public String getDeviceManufacturer(){
            return deviceManufacturer;
        }

        @JavascriptInterface
        public String getDevice(){
            return device;
        }
    }
}