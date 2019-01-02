package cloud.mushfiq.buet.dreamsychology;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class NameActivity extends AppCompatActivity {

    EditText name;
    Integer role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            role=extras.getInt("role");
        }
        setContentView(R.layout.activity_name);

        TextView logout=(TextView)findViewById(R.id.textView3);
        name=(EditText)findViewById(R.id.button2);
        Button save=(Button)findViewById(R.id.button3);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str=name.getText().toString().trim();
                if(str.length()>0){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(str)
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //utils.toast(NameActivity.this,"set Correctly");
                                        //Log.d(TAG, "User profile updated.");
                                    }
                                }
                            });
                    String email = user.getPhoneNumber();
                    if(email==null || email.length()==0){
                        email=user.getUid();
                    }
                    String[] splited = str.split("\\s+");
                    int gg=splited.length;
                    Character charA=splited[0].charAt(0);
                    StringBuilder sb = new StringBuilder();
                    sb.append(charA);
                    if(gg>1){
                        Character charB=splited[1].charAt(0);
                        sb.append(charB);
                    }
                    String ff=sb.toString();

                        //FirebaseFirestore db = FirebaseFirestore.getInstance();
                        final CollectionReference clients = FirebaseFirestore.getInstance().collection("Clients");
                        final CollectionReference indexes = FirebaseFirestore.getInstance().collection("Index");
                        final CollectionReference volunteers = FirebaseFirestore.getInstance().collection("Volunteers");

                        Map<String, Object> data1 = new HashMap<>();
                        Map<String, Object> data2 = new HashMap<>();
                        Map<String, Object> data3 = new HashMap<>();
                        data1.put("name", str);
                        data1.put("isEmail", false);
                        data1.put("e-mail", email);
                        data1.put("role", role);
                        data2.put("name", str);
                        data2.put("pickedUp", false);
                        //data2.put("pickedBy", "");
                        //data2.put("lastWritten",null);
                        data2.put("nameKey",ff);
                        data2.put("comment",false);
                        indexes.document(email).set(data2, SetOptions.merge());
                        clients.document(email).set(data1, SetOptions.merge());
                        if(role==2){
                            data3.put("name", str);
                            volunteers.document(email).set(data3, SetOptions.merge());
                        }


                    Intent in;
                    if(role==3){
                        in= new Intent(NameActivity.this,starting.class);
                        in.putExtra("email",email);
                        in.putExtra("name",str);
                        in.putExtra("role",role);
                        startActivity(in);
                        finish();
                    }
                    else if(role==2){
                        in= new Intent(NameActivity.this,starting.class);
                        in.putExtra("email",email);
                        in.putExtra("role",role);
                        startActivity(in);
                        finish();
                    }
                    else if(role==1||role==4){
                        in= new Intent(NameActivity.this,inbox.class);
                        in.putExtra("email",email);
                        in.putExtra("role",role);
                        startActivity(in);
                        finish();
                    }
                    else{

                    }
                }
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String email = user.getPhoneNumber();
                FirebaseFirestore.getInstance().collection("Tokens").document(email).delete();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(NameActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
