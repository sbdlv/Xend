package me.sergiobarriodelavega.xend;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.impl.JidCreate;

import java.io.IOException;

import me.sergiobarriodelavega.xend.entities.XMPPUser;

public class ProfileInfoActivity extends AppCompatActivity {
    private TextView tvProfileUserName, tvProfileName;
    private ImageView ivProfileImage;

    private XMPPUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_info);

        //Bundle
        user = (XMPPUser) getIntent().getExtras().getSerializable("user");

        //Toolbar
        getSupportActionBar().setTitle(user.getJid());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Views
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileUserName= findViewById(R.id.tvProfileUserName);
        ivProfileImage = findViewById(R.id.ivProfileImage);

        VCardManager vCardManager;
        VCard vCard;
        try {
            vCardManager = VCardManager.getInstanceFor(App.getConnection());
            vCard = vCardManager.loadVCard(JidCreate.entityBareFrom(user.getJid()));

            //Show data
            tvProfileUserName.setText(user.getJid());
            //If user has no VCard set, use the named used during registration, if not, use undefined name string resource
            if(getFullName(vCard) == null){
                if(user.getUserName() == null){
                    tvProfileName.setText(R.string.user_no_name);
                } else {
                    tvProfileName.setText(user.getUserName());
                }
            } else {
                tvProfileName.setText(getFullName(vCard));
            }

            //Image
            if(vCard.getAvatar() != null){
                byte[] imageData = vCard.getAvatar();
                Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                ivProfileImage.setImageBitmap(bmp);
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

    /**
     * Gets the full name First + Middel + Last name.
     * @param vCard
     * @return If first name is null, returns null, else the full name without null values
     */
    private static String getFullName(VCard vCard){
        StringBuilder fullName;
        if (vCard.getFirstName() == null){
            return null;
        }

        fullName = new StringBuilder(vCard.getFirstName());

        if(vCard.getMiddleName() != null){
            fullName.append(" ");
            fullName.append(vCard.getMiddleName());
        }

        if(vCard.getLastName()!= null){
            fullName.append(" ");
            fullName.append(vCard.getLastName());
        }

        return fullName.toString();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
