package com.silas.meditacao.io;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.ViewPager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by silas on 22/06/15.
 */
public class ProcessaMeditacoesTask extends AsyncTask<HashMap<Integer, String>, Void, Void> {
    private Context mContext;
    private ProgressDialog progress;
    private ViewPager viewPager;

    public ProcessaMeditacoesTask(Context context, ViewPager vPager) {
        mContext = context;
        viewPager = vPager;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = new ProgressDialog(mContext);
        progress.setIndeterminate(true);
        progress.setTitle("Recebendo poder!");
        progress.setMessage("Ore pelo meu criador e aguarde...");
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    protected Void doInBackground(HashMap<Integer, String>... tmp) {
        HashMap<Integer, String> urls = tmp[0];
        int tentativa = 1;

        for (Map.Entry<Integer, String> url : urls.entrySet()) {
            processaMeditacoes(url, tentativa);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progress.dismiss();
        viewPager.getAdapter().notifyDataSetChanged();
        super.onPostExecute(aVoid);
    }

    private void processaMeditacoes(Map.Entry<Integer, String> entry, int tentativa) {
        URL url;
        BufferedReader reader = null;
        StringBuffer stringBuffer;
        ExtraiMeditacao extrator = new ExtraiMeditacao(mContext);

        try {

            // create the HttpURLConnection
            url = new URL(entry.getValue());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // just want to do an HTTP GET here
            connection.setRequestMethod("GET");

            // uncomment this if you want to write output to this url
            //connection.setDoOutput(true);

            // give it 15 seconds to respond
            connection.setReadTimeout(15 * 1000);

            if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
                connection.setRequestProperty("Connection", "close");
            }

            connection.connect();

            // read the output from the server
            InputStream is = connection.getInputStream();

            if (is != null) {
                reader = new BufferedReader(new InputStreamReader(is));
                stringBuffer = new StringBuffer();

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                String html = stringBuffer.toString();

                extrator.processaExtracao(html, entry.getKey());
            }

        } catch (IOException e) {
            e.printStackTrace();
            if (tentativa <= 3) {
                tentativa++;
                processaMeditacoes(entry, tentativa);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            // close the reader; this can throw an exception too, so
            // wrap it in another try/catch block.
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }
}