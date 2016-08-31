package com.lightenartes.bot.ig.activity.fragment;

/**
 * Created by Ravi on 29/07/15.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lightenartes.bot.ig.R;
import com.lightenartes.bot.ig.adapter.BotStatusAdapter;
import com.lightenartes.bot.ig.adapter.NavigationDrawerAdapter;
import com.lightenartes.bot.ig.db.Souls;
import com.lightenartes.bot.ig.db.Users;
import com.lightenartes.bot.ig.model.BotStatus;
import com.lightenartes.bot.ig.view.AddUserDialog;
import com.lightenartes.bot.ig.view.FindTargetDialog;
import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class HomeFragment extends Fragment {

    @Bind(R.id.viewB)
    RecyclerView recyclerView;

    @Bind(R.id.fab)
    FloatingActionButton floatingActionButton;

    @Bind(R.id.textView2)
    TextView lastUser;

    @Bind(R.id.textView3)
    TextView lastStatus;

    @Bind(R.id.textView4)
    TextView lastExecDate;

    private Users users;

    private BotStatusAdapter adapter;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        ButterKnife.bind(this, rootView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new FragmentDrawer.RecyclerTouchListener(getActivity(), recyclerView, new FragmentDrawer.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void loadData(Users users) {
        this.users = SugarRecord.findById(Users.class, users.getId());

        List<BotStatus> data = new ArrayList<>();
        DecimalFormat formatter = new DecimalFormat("#,###,###");

        long successCount = Souls.count(Souls.class, "status = ? AND users = ?", new String[]{"200", "" + users.getId()});
        long pendingCount = Souls.count(Souls.class, "status = ? AND users = ?", new String[]{"0", "" + users.getId()});
        long failCount = Souls.count(Souls.class, "status != ? AND status != ?  AND users = ?", new String[]{"200", "0", "" + users.getId()});

        data.add(new BotStatus(R.drawable.ic_favorite_black_24dp, "Following", this.users.getFollowing() + ""));
        data.add(new BotStatus(R.drawable.ic_group_add_black_24dp, "Followers", this.users.getFollowers() + ""));

        data.add(new BotStatus(R.drawable.ic_assignment_turned_in_black_24dp, "Success", formatter.format(successCount)));
        data.add(new BotStatus(R.drawable.ic_assignment_black_24dp, "Pending", formatter.format(pendingCount)));
        data.add(new BotStatus(R.drawable.ic_assignment_late_black_24dp, "Failed", formatter.format(failCount)));

        List<Souls> soulses = Select.from(Souls.class)
                .where(Condition.prop("users").eq(users.getId()))
                .limit("1")
                .orderBy("executedate DESC")
                .list();

        if (!soulses.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("cccc HH:mm");
            lastUser.setText(soulses.get(0).getUsername());
            lastStatus.setText(soulses.get(0).getStatus() + "");
            lastExecDate.setText(sdf.format(soulses.get(0).getExecutedate()));
        }

        adapter = new BotStatusAdapter(getActivity(), data);
        recyclerView.setAdapter(adapter);
    }

    public void showDialog() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FindTargetDialog newFragment = new FindTargetDialog();
        newFragment.setUsers(users);
        newFragment.setFindTargetDialogListener(new FindTargetDialog.FindTargetDialogListener() {
            @Override
            public void onFindTargetDone() {
                loadData(users);
            }
        });
        newFragment.show(fragmentManager, "dialog");
    }
}
