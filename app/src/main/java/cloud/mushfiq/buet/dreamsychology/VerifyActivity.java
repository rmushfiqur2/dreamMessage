package cloud.mushfiq.buet.dreamsychology;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class VerifyActivity extends AppCompatActivity implements View.OnClickListener{
    int role;
    boolean given;
    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        given=false;
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            given=true;
            role=extras.getInt("role");
        }
        setContentView(R.layout.activity_verify);

        TextView name=(TextView)findViewById(R.id.textView);
        TextView text=(TextView)findViewById(R.id.textView2);
        TextView phone=(TextView)findViewById(R.id.textView4);
        TextView logout=(TextView)findViewById(R.id.textView3);
        Button verify=(Button)findViewById(R.id.button2);
        Button send=(Button)findViewById(R.id.button3);
        bar=(ProgressBar)findViewById(R.id.progress) ;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email=user.getEmail();
        name.setText(email);

        verify.setOnClickListener(this);
        send.setOnClickListener(this);
        phone.setOnClickListener(this);
        logout.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button2) {
            bar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Task usertask = FirebaseAuth.getInstance().getCurrentUser().reload();
            usertask.addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    final FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                    boolean ab = user1.isEmailVerified();

                    if(ab){
                        if(given) {
                            bar.setVisibility(View.GONE);
                            Intent in;
                            if(role==3){
                                in= new Intent(VerifyActivity.this,starting.class);
                                in.putExtra("email",user1.getEmail());
                                in.putExtra("name",user1.getDisplayName());
                                in.putExtra("role",role);
                                startActivity(in);
                                finish();
                            }
                            else if(role==2){
                                in= new Intent(VerifyActivity.this,starting.class);
                                in.putExtra("email",user1.getEmail());
                                in.putExtra("role",role);
                                startActivity(in);
                                finish();
                            }
                            else if(role==1||role==4){
                                in= new Intent(VerifyActivity.this,inbox.class);
                                in.putExtra("email",user1.getEmail());
                                in.putExtra("role",role);
                                startActivity(in);
                                finish();
                            }
                            else{

                            }
                        }
                        else{
                            CollectionReference clients = FirebaseFirestore.getInstance().collection("Clients");
                            clients.document(user1.getEmail()).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                bar.setVisibility(View.GONE);
                                                DocumentSnapshot document = task.getResult();
                                                int role=(int)(long) document.getData().get("role");
                                                Intent in;
                                                if(role==3){
                                                    in= new Intent(VerifyActivity.this,starting.class);
                                                    in.putExtra("email",user1.getEmail());
                                                    in.putExtra("name",user1.getDisplayName());
                                                    in.putExtra("role",role);
                                                    startActivity(in);
                                                    finish();
                                                }
                                                else if(role==2){
                                                    in= new Intent(VerifyActivity.this,starting.class);
                                                    in.putExtra("email",user1.getEmail());
                                                    in.putExtra("role",role);
                                                    startActivity(in);
                                                    finish();
                                                }
                                                else if(role==1||role==4){
                                                    in= new Intent(VerifyActivity.this,inbox.class);
                                                    in.putExtra("email",user1.getEmail());
                                                    in.putExtra("role",role);
                                                    startActivity(in);
                                                    finish();
                                                }
                                                else{

                                                }
                                            }
                                            else{
                                                bar.setVisibility(View.GONE);
                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                utils.toast(VerifyActivity.this,"Connect to internet and re-try.");
                                            }
                                        }
                                    });
                        }
                    }
                    else{
                        bar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        utils.toast(VerifyActivity.this,"Sorry! Verify Correctly");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    bar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    utils.toast(VerifyActivity.this,"Connect to internet and re-try.");
                }
            });
        }
        if (i == R.id.button3) {
            bar.setVisibility(View.GONE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
            user1.sendEmailVerification()
                    .addOnCompleteListener(VerifyActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                bar.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                utils.toast(VerifyActivity.this,"New Email has been sent.");
                            } else {
                                bar.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                utils.toast(VerifyActivity.this,"Connect to internet and re-try.");
                            }
                            // [END_EXCLUDE]
                        }
                    });
        }
        if (i == R.id.textView4) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String email = user.getEmail();
            FirebaseFirestore.getInstance().collection("Tokens").document(email).delete();
            FirebaseAuth.getInstance().signOut();
            Intent in= new Intent(this,MainActivity.class);
            in.putExtra("phone",true);
            startActivity(in);
            finish();
        }
        if (i == R.id.textView3) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(VerifyActivity.this, MainActivity.class));
            finish();
        }

    }
}
