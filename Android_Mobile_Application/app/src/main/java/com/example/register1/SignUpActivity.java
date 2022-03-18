package com.example.register1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignUpActivity extends AppCompatActivity {

    private EditText etEmail, etPassword1, etPassword2;
    private Button signUpBtn;
    private TextView signInTV;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.emailsu);
        etPassword1 = findViewById(R.id.password1);
        etPassword2 = findViewById(R.id.password2);

        signUpBtn = findViewById(R.id.register);

        progressDialog = new ProgressDialog(this);

        signInTV = findViewById(R.id.signin);
        
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        
        signInTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void register() {
        String email = etEmail.getText().toString();
        String password1 = etPassword1.getText().toString();
        String password2 = etPassword2.getText().toString();
        if(TextUtils.isEmpty(email)){
            etEmail.setError("Enter your email");
            return;
        }
        else if(!isValidEmail(email)){
            etEmail.setError("Invalid email");
            return;
        }
        else if(TextUtils.isEmpty(password1)){
            etPassword1.setError("Enter your password");
            return;
        }
        else if(password1.length() < 6){
            etPassword1.setError("Length should be at least 6 characters!");
            return;
        }
        else if(TextUtils.isEmpty(password2)){
            etPassword2.setError("Confirm your password");
            return;
        }
        else if(!password2.equals(password1)){
            etPassword2.setError("Password didn't match");
            return;
        }


        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth.createUserWithEmailAndPassword(email, password1).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignUpActivity.this, "Successfully registered", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SignUpActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(SignUpActivity.this, "Sign up fail", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}
