package cloud.mushfiq.buet.dreamsychology;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    boolean only_phone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        Log.d("sign in ","startting...");

        if ( extras!=null && extras.getBoolean("phone")) {
            only_phone=true;
        } else {
            only_phone=false;
        }
        MobileAds.initialize(this, "ca-app-pub-2357471854208557~1280647804");

        //if(utils.isNetworkAvailable(this)){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user==null) {
            Log.d("sign in ","user null");

            //Intent i = new Intent(MainActivity.this,LoginActivity.class);
            //startActivity(i);
            //finish();

            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                    new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
            );

            if(only_phone){
                providers = Arrays.asList(
                        new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()
                );
            }


//Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setLogo(R.drawable.logo_256)
                            .build(),
                    123);
            FirebaseFirestore.getInstance()
                    .collection("Clients")
                    .document("ge")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null && document.exists()) {
                                    //int role=(int)(long) document.getData().get("role");
                                    //String name=document.get("name").toString();
                                    Toast.makeText(MainActivity.this,"doc ok",Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(MainActivity.this,"No documnet found",Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(MainActivity.this,"Check network connection",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{
            //Task usertask = FirebaseAuth.getInstance().getCurrentUser().reload();
            //usertask.addOnSuccessListener(new OnSuccessListener() {
            //@Override
            //public void onSuccess(Object o) {
            //final FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
            boolean ab = user.isEmailVerified();
            String email = user.getEmail();
            boolean fb=false;

            for (UserInfo us: user.getProviderData()) {
                if (us.getProviderId().equals("facebook.com")) {
                    fb=true;
                    break;
                }
            }

            if(fb ||ab || email==null || email.length()==0 ){
                if(email==null || email.length()==0){
                    email=user.getPhoneNumber();
                }
                if(email==null || email.length()==0){
                    email=user.getUid();
                }
                Log.d("sign in ",email+ " logged in");

                final String email1=email;
                //CollectionReference clients = FirebaseFirestore.getInstance().collection("Clients");
                FirebaseFirestore.getInstance()
                        .collection("Clients")
                        .document(email1)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null && document.exists()) {
                                        int role=(int)(long) document.getData().get("role");
                                        String name=document.get("name").toString();
                                        Log.d("sign in ", " logged in as role " + Integer.toString(role));
                                        Intent in;
                                        if(role==3){
                                            in= new Intent(MainActivity.this,starting.class);
                                            in.putExtra("email",email1);
                                            in.putExtra("name",name);
                                            in.putExtra("role",role);
                                            startActivity(in);
                                            finish();
                                        }
                                        else if(role==2){
                                            in= new Intent(MainActivity.this,starting.class);
                                            in.putExtra("email",email1);
                                            in.putExtra("role",role);
                                            startActivity(in);
                                            finish();
                                        }
                                        else if(role==1 || role==4){
                                            in= new Intent(MainActivity.this,inbox.class);
                                            in.putExtra("email",email1);
                                            in.putExtra("role",role);
                                            startActivity(in);
                                            finish();
                                        }
                                        else{

                                        }

                                    } else {
                                        Log.d("sign in ", " no document found " );
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        final Query Qvol=db.collection("Volunteers").whereEqualTo("e-mail", email1);
                                        Qvol.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot documentSnapshots) {

                                                    Intent in= new Intent(MainActivity.this,NameActivity.class);
                                                    if(documentSnapshots.size()>0){
                                                        in.putExtra("role",2);
                                                    }
                                                    else{
                                                        in.putExtra("role",3);
                                                    }
                                                    startActivity(in);
                                                    finish();

                                            }
                                        });
                                    }
                                }
                                else{
                                    Log.d("sign in ", " logged in  problem occured");
                                }
                            }
                        });
            }
            else{
                Intent in= new Intent(MainActivity.this,VerifyActivity.class);
                startActivity(in);
                finish();

            }
        }


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123) {

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                Log.d("Refreshed token", "Refreshed token: " + refreshedToken);
                final CollectionReference tok = FirebaseFirestore.getInstance().collection("Tokens");
                Map<String, Object> dt1 = new HashMap<>();
                dt1.put("token", refreshedToken);


                String email;

                boolean fb1=false;

                for (UserInfo us: user.getProviderData()) {
                    if (us.getProviderId().equals("facebook.com")) {
                        fb1=true;
                        break;
                    }
                }
                final boolean fb=fb1;
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                final CollectionReference clients = db.collection("Clients");
                final CollectionReference indexes = db.collection("Index");
                final CollectionReference volunteers = db.collection("Volunteers");
                email=user.getEmail();
                if(email==null || email.length()==0){
                    email=user.getPhoneNumber();
                }
                if(email==null || email.length()==0){
                    email=user.getUid();
                }
                tok.document(email).set(dt1, SetOptions.merge());
                final String email1=email;
                final Query Qvol=db.collection("Volunteers").whereEqualTo("e-mail", email);
                Query Qcli=db.collection("Clients").whereEqualTo("e-mail", email);
                Qcli.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if(documentSnapshots.size()>0){
                            //data written , no action needed
                            int role=(int)(long)documentSnapshots.getDocuments().get(0).getData().get("role");

                            if(user.getDisplayName()==null ||user.getDisplayName().length()==0){
                                String name=(String) documentSnapshots.getDocuments().get(0).getData().get("name");
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();

                                user.updateProfile(profileUpdates);
                            }
                            if(!fb && !user.isEmailVerified() && user.getEmail()!=null && user.getEmail().length()>0){
                                Intent in= new Intent(MainActivity.this,VerifyActivity.class);
                                in.putExtra("role",role);
                                startActivity(in);
                                finish();
                            }
                            else{
                                Intent in;
                                if(role==3){
                                    in= new Intent(MainActivity.this,starting.class);
                                    in.putExtra("email",email1);
                                    in.putExtra("name",user.getDisplayName());
                                    in.putExtra("role",role);
                                    startActivity(in);
                                    finish();
                                }
                                else if(role==2){
                                    in= new Intent(MainActivity.this,starting.class);
                                    in.putExtra("email",email1);
                                    in.putExtra("role",role);
                                    startActivity(in);
                                    finish();
                                }
                                else if(role==1||role==4){
                                    in= new Intent(MainActivity.this,inbox.class);
                                    in.putExtra("email",email1);
                                    in.putExtra("role",role);
                                    startActivity(in);
                                    finish();
                                }
                                else{

                                }

                            }
                        }
                        else{
                            Qvol.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot documentSnapshots) {

                                        if (user.getEmail() == null || user.getEmail().length()==0) {
                                            Intent in= new Intent(MainActivity.this,NameActivity.class);
                                            if(documentSnapshots.size()>0){
                                                in.putExtra("role",2);
                                            }
                                            else{
                                                in.putExtra("role",3);
                                            }
                                            startActivity(in);
                                            finish();
                                        }
                                        else{
                                            Map<String, Object> data1 = new HashMap<>();
                                            Map<String, Object> data2 = new HashMap<>();
                                            Map<String, Object> data3 = new HashMap<>();
                                            if(documentSnapshots.size()>0){
                                                data1.put("role",2);
                                                data3.put("name",user.getDisplayName());
                                                volunteers.document(user.getEmail()).set(data3,SetOptions.merge());
                                            }
                                            else{
                                                data1.put("role",3);
                                            }
                                            String[] splited = user.getDisplayName().split("\\s+");
                                            int gg=splited.length;
                                            Character charA=splited[0].charAt(0);
                                            StringBuilder sb = new StringBuilder();
                                            sb.append(charA);
                                            if(gg>1){
                                                Character charB=splited[1].charAt(0);
                                                sb.append(charB);
                                            }
                                            String ff=sb.toString();
                                            data1.put("e-mail",user.getEmail());
                                            data1.put("name",user.getDisplayName());
                                            data1.put("isEmail",true);
                                            data2.put("pickedUp",false);
                                            //data2.put("pickedBy","");
                                            data2.put("name", user.getDisplayName());
                                            //data2.put("lastWritten",null);
                                            data2.put("nameKey",ff);
                                            data2.put("comment",false);

                                            clients.document(user.getEmail()).set(data1,SetOptions.merge());
                                            indexes.document(user.getEmail()).set(data2,SetOptions.merge());

                                            if(fb){
                                                Intent in;
                                                if(documentSnapshots.size()>0){
                                                    in= new Intent(MainActivity.this,inbox_vol.class);
                                                    in.putExtra("email",email1);
                                                    startActivity(in);
                                                    finish();
                                                }

                                                else{
                                                    in= new Intent(MainActivity.this,chat_user.class);
                                                    in.putExtra("email",email1);
                                                    in.putExtra("name",user.getDisplayName());
                                                    in.putExtra("role",3);
                                                    startActivity(in);
                                                    finish();
                                                }
                                            }
                                            else{
                                                user.sendEmailVerification()
                                                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()) {

                                                                } else {
                                                                    //Log.e("verify", "sendEmailVerification", task.getException());
                                                                }
                                                                // [END_EXCLUDE]
                                                            }
                                                        });
                                                // [END send_email_verification]

                                                Intent in= new Intent(MainActivity.this,VerifyActivity.class);
                                                if(documentSnapshots.size()>0){
                                                    in.putExtra("role",2);
                                                }
                                                else{
                                                    in.putExtra("role",3);
                                                }
                                                startActivity(in);
                                                finish();
                                            }
                                        }



                                }
                            });
                        }

                    }
                });

            } else {

            }
        }
    }
}
