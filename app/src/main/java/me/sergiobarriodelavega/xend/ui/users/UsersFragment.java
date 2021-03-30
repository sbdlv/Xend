package me.sergiobarriodelavega.xend.ui.users;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.BooleanFormField;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.DomainBareJid;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import me.sergiobarriodelavega.xend.App;
import me.sergiobarriodelavega.xend.R;
import me.sergiobarriodelavega.xend.entities.XMPPUser;
import me.sergiobarriodelavega.xend.recyclers.UserAdapter;

public class UsersFragment extends Fragment {
    private RecyclerView recycler;
    private View view;
    private ProgressBar pbUsers;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_users, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        pbUsers = view.findViewById(R.id.pbUsers);
        new ScanUsersTask().execute();
    }

    private class ScanUsersTask extends AsyncTask<Void, Void, List<XMPPUser>>{



        @Override
        protected List<XMPPUser> doInBackground(Void... voids) {
            //XMPP Users query
            //TODO Better search form structure
            ArrayList<XMPPUser> users = new ArrayList<>();
            UserSearchManager userSearchManager = null;
            try {

                userSearchManager = new UserSearchManager(App.getConnection());

                List<DomainBareJid> searchServices = userSearchManager.getSearchServices();
                DataForm build2 = DataForm.builder(DataForm.Type.result).addField(FormField.builder("search").setValue("*").build()).addField(BooleanFormField.booleanBuilder("Name").setValue(true).build()).build();
                ReportedData searchResults = userSearchManager.getSearchResults(build2, searchServices.get(1));

                for(ReportedData.Row row : searchResults.getRows()) {
                    users.add(new XMPPUser(row.getValues("Username").toString(),row.getValues("Email").toString()));
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
            return users;
        }

        @Override
        protected void onPostExecute(List<XMPPUser> users) {
            recycler = view.findViewById(R.id.recyclerUsers);

            //Recycler
            recycler.setAdapter(new UserAdapter(users));
            recycler.setLayoutManager(new LinearLayoutManager(getContext()));

            pbUsers.setVisibility(View.GONE);
        }
    }
}
