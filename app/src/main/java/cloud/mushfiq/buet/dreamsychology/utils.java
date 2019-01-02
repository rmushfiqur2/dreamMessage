package cloud.mushfiq.buet.dreamsychology;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Created by HP on 12-Mar-18.
 */

public class utils {
    public static boolean isNetworkAvailable( Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static void toast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    public static void prebareInbox (final Context context){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email=user.getEmail();
        if(email==null){
            email=user.getPhoneNumber();
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Query Qad=db.collection("Admins").whereEqualTo("e-mail", email);
        final Query Qvol=db.collection("Volunteers").whereEqualTo("e-mail", email);

        Qad.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if(documentSnapshots.size()>0){
                    toast(context,"You are admin");
                }
                else{
                    Qvol.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            if(documentSnapshots.size()>0){
                                toast(context,"You are volunteer");
                            }
                            else{
                                toast(context,"You are client");
                            }

                        }
                    });
                }

            }
        });
    }
}
