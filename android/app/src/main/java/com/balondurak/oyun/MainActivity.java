package com.balondurak.oyun;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Oyunu tam ekran bir WebView içinde açar.
 * İnternet varsa en son sürümü GitHub Pages adresinden yükler; böylece oyun güncellenince
 * herkesin uygulaması bir sonraki açılışta kendiliğinden yenilenir.
 * İnternet yoksa ya da adres açılmazsa uygulamanın içindeki kopyayı (assets/index.html) açar.
 */
public class MainActivity extends Activity {

    private static final String ONLINE_URL = "https://celiklioguzhan.github.io/balon-duragi/";
    private static final String OFFLINE_URL = "file:///android_asset/index.html";

    private WebView web;
    private boolean fellBack = false;

    private boolean online() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return nc != null && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        web = new WebView(this);
        web.setBackgroundColor(Color.parseColor("#4FC3FF"));
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);                 // ilerleme kaydı (localStorage)
        s.setMediaPlaybackRequiresUserGesture(false);  // sesler ilk dokunuştan sonra çalsın
        s.setAllowFileAccess(true);
        boolean net = online();
        // internet varken her açılışta en yeni sürüm; yokken önbellekteki son sürüm
        s.setCacheMode(net ? WebSettings.LOAD_NO_CACHE : WebSettings.LOAD_CACHE_ONLY);
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError err) {
                if (req.isForMainFrame() && !fellBack) { fellBack = true; view.loadUrl(OFFLINE_URL); }
            }
        });
        setContentView(web);
        hideBars();
        if (savedInstanceState != null) web.restoreState(savedInstanceState);
        else web.loadUrl(ONLINE_URL);
    }

    /** Durum ve gezinme çubuklarını gizler; kaydırınca geçici olarak görünürler. */
    private void hideBars() {
        if (Build.VERSION.SDK_INT >= 30) {
            WindowInsetsController c = getWindow().getInsetsController();
            if (c != null) {
                c.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                c.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideBars();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        web.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        web.onResume();
    }
}
