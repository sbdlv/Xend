package me.sergiobarriodelavega.xend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileEditActivity extends AppCompatActivity {
    private VCard vCard;
    private VCardManager vCardManager;
    private EditText txtFirstName, txtMiddleName, txtLastName;
    private TextView tvProfileUserName;
    private ImageView ivProfileImage;
    private static final int PICTURE_SELECT = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        ivProfileImage = findViewById(R.id.ivProfileImage);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtMiddleName = findViewById(R.id.txtMiddleName);
        txtLastName = findViewById(R.id.txtLastName);
        tvProfileUserName = findViewById(R.id.tvProfileUserName);

        //Toolbar
        getSupportActionBar().setTitle(R.string.edit_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            tvProfileUserName.setText(App.getConnection().getUser().asBareJid().toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Load data
        try {
            vCardManager = VCardManager.getInstanceFor(App.getConnection());
            vCard = vCardManager.loadVCard();

            //Load data from VCard
            if(vCard.getAvatar() != null){
                byte[] imageData = vCard.getAvatar();
                Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                ivProfileImage.setImageBitmap(bmp);
            }

            txtFirstName.setText(vCard.getFirstName());
            txtMiddleName.setText(vCard.getMiddleName());
            txtLastName.setText(vCard.getLastName());

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

    public boolean saveChanges(MenuItem view){

        //Full name
        vCard.setFirstName(txtFirstName.getText().toString());
        vCard.setMiddleName(txtMiddleName.getText().toString());
        vCard.setLastName(txtLastName.getText().toString());

        //Save VCard
        Toast.makeText(this, R.string.saving_changes, Toast.LENGTH_LONG).show();
        new SaveVCard().execute();

        return false;
    }

    public void changeImage(View view){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,getString(R.string.select_a_picture)), PICTURE_SELECT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PICTURE_SELECT:
                //If the user didn't cancel the action
                if(data != null){
                    Uri image = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
                        ivProfileImage.setImageBitmap(bitmap);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        vCard.setAvatar(byteArrayOutputStream.toByteArray());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error while changing picture", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_profile_edit, menu);
        return super.onCreateOptionsMenu(menu);
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

    private class SaveVCard extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                vCardManager.saveVCard(vCard);
                return true;
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean successful) {
            super.onPostExecute(successful);
            if(successful){
                Toast.makeText(ProfileEditActivity.this, R.string.data_saved, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileEditActivity.this, R.string.error_on_saving, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
