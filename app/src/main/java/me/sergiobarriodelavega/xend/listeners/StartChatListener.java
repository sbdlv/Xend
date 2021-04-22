package me.sergiobarriodelavega.xend.listeners;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.sergiobarriodelavega.xend.ChatActivity;
import me.sergiobarriodelavega.xend.entities.XMPPUser;

public class StartChatListener implements View.OnClickListener {
    private List<XMPPUser> users;
    private RecyclerView recycler;
    private Fragment fragment;

    public StartChatListener(List<XMPPUser> users, RecyclerView recycler, Fragment fragment) {
        this.users = users;
        this.recycler = recycler;
        this.fragment = fragment;
    }

    @Override
    public void onClick(View view) {
        //Load chat
        XMPPUser user = users.get(recycler.getChildAdapterPosition(view));
        Intent i = new Intent(fragment.getContext(), ChatActivity.class);
        i.putExtra("user", user);
        fragment.startActivity(i);
    }
}
