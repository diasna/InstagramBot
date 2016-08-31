package com.lightenartes.bot.ig.adapter;

/**
 * Created by Ravi on 29/07/15.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lightenartes.bot.ig.db.Users;

import java.util.Collections;
import java.util.List;

import com.lightenartes.bot.ig.R;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Users> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public NavigationDrawerAdapter(Context context, List<Users> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
            return new MyViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.nav_drawer_row_add, parent, false);
            return new MyViewHolder2(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < data.size()) {
            Users current = data.get(position);
            ((MyViewHolder) holder).title.setText("@" + current.getUsername());
        }
    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < data.size())
            return 0;
        return 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }

    class MyViewHolder2 extends RecyclerView.ViewHolder {

        public MyViewHolder2(View itemView) {
            super(itemView);
        }
    }
}
