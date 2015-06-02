package com.silas.meditacao.io;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

/**
 * Created by silas on 09/09/14.
 */
public class HTTPCliente extends AsyncHttpClient {
    private volatile static HTTPCliente cpb = new HTTPCliente();

    private static PersistentCookieStore myCookieStore;

    private static AsyncHttpClient client = new AsyncHttpClient();

    private HTTPCliente() { }

    public static HTTPCliente getInstace(Context c) {
        if(cpb == null) {
            synchronized (HTTPCliente.class) {
                if(cpb == null) {
                    cpb = new HTTPCliente();
                    myCookieStore = new PersistentCookieStore(c.getApplicationContext());
                    client.setCookieStore(myCookieStore);
                    client.setUserAgent("Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
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