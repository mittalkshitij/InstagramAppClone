package com.example.parseproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,View.OnKeyListener {

    EditText username;
    EditText password;
    ImageView imageView;
    ImageView imageView2;

    public void showUserList()
    {
        Intent intent=new Intent(getApplicationContext(),UserListActivity.class);
        String usernameStr=username.getText().toString();
              intent.putExtra("Username",usernameStr);
        startActivity(intent);
    }


    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.imageView || view.getId()==R.id.layout || view.getId()==R.id.imageView2)
        {
            InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if(i==KeyEvent.KEYCODE_ENTER && keyEvent.getAction()==KeyEvent.ACTION_DOWN)
        {
            loginClicked(view);
        }
        return false;
    }

    public void nextSignup(View view)
    {
        Intent intent=new Intent(getApplicationContext(),SignupActivity.class);
        startActivity(intent);
    }

    public void loginClicked(View view)
    {


        if(username.getText().toString().equals("") || password.getText().toString().equals(""))
        {
            Toast.makeText(this, "Username and Password are required", Toast.LENGTH_SHORT).show();
        }

        ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(user!=null)
                {
                    Log.i("Info","Login Successful");
                    showUserList();
                }else
                {
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Instagram");

//        if(ParseUser.getCurrentUser()!=null)
//        {
//            showUserList();
//        }

        username=findViewById(R.id.usernameEditText);
        password=findViewById(R.id.passwordEditText);
        imageView=findViewById(R.id.imageView);
        imageView2=findViewById(R.id.imageView2);
        ConstraintLayout layout=findViewById(R.id.layout);

        imageView.setOnClickListener((View.OnClickListener) this);
        imageView2.setOnClickListener(this);
        layout.setOnClickListener(this);

        password.setOnKeyListener(this);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }



}
