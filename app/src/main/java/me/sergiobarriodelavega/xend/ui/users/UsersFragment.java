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

import com.google.android.material.snackbar.Snackbar;

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
import me.sergiobarriodelavega.xend.entities.XMPPUser;
import me.sergiobarriodelavega.xend.recyclers.UserAdapter;

public class UsersFragment extends Fragment implements View.OnClickListener{
    private RecyclerView recycler;
    private View view;
    private ProgressBar pbUsers;
    private UserAdapter userAdapter;
    private ArrayList<XMPPUser> usersList;
    private View.OnClickListener onClickListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_users, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        pbUsers = view.findViewById(R.id.pbUsers);
        onClickListener = this;
        new ScanUsersTask().execute();
    }

    @Override
    public void onClick(View view) {
        //Load chat
        //TODO Open chat activity to selected user
        Snackbar.make(getView(),"Chat with " + usersList.get(recycler.getChildAdapterPosition(view)).getUserName(),Snackbar.LENGTH_LONG).show();
    }

    private class ScanUsersTask extends AsyncTask<Void, Void, List<XMPPUser>>{

        @Override
        protected List<XMPPUser> doInBackground(Void... voids) {
            //XMPP Users query
            //TODO Better search form structure and better managing of Exceptions (Warning messages)
            ArrayList<XMPPUser> users = new ArrayList<>();
            UserSearchManager userSearchManager;
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
            usersList = (ArrayList<XMPPUser>) users;
            recycler = view.findViewById(R.id.recyclerUsers);

            //Recycler
            userAdapter = new UserAdapter(users);
            userAdapter.setOnClickListener(onClickListener);
            recycler.setAdapter(userAdapter);
            recycler.setLayoutManager(new LinearLayoutManager(getContext()));

            pbUsers.setVisibility(View.GONE);
        }

    }
}
