package com.example.mcassignment;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Activity_Register extends AppCompatActivity {

    private AccountDBHelper db;
    private TextView txtUsername;
    private TextView txtPassword;
    private Button btnRegister;
    private Button btnLogin;
    private TextView txtFname;
    private TextView txtLname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtUsername = (TextView) findViewById(R.id.uname);
        txtPassword = (TextView) findViewById(R.id.pword);
        txtFname = (TextView) findViewById(R.id.fname);
        txtLname = (TextView) findViewById(R.id.lname);
        btnRegister = (Button) findViewById(R.id.register);
        btnLogin = (Button) findViewById(R.id.login);
        db = new AccountDBHelper(this);

        btnRegister.setOnClickListener(view -> signupButtonClick());
        btnLogin.setOnClickListener(view -> openLoginScreen());
    }

    public void signupButtonClick() {

        String username = txtUsername.getText().toString();
        String firstname = txtFname.getText().toString();
        String lastname = txtLname.getText().toString();
        String password = txtPassword.getText().toString();

        if (username.equals("")) {
            Toast.makeText(Activity_Register.this, "Please enter username", Toast.LENGTH_SHORT).show();
            return;
        }
        if (firstname.equals("")) {
            Toast.makeText(Activity_Register.this, "Please enter first name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (lastname.equals("")) {
            Toast.makeText(Activity_Register.this, "Please enter last name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.equals("")) {
            Toast.makeText(Activity_Register.this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        if(db.getUser(username) != null) {
            Toast.makeText(Activity_Register.this, "User already exists! Please Login", Toast.LENGTH_SHORT).show();
            return;
        }

        if(db.createUser(username, firstname, lastname, password)) {
            Toast.makeText(Activity_Register.this, "User created successfully!", Toast.LENGTH_SHORT).show();
            User user = new User(username, firstname, lastname, password);
            User.setInstance(user);
            Intent intent = new Intent(Activity_Register.this, MainMenuActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(Activity_Register.this, "Unable to create user", Toast.LENGTH_SHORT).show();
        }

    }

    public void openLoginScreen() {
        Intent intent = new Intent(Activity_Register.this, Activity_Login.class);
        startActivity(intent);
    }
}