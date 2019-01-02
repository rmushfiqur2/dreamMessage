package cloud.mushfiq.buet.dreamsychology;


import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginActivity extends AppCompatActivity {

    EditText mobile, password;
    Button login;
    TextView forget,signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mobile=findViewById(R.id.input_email);
        password=findViewById(R.id.input_password);
        login=findViewById(R.id.btn_login);
        forget=findViewById(R.id.forget);
        signup=findViewById(R.id.link_signup1);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String phone = mobile.getText().toString().trim();
                final String code = password.getText().toString().trim();
                if(!phone.isEmpty() && !code.isEmpty()){
                    /*SharedPreferences get=getSharedPreferences("all",MODE_PRIVATE);
                    SharedPreferences.Editor editor = get.edit();
                    editor.putString("mobile","+8801740196531");
                    editor.putString("name","amar nam");
                    editor.putString("email","rmushfiqur2@gmail.com");
                    editor.putString("nid","78745");
                    editor.apply();
                    Intent i = new Intent(LoginActivity.this,settingsActivity.class);
                    startActivity(i);
                    finish();*/
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseFirestore.getInstance().collection("Users").document(phone)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    if(document.getString("password").equals(code)){
                                        SharedPreferences get=getSharedPreferences("all",MODE_PRIVATE);
                                        SharedPreferences.Editor editor = get.edit();
                                        editor.putString("mobile",document.getString("mobile"));
                                        editor.putString("name",document.getString("name"));
                                        editor.putString("email",document.getString("email"));
                                        editor.putString("nid",document.getString("nid"));
                                        editor.apply();
                                    }
                                    else{
                                        Toast.makeText(LoginActivity.this,"Your phone no and password doesn't match.",Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this,"You are not registered. Sign up please",Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                Toast.makeText(LoginActivity.this,"Check network connection",Toast.LENGTH_SHORT).show();


                            }
                        }
                    });
                }
                else{
                    Toast.makeText(LoginActivity.this,"Input valid data",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
