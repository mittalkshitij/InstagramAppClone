package com.example.parseproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity implements View.OnKeyListener,View.OnClickListener {

     EditText createUsername;
     EditText createPassword;
     ImageView imageView3,imageView4;

     public void showUserList()
    {
        Intent intent=new Intent(getApplicationContext(),UserListActivity.class);
        startActivity(intent);
    }

     public void showProfile()
    {
        Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
        startActivity(intent);

    }

      @Override
    public void onClick(View view) {

        if(view.getId()==R.id.imageView3 || view.getId()==R.id.layout2 || view.getId()==R.id.imageView4)
        {
            InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }

    }


     @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

         if(i==KeyEvent.KEYCODE_ENTER && keyEvent.getAction()==KeyEvent.ACTION_DOWN)
        {
            signupClicked(view);
        }
        return false;
    }


    public void signupClicked(View view)
    {


        if(createUsername.getText().toString().equals("") || createPassword.getText().toString().equals(""))
        {
            Toast.makeText(this, "Username and Password are required", Toast.LENGTH_SHORT).show();
        }
        else
        {
            ParseUser user=new ParseUser();
            user.setUsername(createUsername.getText().toString());
            user.setPassword(createPassword.getText().toString());

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e==null)
                    {
                        Log.i("INFO","Sign up Successful");
                        showUserList();
                    }
                    else
                    {
                        Toast.makeText(SignupActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void nextLogin(View view)
    {
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        setTitle("Instagram");

        createUsername=findViewById(R.id.createUsernameEditText);
        createPassword=findViewById(R.id.createPassEditText);
        imageView3=findViewById(R.id.imageView3);
        imageView4=findViewById(R.id.imageView4);
        ConstraintLayout layout2=findViewById(R.id.layout2);
          createPassword.setOnKeyListener(this);

          imageView3.setOnClickListener((View.OnClickListener) this);
        imageView4.setOnClickListener(this);
        layout2.setOnClickListener(this);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }


}