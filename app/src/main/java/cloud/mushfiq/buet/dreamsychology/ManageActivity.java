package cloud.mushfiq.buet.dreamsychology;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageActivity extends AppCompatActivity implements dialogInterface{
    ArrayList<Vol> vols;
    RecyclerView list;
    volAdapter adapter;
    LinearLayoutManager LI;
    ProgressBar running;

    String delEmail;
    int delPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        final GraphView graph = (GraphView) findViewById(R.id.graph);
        final TextView overall=(TextView) findViewById(R.id.overall);
        final EditText editemail=(EditText) findViewById(R.id.editemail);
        final Button add=(Button) findViewById(R.id.add);
        running=(ProgressBar)findViewById(R.id.progres);

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("Trigger").document("Statistics");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        overall.setText("Overall service reicipents: "+document.getString("total"));
                        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                                new DataPoint(0,(int) (long) document.getData().get("week14")),
                                new DataPoint(1, (int) (long) document.getData().get("week13")),
                                new DataPoint(2, (int) (long) document.getData().get("week12")),
                                new DataPoint(3, (int) (long) document.getData().get("week11")),
                                new DataPoint(4, (int) (long) document.getData().get("week10")),
                                new DataPoint(5, (int) (long) document.getData().get("week9")),
                                new DataPoint(6, (int) (long) document.getData().get("week8")),
                                new DataPoint(7, (int) (long) document.getData().get("week7")),
                                new DataPoint(8, (int) (long) document.getData().get("week6")),
                                new DataPoint(9, (int) (long) document.getData().get("week5")),
                                new DataPoint(10, (int) (long) document.getData().get("week4")),
                                new DataPoint(11, (int) (long) document.getData().get("week3")),
                                new DataPoint(12, (int) (long) document.getData().get("week2")),
                                new DataPoint(13, (int) (long) document.getData().get("week1"))
                        });
                        graph.addSeries(series);

                        //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        //Log.d(TAG, "No such document");
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"14th week", "", "","","", "","","", "","","", "","","Recent"});
        //staticLabelsFormatter.setVerticalLabels(new String[] {"low", "middle", "high"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        list =  findViewById(R.id.list);

        LI=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(LI);
        list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        vols=new ArrayList<>();
        adapter=new volAdapter(vols);
        list.setAdapter(adapter);
        list.hasFixedSize();
        FirebaseFirestore.getInstance().collection("Volunteers").orderBy("totalReplyWeek", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Vol m= new Vol();
                                m.email=document.getString("email");
                                m.week=Long.toString((long) document.getData().get("totalReplyWeek"));
                                m.total=Long.toString((long) document.getData().get("totalReply"));
                                if(document.getData().get("name")!=null){
                                    m.name=document.getString("name");
                                }
                                else{
                                    m.name="(Not signed in)";
                                }
                                vols.add(m);
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            adapter.notifyDataSetChanged();
                            running.setVisibility(View.GONE);
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ab=editemail.getText().toString().trim();
                if(ab.length()>0 && utils.isNetworkAvailable(ManageActivity.this)){
                    running.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    if(ab.charAt(0)=='0' && ab.charAt(1)=='1'){
                        ab="+88"+ab;
                    }
                    editemail.setText("");
                    final String ab1=ab;
                    final DocumentReference docRef = FirebaseFirestore.getInstance().collection("Volunteers").document(ab1);
                    final DocumentReference docRef1 = FirebaseFirestore.getInstance().collection("Clients").document(ab1);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null && document.exists()) {
                                    running.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    utils.toast(ManageActivity.this, "Already Exists!");
                                } else {
                                    Map<String, Object> newvol = new HashMap<>();
                                    newvol.put("email", ab1);
                                    docRef.set(newvol, SetOptions.merge());
                                    running.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    utils.toast(ManageActivity.this, "Volunteer added successfully!");

                                    docRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                String nm;
                                                if (document != null && document.exists()) {
                                                    nm=document.getString("name");
                                                } else {
                                                    nm="(Not signed in)";
                                                }
                                                Vol m= new Vol();
                                                m.email=ab1;
                                                m.name=nm;
                                                m.total="0";
                                                m.week="0";
                                                vols.add(m);
                                                adapter.notifyDataSetChanged();

                                            } else {
                                                utils.toast(ManageActivity.this,"New volunteer will show in list after re-start.");
                                            }
                                        }
                                    });


                                }
                            } else {
                                running.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                utils.toast(ManageActivity.this, "Connection Error!");
                            }
                        }
                    });

                }
                else{
                    utils.toast(ManageActivity.this,"Enter valid email/phone with internet connection.");
                }

            }
        });
        add.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String ab=editemail.getText().toString().trim();
                if(ab.length()>0 && utils.isNetworkAvailable(ManageActivity.this)) {
                    if (ab.charAt(0) == '0' && ab.charAt(1) == '1') {
                        ab = "+88" + ab;
                    }
                    final String ab1 = ab;
                    AlertDialog.Builder builder = new AlertDialog.Builder(ManageActivity.this);
                    builder.setMessage("Are you sure to add a moderator ?")
                            .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    running.setVisibility(View.VISIBLE);
                                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    editemail.setText("");
                                    final DocumentReference docRef = FirebaseFirestore.getInstance().collection("Clients").document(ab1);
                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document != null && document.exists()) {
                                                    int role=(int)(long) document.getData().get("role");
                                                    running.setVisibility(View.GONE);
                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                    if(role==1 || role==4){
                                                        utils.toast(ManageActivity.this, "This user is already an admin/moderator.");
                                                    }
                                                    if(role==2){
                                                        utils.toast(ManageActivity.this, "First remove this user from volunteer list and then add as admin.");
                                                    }
                                                    if(role==3){
                                                        Map<String, Object> data1 = new HashMap<>();
                                                        data1.put("role",4);
                                                        docRef.set(data1,SetOptions.merge());
                                                        utils.toast(ManageActivity.this, "Moderator added successfully.");
                                                    }
                                                } else {
                                                    running.setVisibility(View.GONE);
                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                    utils.toast(ManageActivity.this, "To make this user moderator, s/he must create account first.");
                                                }
                                            } else {
                                                running.setVisibility(View.GONE);
                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                utils.toast(ManageActivity.this, "Connection Error!");
                                            }
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("No, as admin", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    running.setVisibility(View.VISIBLE);
                                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    editemail.setText("");
                                    final DocumentReference docRef = FirebaseFirestore.getInstance().collection("Clients").document(ab1);
                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document != null && document.exists()) {
                                                    int role=(int)(long) document.getData().get("role");
                                                    running.setVisibility(View.GONE);
                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                                    if(role==1 || role==4){
                                                        utils.toast(ManageActivity.this, "This user is already an admin/moderator.");
                                                    }
                                                    if(role==2){
                                                        utils.toast(ManageActivity.this, "First remove this user from volunteer list and then add as admin.");
                                                    }
                                                    if(role==3){
                                                        Map<String, Object> data1 = new HashMap<>();
                                                        data1.put("role",1);
                                                        docRef.set(data1,SetOptions.merge());
                                                        utils.toast(ManageActivity.this, "Admin added successfully.");
                                                    }
                                                } else {
                                                    running.setVisibility(View.GONE);
                                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                    utils.toast(ManageActivity.this, "To make this user admin, s/he must create account first.");
                                                }
                                            } else {
                                                running.setVisibility(View.GONE);
                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                utils.toast(ManageActivity.this, "Connection Error!");
                                            }
                                        }
                                    });
                                }
                            });
                    builder.show();
                }
                else{
                    utils.toast(ManageActivity.this,"Enter valid email/phone with internet connection.");
                }
                return true;
            }
        });
        overall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(ManageActivity.this,userlist.class);
                startActivity(in);
            }
        });

    }

    private class volAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<Vol> mScientists;
        public volAdapter(ArrayList<Vol> Scientists){
            mScientists = Scientists;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(ManageActivity.this);
            View view = layoutInflater.inflate(R.layout.vol_unit,parent,false);
            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final Vol s = mScientists.get(position);
            holder.name.setText(s.name);
            holder.email.setText(s.email);
            holder.total.setText(s.total);
            holder.week.setText(s.week);
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delEmail=s.email;
                    delPos=position;
                    confirmDialogFragment alertDialog = new confirmDialogFragment();// myFragment.newInstance("Some title");
                    alertDialog.setCancelable(true);
                    alertDialog.show(getSupportFragmentManager(), "abba");
                }
            });
        }

        @Override
        public int getItemCount() {
            return mScientists.size();

        }
    }
    private class Vol {
        public String name;
        public String email;
        public String total;
        public String week;
    }
    private class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView email;
        public TextView total;
        public TextView week;
        public ImageButton button;

        public ViewHolder(View v) {
            super(v);
            name=v.findViewById(R.id.name);
            email=v.findViewById(R.id.email);
            total=v.findViewById(R.id.total);
            week=v.findViewById(R.id.week);
            button=v.findViewById(R.id.delete);
        }
    }
    public static class confirmDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {


            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialog_confirm)
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialogInterface dI=(dialogInterface) getActivity();
                            dI.onDialogPositiveClick();
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
    @Override
    public void onDialogPositiveClick() {
        if(utils.isNetworkAvailable(ManageActivity.this)){
            running.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            Map<String, Object> city = new HashMap<>();
            city.put("email", delEmail);

            FirebaseFirestore.getInstance().collection("Trigger").document("toBeDeleted")
                    .set(city)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            vols.remove(delPos);
                            adapter.notifyDataSetChanged();
                            running.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            utils.toast(ManageActivity.this, "Deleted Successfully !");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            running.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            utils.toast(ManageActivity.this, "Connection Error!");
                        }
                    });


        }
        else{
            utils.toast(this,"Internet connection not found");
        }
        //utils.toast(this,delEmail);
    }

}