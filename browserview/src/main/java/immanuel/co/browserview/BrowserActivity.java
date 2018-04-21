package immanuel.co.browserview;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BrowserActivity extends AppCompatActivity {

    public WebView webView;
    int appVersion = 0;
    int osVersion;
    String deviceModel;
    String deviceManufacturer;
    String device;
    String url;

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
        String deviceId = "";
        device = isTablet(BrowserActivity.this);
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

    public class AndroidBridge {

        private BrowserActivity activity;

        public AndroidBridge(BrowserActivity activity) {
            this.activity = activity;
        }

        @JavascriptInterface
        public void userLogStatus(boolean status){

        }
    }
}
