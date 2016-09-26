package com.abrenaut.geotracker;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;

/**
 * Created by arthurbrenaut on 26/09/2016.
 */
public class MapFragment extends Fragment {

    public WebView mWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String address = preferences.getString(SettingsFragment.KEY_ADDRESS, null);
        String deviceId = Settings.Secure.ANDROID_ID;

        String url = Uri.parse(address)
                .buildUpon()
                .appendQueryParameter("device_id", deviceId)
                .build().toString();

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mWebView = (WebView) view.findViewById(R.id.webview);
        mWebView.loadUrl(url);

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient());

        return view;

    }

}