package com.silas.guiaes.io;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

/**
 * Created by silas on 09/09/14.
 */
public class CPBCliente {
    //private static final String BASE_URL = "http://cpbmais.cpb.com.br/";

    private static PersistentCookieStore myCookieStore;

    private static AsyncHttpClient client = new AsyncHttpClient();

    public CPBCliente() {
        client.setCookieStore(myCookieStore);
    }


    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

    /*private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }*/
}