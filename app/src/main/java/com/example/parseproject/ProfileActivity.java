package com.example.parseproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    TextView usernameTextView;
    Button uploadButton;
    ImageView profileImageView;
    LinearLayout linearLayout;
    String username;

     int SELECT_PICTURE = 200;


      @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.share_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.profile)
        {
             Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
              intent.putExtra("Username",username);
            startActivity(intent);

        }

        if(item.getItemId()==R.id.share) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            } else {
            getPhoto(); }
        }
        else if(item.getItemId()==R.id.logout)
        {
            ParseUser.logOut();
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

       public void getPhoto()
    {
          Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1)
        {
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                getPhoto();
            }
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        linearLayout=findViewById(R.id.linearLayout);

        setTitle("Instagram");

        uploadButton=findViewById(R.id.uploadButton);
        profileImageView=findViewById(R.id.profileImageView);

        usernameTextView=findViewById(R.id.usernameTextView);

        Intent intent=getIntent();
        username=intent.getStringExtra("Username");

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        usernameTextView.setText("Username:"+ username);

        ParseQuery<ParseObject> query=new ParseQuery<ParseObject>("Image");
         query.whereEqualTo("username",username);
         query.orderByDescending("createdAt");
         query.findInBackground(new FindCallback<ParseObject>() {

             @Override
             public void done(List<ParseObject> objects, ParseException e) {
                 if(e==null && objects.size()>0){
                     for (ParseObject object:objects)
                     {
                         ParseFile file=(ParseFile) object.get("image");

                         assert file != null;
                         file.getDataInBackground(new GetDataCallback() {
                             @Override
                             public void done(byte[] data, ParseException e) {
                                 if(e==null && data!=null)
                                 {
                                     Bitmap bitmap= BitmapFactory.decodeByteArray(data,0, data.length);
                                      ImageView imageView=new ImageView(getApplicationContext());

                                     imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                            ));

                             imageView.setImageBitmap(bitmap);
                                               linearLayout.addView(imageView);
                                 }
                             }
                         });
                     }
                 }
             }
         });


    }

    void imageChooser() {

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImageUri = data.getData();
        Uri selectedImageUri2 = data.getData();

        if (resultCode == RESULT_OK) {

            if (requestCode == SELECT_PICTURE) {


                if (null != selectedImageUri2) {

                    profileImageView.setImageURI(selectedImageUri2);
                    try {
                        Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImageUri2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
          if(requestCode==1 && resultCode==RESULT_OK && data!=null)
            {
                try {
                    Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImageUri);

                    ByteArrayOutputStream stream=new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);

                    byte[] bytearray=stream.toByteArray();
                    ParseFile file=new ParseFile("image.png",bytearray);
                    ParseObject object=new ParseObject("Image");
                    object.put("image",file);
                    object.put("username", ParseUser.getCurrentUser().getUsername());

                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null)
                            {
                                Toast.makeText(ProfileActivity.this, "Image has been shared", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(ProfileActivity.this, "There has been an issue uploading the image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
    }
}