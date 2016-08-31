package com.lightenartes.bot.ig.view;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.lightenartes.bot.ig.R;
import com.lightenartes.bot.ig.db.Souls;
import com.lightenartes.bot.ig.db.Users;
import com.lightenartes.bot.ig.instagram.InstagramSession;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by diaxz <dias.arifin@gmail.com> on 10/25/15.
 */
public class FindTargetDialog extends DialogFragment {

    @Bind(R.id.editText)
    EditText uri;

    FindTargetDialogListener findTargetDialogListener;

    private Users users;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_find_target, null);
        ButterKnife.bind(this, view);
        builder.setTitle("Find Target")
                .setView(view)
                .setPositiveButton("Find",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final AsyncHttpClient client = new AsyncHttpClient();
                                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", "Absorbing Souls...", true);
                                client.get("http://api.instagram.com/publicapi/oembed/?url=" + uri.getText().toString(), new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onStart() {
                                        progressDialog.show();
                                    }

                                    @Override
                                    public void onFinish() {
                                        progressDialog.dismiss();
                                    }

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        try {
                                            JSONObject obj = new JSONObject(new String(responseBody));
                                            absorbSoul(client, obj.getString("media_id"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        Log.d("Absorb Fail [" + statusCode + "]", new String(responseBody));
                                    }
                                });
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        loadClipboard();
        return builder.create();
    }

    public void setFindTargetDialogListener(FindTargetDialogListener findTargetDialogListener) {
        this.findTargetDialogListener = findTargetDialogListener;
    }

    public interface FindTargetDialogListener {
        void onFindTargetDone();
    }

    private void absorbSoul(AsyncHttpClient client, String mediaId) {
        InstagramSession instagramSession = new InstagramSession(activity.getApplicationContext());
        client.get("https://api.instagram.com/v1/media/" + mediaId + "/likes?access_token=" + instagramSession.getAccessToken(), new AsyncHttpResponseHandler() {
            final ProgressDialog dialog = ProgressDialog.show(activity, "", "Capturing Souls...", true);

            @Override
            public void onStart() {
                dialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONArray souls = new JSONObject(new String(responseBody)).getJSONArray("data");
                    int captured = 0;
                    for (int i = 0; i < souls.length(); i++) {
                        JSONObject soulObj = souls.getJSONObject(i);
                        if (Souls.find(Souls.class, "instagram = ? AND users = ?", soulObj.getLong("id") + "", users.getId() + "").isEmpty()) {
                            Souls soul = new Souls(
                                    soulObj.getLong("id"),
                                    soulObj.getString("username"),
                                    soulObj.getString("full_name"),
                                    users
                            );
                            soul.save();
                            captured++;
                        }
                    }
                    Toast.makeText(activity, "Soul Captured :" + captured, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    findTargetDialogListener.onFindTargetDone();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("Absorb Soul Fail", "[" + statusCode + "]");
            }
        });
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    private void loadClipboard() {
        ClipboardManager myClipboard;
        myClipboard = (ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
        ClipData abc = myClipboard.getPrimaryClip();
        if (abc != null) {
            ClipData.Item item = abc.getItemAt(0);
            String text = item.getText().toString();

            if (text.startsWith("https://instagram.com/p/") || text.startsWith("https://www.instagram.com/p/")) {
                uri.setText(text);
            }
        }
    }

    Activity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }
}