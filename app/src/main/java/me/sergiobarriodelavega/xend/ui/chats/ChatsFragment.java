package me.sergiobarriodelavega.xend.ui.chats;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.sergiobarriodelavega.xend.App;
import me.sergiobarriodelavega.xend.R;
import me.sergiobarriodelavega.xend.entities.XMPPUser;
import me.sergiobarriodelavega.xend.listeners.StartChatListener;
import me.sergiobarriodelavega.xend.recyclers.LastChattedUserAdapter;

public class ChatsFragment extends Fragment {
    private LastChattedUserAdapter userAdapter;
    private RecyclerView recycler;
    private View view;

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

    private class LoadLastUsers extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            return  App.getDb(getContext()).lastChattedUsersDAO().getAllLastChattedUsers();
        }

        @Override
        protected void onPostExecute(List<String> usersJIDs) {
            List<XMPPUser> usersList;

            if(usersJIDs == null ||usersJIDs.size() == 0){
                Toast.makeText(getContext(), "No recent chats", Toast.LENGTH_SHORT);
            } else {
                usersList = new ArrayList<>();

                for(String jid : usersJIDs){
                    usersList.add(new XMPPUser(jid));
                }

                recycler = view.findViewById(R.id.recylcerLastChattedUsers);

                //Recycler
                userAdapter = new LastChattedUserAdapter(usersList);
                userAdapter.setOnClickListener(new StartChatListener(usersList, recycler, ChatsFragment.this));
                recycler.setAdapter(userAdapter);
                recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            }

        }
    }
}