package com.silas.guiaes.io;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

/**
 * Created by silas on 09/09/14.
 */
public class CPBCliente extends AsyncHttpClient {
    private volatile static CPBCliente cpb = new CPBCliente();

    private static PersistentCookieStore myCookieStore;

    private static AsyncHttpClient client = new AsyncHttpClient();

    private CPBCliente() { }

    public static CPBCliente getInstace(Context c) {
        if(cpb == null) {
            synchronized (CPBCliente.class) {
                if(cpb == null) {
                    cpb = new CPBCliente();
                    myCookieStore = new PersistentCookieStore(c.getApplicationContext());
                    client.setCookieStore(myCookieStore);
                    client.setUserAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0");
                }
            }
        }
        return cpb;
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }
}