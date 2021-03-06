package me.sergiobarriodelavega.xend.recyclers;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.IOException;
import java.util.List;

import me.sergiobarriodelavega.xend.App;
import me.sergiobarriodelavega.xend.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private List<String> users;
    private View.OnClickListener onClickListener;

    public UserAdapter(List<String> jids) {
        this.users = jids;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_user, parent, false);
        view.setOnClickListener(onClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String jid = users.get(position);
        VCard user;

        try {
            //Put values on labels
            user = App.getUserVCard(jid);

            //Name and JID
            if(user.getFirstName() == null || user.getFirstName().isEmpty()){
                holder.tvUserName.setText(jid);
            } else {
                holder.tvUserName.setText(user.getFirstName());
            }
            holder.tvUserJID.setText(jid);

            //Picture
            Bitmap userPicture = App.avatarToBitmap(user);
            if (userPicture != null){
                ImageViewCompat.setImageTintList(holder.ivUserPicture, null); //Remove tint
                holder.ivUserPicture.setImageBitmap(userPicture);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUserName, tvUserJID;
        private final ImageView ivUserPicture;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            tvUserName = view.findViewById(R.id.tvUserName);
            tvUserJID = view.findViewById(R.id.tvUserJID);
            ivUserPicture = view.findViewById(R.id.ivUserPicture);
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
