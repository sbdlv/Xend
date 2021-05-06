package me.sergiobarriodelavega.xend.ui.users;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.BooleanFormField;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.DomainBareJid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.sergiobarriodelavega.xend.App;
import me.sergiobarriodelavega.xend.R;
import me.sergiobarriodelavega.xend.listeners.StartChatListener;
import me.sergiobarriodelavega.xend.recyclers.UserAdapter;

public class UsersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView recycler;
    private View view;
    private ProgressBar pbUsers;
    private UserAdapter userAdapter;
    private TextView tvNoUsersFound;
    private SwipeRefreshLayout swRefreshUsers;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_users, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        pbUsers = view.findViewById(R.id.pbUsers);
        tvNoUsersFound = view.findViewById(R.id.tvNoUsersFound);
        swRefreshUsers = view.findViewById(R.id.swRefreshUsers);
        swRefreshUsers.setOnRefreshListener(this);
        new ScanUsersTask().execute();
    }

    @Override
    public void onRefresh() {
        new ScanUsersTask().execute();
    }

    private class ScanUsersTask extends AsyncTask<Void, Void, ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            //XMPP Users query
            //TODO Better search form structure and better managing of Exceptions (Warning messages)
            ArrayList<String> jids = new ArrayList<>();
            UserSearchManager userSearchManager;
            try {

                userSearchManager = new UserSearchManager(App.getConnection());

                List<DomainBareJid> searchServices = userSearchManager.getSearchServices();
                DataForm build2 = DataForm.builder(DataForm.Type.result).addField(FormField.builder("search").setValue("*").build()).addField(BooleanFormField.booleanBuilder("Name").setValue(true).build()).build();
                ReportedData searchResults = userSearchManager.getSearchResults(build2, searchServices.get(1));

                String userName, jid;
                for(ReportedData.Row row : searchResults.getRows()) {
                    //TODO Check that the app user is not saved on the list
                    jid = row.getValues("jid").toString();
                    jid = jid.substring(1, jid.length() - 1);

                    userName = row.getValues("Username").toString();
                    userName = userName.substring(1, userName.length() - 1);

                    jids.add(jid);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            return jids;
        }

        @Override
        protected void onPostExecute(ArrayList<String> jids) {
            if(jids.size() == 0){
                tvNoUsersFound.setVisibility(View.VISIBLE);
            } else {
                tvNoUsersFound.setVisibility(View.GONE);
                recycler = view.findViewById(R.id.recyclerUsers);

                //Recycler
                StartChatListener startChatListener = new StartChatListener(jids ,recycler, UsersFragment.this);
                userAdapter = new UserAdapter(jids);
                userAdapter.setOnClickListener(startChatListener);
                recycler.setAdapter(userAdapter);
                recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            pbUsers.setVisibility(View.GONE);

            swRefreshUsers.setRefreshing(false);
        }

    }
}
