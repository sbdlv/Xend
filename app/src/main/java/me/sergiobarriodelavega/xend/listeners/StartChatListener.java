package me.sergiobarriodelavega.xend.listeners;

import android.content.Intent;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import me.sergiobarriodelavega.xend.ChatActivity;

public class StartChatListener implements View.OnClickListener {
    private ArrayList<String> jids;
    private RecyclerView recycler;
    private Fragment fragment;

    public StartChatListener(ArrayList<String> jids, RecyclerView recycler, Fragment fragment) {
        this.jids = jids;
        this.recycler = recycler;
        this.fragment = fragment;
    }

    @Override
    public void onClick(View view) {
        //Load chat
        String jid = jids.get(recycler.getChildAdapterPosition(view));
        Intent i = new Intent(fragment.getContext(), ChatActivity.class);
        i.putExtra("user", jid);
        fragment.startActivity(i);
    }
}
