package com.example.mcassignment;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Activity_Login extends AppCompatActivity {

    private AccountDBHelper db;
    private TextView txtUname;
    private TextView txtPassword;
    private Button btnRegister;
    private Button btnLogin;
    private static final int REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUname = (TextView) findViewById(R.id.uname);
        txtPassword = (TextView) findViewById(R.id.pword);
        btnRegister = (Button) findViewById(R.id.register);
        btnLogin = (Button) findViewById(R.id.login);

        db = new AccountDBHelper(this);

        btnRegister.setOnClickListener(view -> openSignupScreen());
        btnLogin.setOnClickListener(view -> loginButtonClick());

        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loginButtonClick() {

        String username = txtUname.getText().toString();
        String password = txtPassword.getText().toString();

        if (txtUname.getText().toString().equals("")) {
            Toast.makeText(Activity_Login.this, "Please enter username", Toast.LENGTH_SHORT).show();
            return;
        }
        if (txtPassword.getText().toString().equals("")) {
            Toast.makeText(Activity_Login.this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = db.getUser(username);
        if (user == null || !user.getPassword().equals(password)) {
            Toast.makeText(Activity_Login.this, "Incorrect username/password", Toast.LENGTH_SHORT).show();
            return;
        }

        User.setInstance(user);
        Intent intent = new Intent(Activity_Login.this, MainMenuActivity.class);
        startActivity(intent);

    }

    public void openSignupScreen() {
        Intent intent = new Intent(Activity_Login.this, Activity_Register.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "permission denied.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}