package com.lightenartes.bot.ig.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lightenartes.bot.ig.R;
import com.lightenartes.bot.ig.model.BotStatus;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by diaxz <dias.arifin@gmail.com> on 10/25/15.
 */
public class BotStatusAdapter extends RecyclerView.Adapter<BotStatusAdapter.BotStatusHolder>{

    List<BotStatus> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public BotStatusAdapter(Context context, List<BotStatus> data) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public BotStatusHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_row_status, parent, false);
        BotStatusHolder holder = new BotStatusHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(BotStatusHolder holder, int position) {
        BotStatus current = data.get(position);
        holder.icon.setImageResource(current.getIcon());
        holder.title.setText(current.getTitle());
        holder.value.setText(current.getValue());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class BotStatusHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.imageView2)
        ImageView icon;

        @Bind(R.id.textView5)
        TextView title;

        @Bind(R.id.textView6)
        TextView value;

        public BotStatusHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
