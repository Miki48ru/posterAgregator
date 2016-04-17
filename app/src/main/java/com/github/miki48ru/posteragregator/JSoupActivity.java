package com.github.miki48ru.posteragregator;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class JSoupActivity extends AppCompatActivity {

    private TextView mTextView;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jsoup);

        mWebView = (WebView)findViewById(R.id.text_parsing);
    }


    public void onClick(View view) {
        String searchStr = ((TextView)findViewById(R.id.et_url)).getText().toString();
        new ParsingPageTask().execute(searchStr);
    }

    class ParsingPageTask extends AsyncTask<String, Void, String>{


        @Override
        protected String doInBackground(String... params) {
            Document document = null;
            String plainText = "";
            String htmlText = "";

            try {
                document = Jsoup.connect(params[0]).get();
                Element warning = document.select( "div class=\"b-guides\" ").first();
                plainText = warning.text();
                htmlText = warning.html();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return htmlText;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mWebView.loadDataWithBaseURL(null, result, "text/html", "UTF-8",
                    null);
        }
    }




}
