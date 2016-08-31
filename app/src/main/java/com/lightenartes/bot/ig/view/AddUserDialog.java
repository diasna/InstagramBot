package com.lightenartes.bot.ig.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.lightenartes.bot.ig.R;
import com.lightenartes.bot.ig.db.Users;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by diaxz <dias.arifin@gmail.com> on 10/25/15.
 */
public class AddUserDialog extends DialogFragment {

    @Bind(R.id.editText)
    EditText username;

    @Bind(R.id.editText2)
    EditText password;

    @Bind(R.id.switch1)
    Switch isFollow;

    AddUserDialogListener addUserDialogListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_account, null);

        ButterKnife.bind(this, view);

        builder.setTitle("Add User")
                .setView(view)
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Users users = new Users();
                                users.setUsername(username.getText().toString());
                                users.setPassword(password.getText().toString());
                                users.setIsFollow(isFollow.isChecked());
                                users.save();
                                addUserDialogListener.onUserAdded();
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }

    public void setAddUserDialogListener(AddUserDialogListener addUserDialogListener) {
        this.addUserDialogListener = addUserDialogListener;
    }

    public interface AddUserDialogListener {
        void onUserAdded();
    }
}