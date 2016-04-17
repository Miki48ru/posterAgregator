package com.github.miki48ru.posteragregator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button btnAgo;

    private ProgressDialog dialog;

    // метка-тег для логов. По ней удобно будет отфильтровать записи в логах
    private final String LOG_TAG = "WebAccess";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAgo = (Button)findViewById(R.id.btn_ago);
    }

    public void sendRequest(View v) {
        String url = ((EditText)findViewById(R.id.etURL)).getText().toString();
        String searchStr = ((EditText)findViewById(R.id.etSearchString)).getText().toString();
        new DownloadWebpageTask().execute(url, searchStr);
    }

    public void onClickAgo(View view) {
        Intent intent = new Intent(MainActivity.this, JSoupActivity.class);
        startActivity(intent);

    }

    // класс - асинхронно выполняемая задача (фоновый поток)
    private class DownloadWebpageTask extends AsyncTask<String, String, String> {
        StringBuilder out;

        // фоновая задача - выполнение запроса к серверу
        @Override
        protected String doInBackground(String ... urls) {
            URL url = null;
            try {
                // получаем переданный адрес (из метода sendRequest)
                String urlAddr = urls[0];
                // получаем текст для поиска
                String searchStr = urls[1];

                // формируем поисковый запрос - добавляем параметр запроса к серверу
                urlAddr += "?text=" + searchStr;
                // выводим в лог адрес для проверки
                Log.v(LOG_TAG, "URL=" + urlAddr);

                // создание объекта URL на основе строки адреса
                url = new URL(urlAddr);

                // создаём объект для соединения с указанным адресом
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                // получаем ссылку на поток, содержащий ответ сервера
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                // проверяем код ответа сервера
                if (connection.getResponseCode() == connection.HTTP_OK) {
                    Log.v(LOG_TAG, "conn ok");

                    // обрабатываем ответ сервера - записываем его в строковую переменную
                    out = new StringBuilder();
                    String line;
                    // считываем очередную строку из ответа сервера
                    while ((line = reader.readLine()) != null) {
                        // добавляем её в нашу переменную
                        out.append(line);
                    }
                    // закрываем поток ответа сервера
                    reader.close();

                } else {
                    out.append("Произошла ошибка, код ответа: " + connection.getResponseCode());
                    Log.v(LOG_TAG, "conn err");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException ie) {

            } catch (Exception e) {
                Log.v("WebTest", "Exception");
            }
            return "";
        }

        // выполняется перед запуском фоновой задачи
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Загрузка данных с сервера...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }

        // выполняется после завершения фоновой задачи
        @Override
        protected void onPostExecute(String result) {
            EditText editText = (EditText)findViewById(R.id.etResponse);
            editText.setText(out.toString());
            dialog.dismiss();
            super.onPostExecute(result);
        }
    }
}

