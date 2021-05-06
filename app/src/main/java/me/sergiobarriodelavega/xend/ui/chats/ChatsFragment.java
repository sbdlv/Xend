package me.sergiobarriodelavega.xend.ui.chats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.sergiobarriodelavega.xend.App;
import me.sergiobarriodelavega.xend.LocalBroadcastsEnum;
import me.sergiobarriodelavega.xend.R;
import me.sergiobarriodelavega.xend.entities.XMPPUser;
import me.sergiobarriodelavega.xend.listeners.StartChatListener;
import me.sergiobarriodelavega.xend.recyclers.LastChattedUserAdapter;
import me.sergiobarriodelavega.xend.room.LastChattedUser;

public class ChatsFragment extends Fragment {
    private LastChattedUserAdapter userAdapter;
    private RecyclerView recycler;
    private View view;
    private BroadcastReceiver broadcastReceiverChatsDeleted;
    private LinearLayout llNoRecentChats;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chats, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.view = view;
        new LoadLastUsers().execute();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiverChatsDeleted);
        super.onDestroy();
    }

    private class LoadLastUsers extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            List<LastChattedUser> lastChattedUsers = App.getDb(getContext()).lastChattedUsersDAO().getAllLastChattedUsers();

            llNoRecentChats = view.findViewById(R.id.llNoRecentChats);

            if(lastChattedUsers == null ||lastChattedUsers.size() == 0){
                llNoRecentChats.setVisibility(View.VISIBLE);
            } else {
                recycler = view.findViewById(R.id.recylcerLastChattedUsers);

                //Extract JIDs
                ArrayList<String> jids = new ArrayList<>();

                for(LastChattedUser user: lastChattedUsers){
                    jids.add(user.jid);
                }

                //Listener
                StartChatListener startChatListener = new StartChatListener(jids, recycler, ChatsFragment.this);

                //Recycler
                userAdapter = new LastChattedUserAdapter(lastChattedUsers);
                userAdapter.setOnClickListener(startChatListener);
                recycler.setAdapter(userAdapter);
                recycler.setLayoutManager(new LinearLayoutManager(getContext()));

                //Register BroadcastReceiver
                broadcastReceiverChatsDeleted = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        userAdapter.clear();
                        userAdapter.notifyDataSetChanged();
                        llNoRecentChats.setVisibility(View.VISIBLE);
                    }
                };

                LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiverChatsDeleted,
                        new IntentFilter(LocalBroadcastsEnum.RECENT_CHATS_DELETED));
            }

            return null;
        }
    }
}