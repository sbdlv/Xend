package me.sergiobarriodelavega.xend;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.impl.JidCreate;

import java.io.IOException;

public class ProfileInfoActivity extends AppCompatActivity {
    private TextView tvProfileUserName, tvProfileName, tvEmail, tvPersonalAddress;
    private ImageView ivProfileImage;

    private String userJID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_info);

        //Bundle
        userJID = getIntent().getExtras().getString("user");

        //Toolbar
        //user = App.getXMPPUser(userJID);
        getSupportActionBar().setTitle(userJID);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Views
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileUserName= findViewById(R.id.tvProfileUserName);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvEmail = findViewById(R.id.tvEmail);
        tvPersonalAddress = findViewById(R.id.tvPersonalAddress);

        new LoadVCard().execute();

    }

    private class LoadVCard extends AsyncTask<Void, Void, VCard> {

        @Override
        protected VCard doInBackground(Void... voids) {
            VCardManager vCardManager;
            VCard vCard;

            try {
                vCardManager = VCardManager.getInstanceFor(App.getConnection());
                vCard = vCardManager.loadVCard(JidCreate.entityBareFrom(userJID));

                return vCard;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(VCard vCard) {
            if(vCard == null){
                Toast.makeText(getApplicationContext(),"Error loading profile", Toast.LENGTH_SHORT).show();
            }

            //Show data
            tvProfileUserName.setText(userJID);
            //If user has no VCard set, use the named used during registration, if not, use undefined name string resource
            if(getFullName(vCard) == null){
                tvProfileName.setText(userJID);
            } else {
                tvProfileName.setText(getFullName(vCard));
            }

            tvEmail.setText(vCard.getEmailHome());
            tvPersonalAddress.setText(vCard.getAddressFieldHome("STREET"));

            //Image
            if(vCard.getAvatar() != null){
                byte[] imageData = vCard.getAvatar();
                Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                ivProfileImage.setImageBitmap(bmp);
            }
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
