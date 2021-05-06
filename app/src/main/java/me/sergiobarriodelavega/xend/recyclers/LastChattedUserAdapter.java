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
import me.sergiobarriodelavega.xend.room.LastChattedUser;

public class LastChattedUserAdapter extends RecyclerView.Adapter<LastChattedUserAdapter.ViewHolder>{

    private List<LastChattedUser> users;
    private View.OnClickListener onClickListener;

    public LastChattedUserAdapter(List<LastChattedUser> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_last_chatted_user, parent, false);
        view.setOnClickListener(onClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LastChattedUser user = users.get(position);
        holder.tvUserName.setText(user.jid);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUserName;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            tvUserName = view.findViewById(R.id.tvUserName);
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void clear(){
        users.clear();
    }
}
