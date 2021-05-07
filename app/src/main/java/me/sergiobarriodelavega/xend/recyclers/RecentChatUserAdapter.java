package me.sergiobarriodelavega.xend.recyclers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import me.sergiobarriodelavega.xend.room.RecentChatUser;

public class RecentChatUserAdapter extends RecyclerView.Adapter<RecentChatUserAdapter.ViewHolder>{

    private List<RecentChatUser> users;
    private View.OnClickListener onClickListener;

    public RecentChatUserAdapter(List<RecentChatUser> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_recent_chat_user, parent, false);
        view.setOnClickListener(onClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentChatUser user = users.get(position);
        holder.tvUserName.setText(user.jid);

        //Set User Picture
        try {
            VCard vCard = App.getXMPPUser(user.jid);
            Bitmap userPicture = App.avatarToBitmap(vCard);
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
        private final TextView tvUserName;
        private final ImageView ivUserPicture;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            tvUserName = view.findViewById(R.id.tvUserName);
            ivUserPicture = view.findViewById(R.id.ivUserPicture);
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void clear(){
        users.clear();
    }
}
