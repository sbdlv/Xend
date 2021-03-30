package me.sergiobarriodelavega.xend.recyclers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.sergiobarriodelavega.xend.R;
import me.sergiobarriodelavega.xend.entities.XMPPUser;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private List<XMPPUser> users;

    public UserAdapter(List<XMPPUser> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_user, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        XMPPUser user = users.get(position);
        holder.tvUserName.setText(user.getUserName());
        holder.tvUserEmail.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUserName, tvUserEmail;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            tvUserName = view.findViewById(R.id.tvUserName);
            tvUserEmail = view.findViewById(R.id.tvUserEmail);
        }
    }


}
