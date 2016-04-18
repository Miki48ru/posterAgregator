package com.github.miki48ru.posteragregator;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import java.io.IOException;

public class JSoupActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jsoup);

        mTextView = (TextView)findViewById(R.id.text_parsing);
    }


    public void onClick(View view) {
        String searchStr = ((EditText)findViewById(R.id.et_url)).getText().toString();
        new ParsingPageTask().execute(searchStr);
    }

    class ParsingPageTask extends AsyncTask<String, Void, String>{


        @Override
        protected String doInBackground(String... params) {


            Document document = null;
            try {
                // Соединяемся с адресом и получаем документ
                Connection connection = Jsoup.connect(params[0]);
                Connection.Response response = connection.execute();
                String body = response.body();

                document = Jsoup.parse(body);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return document.text(); // получаем весь текст
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mTextView.setText(result);
        }
    }




}
