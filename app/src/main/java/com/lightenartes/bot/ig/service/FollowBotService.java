package com.lightenartes.bot.ig.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.lightenartes.bot.ig.CommonUtil;
import com.lightenartes.bot.ig.db.Souls;
import com.lightenartes.bot.ig.db.Users;
import com.lightenartes.bot.ig.instagram.InstagramSession;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.cookie.Cookie;

/**
 * Created by diaxz <dias.arifin@gmail.com> on 10/18/15.
 */
public class FollowBotService extends Service {

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
        final CookieStore cookieStore = new PersistentCookieStore(this);
        final AsyncHttpClient client = new AsyncHttpClient();

        List<Users> users = Users.listAll(Users.class);

        for (Users u : users) {
            doLogin(cookieStore, client, this, u);
        }

        doLike(client, this);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int min = Integer.parseInt(pref.getString("random_interval_min", "60"));
        int max = Integer.parseInt(pref.getString("random_interval_max", "80"));

        CommonUtil.setOneTimeAlarm(this, min, max);

        return super.onStartCommand(intent, flags, startId);
    }

    public static void doLike(final AsyncHttpClient client, final Context context) {
        final String token = new InstagramSession(context).getAccessToken();
        client.get("https://api.instagram.com/v1/users/self/feed?access_token=" + token + "&count=1", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONArray target = new JSONObject(new String(responseBody)).getJSONArray("data");
                    for (int i = 0; i < target.length(); i++) {
                        JSONObject object = target.getJSONObject(i);
                        client.removeHeader("Referer");
                        client.addHeader("Referer", "https://instagram.com/" + object.getString("link") + "/");
                        client.post("https://instagram.com/web/likes/" + object.getString("id") + "/like/", new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                Log.d("SUCCESS LIKE", new String(response));
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                                Log.d("Response Like [" + statusCode + "]", new String(errorResponse));
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("GET LIKE", "[" + statusCode + "]");
            }
        });
    }

    public static void doLogin(CookieStore cookieStore, final AsyncHttpClient client, final Context context, final Users u) {
        client.setCookieStore(cookieStore);

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
                        Log.d("SUCCESS LOGIN", new String(response));
                        doFollow(client, context, u);
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

    public static void doFollow(final AsyncHttpClient client, final Context context, final Users u) {

        List<Souls> soulses = Select.from(Souls.class)
                .where(Condition.prop("status").eq("0"))
                .limit("1")
                .list();

        for (final Souls souls : soulses) {
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
                    Intent intent = new Intent(DONE_EVENT);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    Log.d("Response Follow [" + statusCode + "]", new String(errorResponse));
                    souls.setExecutedate(new Date());
                    souls.setStatus(statusCode);
                    souls.setUsers(u);
                    souls.save();
                    for (Header header : headers) {
                        Log.d("FAIL FOLLOW", header.getName() + " " + header.getValue());
                    }
                    Intent intent = new Intent(DONE_EVENT);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            });
        }
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
