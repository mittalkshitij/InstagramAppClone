package com.example.parseproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    ListView listView;

    Intent intent;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        setTitle("Instagram");
        listView=findViewById(R.id.listView);
        final ArrayList<String> users=new ArrayList<String>();
        final ArrayAdapter arrayAdapter=new ArrayAdapter(this, android.R.layout.simple_list_item_1,users);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getApplicationContext(),UserFeedActivity.class);
                intent.putExtra("username",users.get(i));
                startActivity(intent);
            }
        });

        ParseQuery<ParseUser> query=ParseUser.getQuery();
        query.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e==null)
                {
                    if(objects.size()>0)
                    {
                        for(ParseUser user:objects)
                        {
                            users.add(user.getUsername());
                        }
                        listView.setAdapter(arrayAdapter);
                    }
                }else
                {
                    e.printStackTrace();
                }
            }
        });

        intent=getIntent();
        username=intent.getStringExtra("Username");
    }

    public void getPhoto()
    {
          Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);

    }

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


}