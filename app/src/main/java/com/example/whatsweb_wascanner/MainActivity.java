package com.example.whatsweb_wascanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

public class MainActivity extends AppCompatActivity {

    public static boolean keyboard_state = true;
    WebView webView;
    MaterialToolbar materialToolbar;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        linearLayout = findViewById(R.id.loading_screen);
        materialToolbar = findViewById(R.id.toolbarMainActivity);

        setSupportActionBar(materialToolbar);

        loadSite();
    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbarmainactivity, menu);


        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        super.onOptionsItemSelected(item);

        int id = item.getItemId();
        switch (id) {


            case R.id.refresh:
                   loadSite();
                return true;

            case R.id.keyboard:
                enableDisableKeyboard(keyboard_state);
                 return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void enableDisableKeyboard(boolean keyboard_state) {

        if(keyboard_state)
        {
            webView.setFocusable(false);
            webView.setFocusableInTouchMode(false);
            keyboard_state = false; }
        else
            webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        keyboard_state = true;
    }



    @Override
    public void onBackPressed() {

        if(webView.canGoBack())
            webView.goBack();
    }

    public void loadSite(){


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                linearLayout.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                Toast.makeText(MainActivity.this, "called...", Toast.LENGTH_SHORT).show();

                String url = request.getUrl().toString();
                if (url.startsWith("https://web.whatsapp.com/")) {
                    Toast.makeText(MainActivity.this, "normal...", Toast.LENGTH_SHORT).show();

                    // Allow navigation within the WhatsApp Web interface
                    return false;
                }
                else if (url.startsWith("https://web.whatsapp.com/send?"))
                {

                    Toast.makeText(MainActivity.this, "downloading...", Toast.LENGTH_SHORT).show();
                    // Intercept file download requests
                    String mediaUrl = extractMediaUrlFromSendUrl(url);
                    if (mediaUrl != null) {
                        downloadFile(mediaUrl);
                        return true;
                    }
                }
                // Allow all other URLs to be loaded normally
                return false;
            }

            private String extractMediaUrlFromSendUrl(String url) {
                // Extract the media URL from the WhatsApp Web "send" URL
                // Example send URL: https://web.whatsapp.com/send?phone=123456789&source=&data=media%2F%2Fdocument%3Fid%3D123456789%26mime%3Dapplication%252Fpdf%26name%3Dexample.pdf%26size%3D12345
                Uri uri = Uri.parse(url);
                String data = uri.getQueryParameter("data");
                if (data != null) {
                    try {
                        String decodedData = URLDecoder.decode(data, "UTF-8");
                        String[] parts = decodedData.split("/");
                        if (parts.length >= 2 && parts[0].equals("media")) {
                            String mediaUrl = "https://web.whatsapp.com/" + decodedData;
                            return mediaUrl;
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            private void downloadFile(String url) {
                // Download the file from the given URL and save it to disk
                // You can use any library or code for downloading files
                // Once the file is downloaded, you can save it to your app's storage or show a notification to the user
                // Here's an example using Android's built-in DownloadManager
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "qqqqqqqqqqqqqq.jpg");
                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                downloadManager.enqueue(request);
            }
        });


      /*  webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                linearLayout.setVisibility(View.GONE);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png")) {

                    Toast.makeText(MainActivity.this, "hello", Toast.LENGTH_SHORT).show();
                    try {
                        URL imageUrl = new URL(url);
                        HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                        connection.connect();
                        InputStream inputStream = connection.getInputStream();
                        return new WebResourceResponse("image/jpeg", "UTF-8", inputStream);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }

        });
*/

        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36";
        webView.getSettings().setUserAgentString(userAgent);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        webView.loadUrl("https://web.whatsapp.com/");

    }

    class DownloadInterface {
        @JavascriptInterface
        public void showToast(String message) {
            Toast.makeText(MainActivity.this,"heloooooo toast: "+ message, Toast.LENGTH_SHORT).show();
        }
    }



}