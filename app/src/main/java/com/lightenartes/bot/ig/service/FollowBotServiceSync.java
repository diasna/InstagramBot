package com.lightenartes.bot.ig.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.lightenartes.bot.ig.CommonUtil;
import com.lightenartes.bot.ig.db.Souls;
import com.lightenartes.bot.ig.db.Users;
import com.lightenartes.bot.ig.instagram.InstagramSession;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.apache.http.client.params.ClientPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.impl.client.BasicCookieStore;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;

/**
 * Created by diaxz <dias.arifin@gmail.com> on 11/1/15.
 */
public class FollowBotServiceSync extends Service {

    public static final String DONE_EVENT = "FUCKING_DONE";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {

                List<Users> users = Users.listAll(Users.class);
                for (Users u : users) {

                    SyncHttpClient client = new SyncHttpClient();

                    PersistentCookieStore cookieStore = new PersistentCookieStore(getApplicationContext());

                    client.setCookieStore(cookieStore);
                    client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

                    initCall(cookieStore, client, getApplicationContext(), u);

                    getUserInfo(client, getApplicationContext(), u);
                }

                return "done";
            }

            @Override
            protected void onPostExecute(String s) {
                Log.d("RESULT", s);
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int min = Integer.parseInt(pref.getString("random_interval_min", "60"));
                int max = Integer.parseInt(pref.getString("random_interval_max", "80"));
                CommonUtil.setOneTimeAlarm(getApplicationContext(), min, max);
                Intent intent = new Intent(DONE_EVENT);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        }.execute("");

        return super.onStartCommand(intent, flags, startId);
    }

    public static void initCall(final CookieStore cookieStore, final SyncHttpClient client, final Context context, final Users u) {
        client.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; C6903 Build/14.6.A.0.368) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36");
        client.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        client.addHeader("Accept-Language", "en-US,en;q=0.5");
        client.get("https://instagram.com/accounts/login/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("INIT", "SUCCESS [" + statusCode + "]");
                doLogin(cookieStore, client, context, u);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("INIT", "FAILURE [" + statusCode + "]");
            }
        });
    }

    public static void doLogin(final CookieStore cookieStore, final SyncHttpClient client, final Context context, final Users u) {
        client.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; C6903 Build/14.6.A.0.368) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36");
        client.addHeader("X-CSRFToken", getCookie(cookieStore.getCookies(), "csrftoken"));
        client.addHeader("Origin", "https://instagram.com");
        client.addHeader("X-Instagram-AJAX", "1");
        client.addHeader("Referer", "https://instagram.com/accounts/login/");
        client.addHeader("X-Requested-With", "XMLHttpRequest");
        client.addHeader("Accept-Language", "en-US,en;q=0.8");
        client.addHeader("Accept", "*/*");
        client.get("https://instagram.com/accounts/login/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                RequestParams params = new RequestParams();
                Log.d("Login", u.getUsername() + ":" + u.getPassword());
                params.put("username", u.getUsername());
                params.put("password", u.getPassword());
                client.post("https://instagram.com/accounts/login/ajax/", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        doFollow(client, u);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        Log.d("FAIL LOGIN", "[" + statusCode + "]");
                        Log.d("Response", new String(errorResponse));
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.d("FAIL INIT", "[" + statusCode + "]");
            }
        });
    }

    public static void doFollow(final SyncHttpClient client, final Users u) {
        final Souls souls = Select.from(Souls.class)
                .where(Condition.prop("status").eq("0"))
                .limit("1")
                .first();
        if (souls != null) {
            client.removeHeader("Referer");
            client.addHeader("Referer", "https://instagram.com/" + souls.getUsername() + "/");
            client.post("https://instagram.com/web/friendships/" + souls.getInstagram() + "/follow/", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    Log.d("SUCCESS FOLLOW", new String(response));
                    souls.setExecutedate(new Date());
                    souls.setStatus(statusCode);
                    souls.setUsers(u);
                    souls.save();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    Log.d("Response Follow [" + statusCode + "]", new String(errorResponse));
                    souls.setExecutedate(new Date());
                    souls.setStatus(statusCode);
                    souls.setUsers(u);
                    souls.save();
                }

            });
        }
    }

    public static void getUserInfo(final SyncHttpClient client, final Context context, final Users u) {
        final InstagramSession is = new InstagramSession(context);
        client.get("https://api.instagram.com/v1/users/" + is.getId() + "?access_token=" + is.getAccessToken(), new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject info = new JSONObject(new String(responseBody)).getJSONObject("data").getJSONObject("counts");
                    u.setFollowers(info.getInt("followed_by"));
                    u.setFollowing(info.getInt("follows"));
                    SugarRecord.saveInTx(u);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("FAIL USER INFO", "[" + statusCode + "]");
                Log.d("Response", new String(responseBody));
            }
        });
    }

    public static String getCookie(List<Cookie> cookies, String name) {
        for (Cookie cookie : cookies) {
            if (name.equalsIgnoreCase(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return "";
    }
}
