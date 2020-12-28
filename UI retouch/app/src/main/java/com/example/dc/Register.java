package com.example.dc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    EditText mfullName,mEmail,mPassword,mPhone;
    Button mregistration,mloginpage;
    FirebaseAuth fAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mfullName= findViewById(R.id.rnameinput);
        mEmail= findViewById(R.id.rmailinput);
        mPassword= findViewById(R.id.passinput);
        mPhone= findViewById(R.id.passconfirm);
        mregistration=findViewById(R.id.Regisbutton);
        mloginpage= findViewById(R.id.loginbutton);

        fAuth= FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        mregistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email=mEmail.getText().toString().trim();
                String password=mPassword.getText().toString().trim();
                String pnumber=mPhone.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {
                    mEmail.setError("Email is required.");
                    return;
                }

                if(TextUtils.isEmpty(password))
                {
                    mPassword.setError("Password is required.");
                    return;
                }

                if(password.length()<8)
                {
                    mPassword.setError("Password must have at least 8 characters.");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(Register.this,"User Successfully Created.",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));

                        }
                        else
                        {


                            Toast.makeText(Register.this,"Error!"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });

    }
}