package cloud.mushfiq.buet.dreamsychology;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryListenOptions;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class inbox extends AppCompatActivity implements listToChat{
    ArrayList<msg_preview> messages;
    ArrayList<Date> timeline;
    ArrayList<String> ID;
    network network;
    int role;

    String myEmail;
    RecyclerView list;
    Iadapter adapter;
    TextView net;
    LinearLayoutManager LI;
    boolean loaded;
    boolean First_load;
    boolean First_load1;
    Date lastDate;
    QueryListenOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            myEmail=extras.getString("email");
            role=extras.getInt("role");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        list =  findViewById(R.id.list);
        net = (TextView) findViewById(R.id.network);
        LI=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(LI);
        list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        loaded=false;

        First_load=true;
        First_load1=true;

        messages= new ArrayList<>();
        timeline= new ArrayList<>();
        ID= new ArrayList<>();
        adapter=new Iadapter(messages,this);
        list.setAdapter(adapter);
        list.hasFixedSize();
        final CollectionReference conversations = FirebaseFirestore.getInstance().collection("Index");
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (LI.findLastVisibleItemPosition() == messages.size()-1  && !loaded && lastDate!=null) {
                    loaded=true;
                    conversations.orderBy("lastWritten", Query.Direction.DESCENDING).startAfter(lastDate).limit(15).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {
                                        return;
                                    }
                                    final int size=documentSnapshots.size();
                                    if(size<1){
                                        return;
                                    }
                                    else{
                                        int AD,DL;
                                        AD=0; DL=0;

                                        for(DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                                            switch (dc.getType()) {
                                                case ADDED:
                                                    AD++;
                                                    break;
                                                case REMOVED:
                                                    DL++;
                                                    break;
                                            }
                                            if(AD>1){
                                                break;
                                            }
                                        }
                                        if(AD==1&&DL==1){
                                            //utils.toast(inbox.this,"returned");
                                            return;
                                        }
                                        AD=0;



                                        for(DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                                            switch (dc.getType()) {
                                                case ADDED:
                                                    AD++;
                                                    Date myseen;
                                                    Date heWroteAt;
                                                    msg_preview m=new msg_preview();
                                                    m.name=(String)dc.getDocument().getData().get("name");
                                                    //m.text="are text";
                                                    //m.namekey="AA";
                                                    //m.isTaken=false;
                                                    //m.our_msg=false;
                                                    //m.newmsg=false;
                                                    m.email=(String)dc.getDocument().getId();

                                                    m.text=(String)dc.getDocument().getData().get("text");
                                                    m.namekey=(String)dc.getDocument().getData().get("nameKey");
                                                    m.isTaken=(Boolean) dc.getDocument().getData().get("pickedUp");
                                                    m.our_msg=(Boolean) dc.getDocument().getData().get("ourMessage");
                                                    //m.newmsg=false;
                                                    /*myseen=(Date)dc.getDocument().getData().get(myEmail);
                                                    heWroteAt=(Date)dc.getDocument().getData().get("lastWritten");
                                                    if(!m.our_msg && myseen!=null && heWroteAt.compareTo(myseen)>0){
                                                        m.newmsg=true;
                                                    }
                                                    else{
                                                        m.newmsg=false;
                                                    }*/
                                    if(dc.getDocument().getData().get(myEmail)==null){
                                        m.newmsg=true;
                                    }
                                    else{
                                        myseen=(Date)dc.getDocument().getData().get(myEmail);
                                        if(m.our_msg || myseen==null){
                                            m.newmsg=false;
                                        }
                                        else{
                                            heWroteAt=(Date)dc.getDocument().getData().get("lastWritten");
                                            if(heWroteAt.compareTo(myseen)>0){
                                                m.newmsg=true;
                                            }
                                            else{
                                                m.newmsg=false;
                                            }
                                        }

                                    }

                                                    if(m.isTaken){
                                                        m.taker=dc.getDocument().get("pickerName").toString();
                                                    }
                                                    Date date;
                                                    if(dc.getDocument().getData().get("lastWritten")==null){
                                                        m.time="Just Now";
                                                        date=Calendar.getInstance().getTime();
                                                    }
                                                    else{
                                                        date=(Date)dc.getDocument().getData().get("lastWritten");
                                                        Calendar cl=Calendar.getInstance();
                                                        long diff = cl.getTimeInMillis() - date.getTime();
                                                        long days = diff / 86400000;

                                                        SimpleDateFormat localDateFormat = new SimpleDateFormat("hh:mm a");

                                                        if(days<1){
                                                            m.time=localDateFormat.format(date);
                                                        }
                                                        else{
                                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
                                                            m.time=dateFormat.format(date);
                                                        }
                                                    }
                                                    /*for(int s=timeline.size()-1;s>-1;s--){
                                                        if(date.compareTo(timeline.get(s))<0){
                                                            timeline.add(s+1,date);
                                                            ID.add(s+1,dc.getDocument().getId());
                                                            messages.add(s+1,m);
                                                            break;
                                                        }
                                                    }*/
                                                    timeline.add(date);
                                                    ID.add(dc.getDocument().getId());
                                                    messages.add(m);

                                                    break;
                                                case MODIFIED:
                                                    //ED ++;
                                                    String id=dc.getDocument().getId();
                                                    //utils.toast(inbox.this,id);
                                                    for(int s=ID.size()-1;s>-1;s--){
                                                        if(id.equals(ID.get(s))){
                                                            msg_preview m1=messages.get(s);
                                                            Date myseen1;
                                                            Date heWroteAt1;
                                                            m1.text=(String)dc.getDocument().getData().get("text");
                                                            m1.our_msg=(Boolean) dc.getDocument().getData().get("ourMessage");
                                                            heWroteAt1=(Date)dc.getDocument().getData().get("lastWritten");
                                                            if(!m1.our_msg && dc.getDocument().getData().get(myEmail)!=null && heWroteAt1.compareTo((Date)dc.getDocument().getData().get(myEmail))>0){
                                                                m1.newmsg=true;
                                                            }
                                                            else{
                                                                m1.newmsg=false;
                                                            }
                                                            m1.isTaken=dc.getDocument().getBoolean("pickedUp");
                                                            if(m1.isTaken){
                                                                m1.taker=dc.getDocument().getString("pickerName");
                                                            }
                                                            //utils.toast(inbox.this,Integer.toString(s));
                                                            //messages.remove(s);
                                                            messages.set(s,m1);
                                                            break;
                                                        }
                                                    }
                                                    break;
                                                case REMOVED:
                                                    //DL++;
                                                    //utils.toast(inbox.this,Integer.toString(dc.getOldIndex()));
                                                    break;
                                            }
                                        }
                                        adapter.notifyDataSetChanged();
                                        if(AD>14){
                                            lastDate=documentSnapshots.getDocuments().get(size - 1).getDate("lastWritten");
                                            loaded=false;
                                        }

                                        //if(size>4){
                                            //utils.toast(inbox.this,Integer.toString(AD)+Integer.toString(ED)+Integer.toString(DL));
                                        //loaded=false;}
                                    }
                                }
                            });

                }
            }
        });


        conversations.orderBy("lastWritten", Query.Direction.DESCENDING).limit(15).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot f, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                int AD,DL;
                AD=0;DL=0;

                for(DocumentChange dc : f.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            AD++;
                            break;
                        case REMOVED:
                            DL++;
                            break;
                    }
                    if(AD>1){
                        //break;
                    }
                }
                //utils.toast(inbox.this,Integer.toString(AD*10+DL));
                if(AD==1&&DL==1){
                    for(DocumentChange dc : f.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                String id=dc.getDocument().getId();
                                for(int s=0;s<ID.size();s++){
                                    if(id.equals(ID.get(s))){
                                        messages.remove(s);
                                        timeline.remove(s);
                                        ID.remove(s);
                                        utils.toast(inbox.this,"something");
                                        break;
                                    }
                                }
                                break;
                        }
                    }
                }
                //utils.toast(inbox.this,Integer.toString(f.size()));

                for(DocumentChange dc :f.getDocumentChanges()){
                    switch (dc.getType()) {
                        case ADDED:
                            Date myseen=new Date();
                            Date heWroteAt=new Date();
                            msg_preview m=new msg_preview();
                            m.name=(String)dc.getDocument().getData().get("name");
                            //m.text="are text";
                            //m.namekey="AA";
                            //m.isTaken=false;
                            //m.our_msg=false;
                            //m.newmsg=false;
                            m.email=(String)dc.getDocument().getId();

                                m.text=(String)dc.getDocument().getData().get("text");
                                m.namekey=(String)dc.getDocument().getData().get("nameKey");
                                m.isTaken=(Boolean) dc.getDocument().getData().get("pickedUp");
                                m.our_msg=(Boolean) dc.getDocument().getData().get("ourMessage");
                                //m.newmsg=false;
                            /*myseen=(Date)dc.getDocument().getData().get(myEmail);
                            heWroteAt=(Date)dc.getDocument().getData().get("lastWritten");
                            if(!m.our_msg && myseen!=null && heWroteAt.compareTo(myseen)>0){
                                m.newmsg=true;
                            }
                            else{
                                m.newmsg=false;
                            }*/
                                    if(dc.getDocument().getData().get(myEmail)==null){
                                        m.newmsg=true;
                                    }
                                    else{
                                        myseen=(Date)dc.getDocument().getData().get(myEmail);
                                        if(!m.our_msg && myseen!=null){
                                            heWroteAt=(Date)dc.getDocument().getData().get("lastWritten");
                                            if(heWroteAt.compareTo(myseen)>0){
                                                m.newmsg=true;
                                            }
                                            else{
                                                m.newmsg=false;
                                            }
                                        }
                                        else{
                                            m.newmsg=false;
                                        }

                                    }

                            if(m.isTaken){
                                m.taker=dc.getDocument().get("pickerName").toString();
                            }
                            Date date;
                            if(dc.getDocument().getData().get("lastWritten")==null){
                                m.time="Just Now";
                                date=Calendar.getInstance().getTime();
                            }
                            else{
                                date=(Date)dc.getDocument().getData().get("lastWritten");
                                Calendar cl=Calendar.getInstance();
                                long diff = cl.getTimeInMillis() - date.getTime();
                                long days = diff / 86400000;

                                SimpleDateFormat localDateFormat = new SimpleDateFormat("hh:mm a");

                                if(days<1){
                                    m.time=localDateFormat.format(date);
                                }
                                else{
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
                                    m.time=dateFormat.format(date);
                                }
                            }


                            if(First_load){
                                messages.add(m);
                                timeline.add(date);
                                ID.add(dc.getDocument().getId());
                            }
                            else{
                                messages.add(0,m);
                                timeline.add(0,date);
                                ID.add(0,dc.getDocument().getId());
                            }
                            break;
                        case MODIFIED:
                            //utils.toast(inbox.this,dc.getDocument().getId());
                            if(dc.getDocument().getMetadata().isFromCache()){
                                break;
                            }
                            int pos=dc.getNewIndex();
                            int pre=dc.getOldIndex();
                            Date myseen1=new Date();
                            Date heWroteAt1=new Date();

                            msg_preview m1=new msg_preview();
                            m1.email=(String)dc.getDocument().getId();
                            m1.name=(String)dc.getDocument().getData().get("name");
                            m1.namekey=(String)dc.getDocument().getData().get("nameKey");
                            m1.text=(String)dc.getDocument().getData().get("text");
                            m1.isTaken=(Boolean) dc.getDocument().getData().get("pickedUp");
                            m1.our_msg=(Boolean) dc.getDocument().getData().get("ourMessage");

                            /*myseen1=(Date)dc.getDocument().getData().get(myEmail);
                            heWroteAt1=(Date)dc.getDocument().getData().get("lastWritten");
                            if(!m1.our_msg && myseen1!=null && heWroteAt1.compareTo(myseen1)>0){
                                m1.newmsg=true;
                            }
                            else{
                                m1.newmsg=false;
                            }*/



                            if((Date)dc.getDocument().getData().get(myEmail)==null){
                                //m1.newmsg=true;
                                //utils.toast(inbox.this,"not found, so");
                            }
                            else{
                                myseen1=(Date)dc.getDocument().getData().get(myEmail);
                                if(m1.our_msg || myseen1==null){
                                    m1.newmsg=false;
                                    //utils.toast(inbox.this,"just seen, so");
                                }
                                else{
                                    heWroteAt1=(Date)dc.getDocument().getData().get("lastWritten");
                                    if(heWroteAt1.compareTo(myseen1)>0){
                                        m1.newmsg=true;
                                        //utils.toast(inbox.this,"calculated, so");
                                    }
                                    else{
                                        m1.newmsg=false;
                                        //utils.toast(inbox.this,"just seen/calc, so");
                                    }
                                }

                            }

                            if(m1.isTaken){
                                m1.taker=dc.getDocument().get("pickerName").toString();
                            }
                            Date date1;
                            if(dc.getDocument().getData().get("lastWritten")==null){
                                m1.time="Just Now";
                                date1=Calendar.getInstance().getTime();
                            }
                            else{
                                date1=(Date)dc.getDocument().getData().get("lastWritten");
                                Calendar cl=Calendar.getInstance();
                                long diff = cl.getTimeInMillis() - date1.getTime();
                                long days = diff / 86400000;

                                SimpleDateFormat localDateFormat = new SimpleDateFormat("hh:mm a");

                                if(days<1){
                                    m1.time=localDateFormat.format(date1);
                                }
                                else{
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
                                    m1.time=dateFormat.format(date1);
                                }
                            }
                            if(pos==pre){
                                messages.set(pos,m1);
                                timeline.set(pos,date1);
                            }
                            else{
                                messages.remove(pre);
                                timeline.remove(pre);
                                ID.remove(pre);
                                messages.add(pos,m1);
                                timeline.add(pos,date1);
                                ID.add(pos,dc.getDocument().getId());
                            }

                            break;
                        case REMOVED:

                            break;
                    }
                }
                adapter.notifyDataSetChanged();
                if(First_load && f.getDocuments().size()>0){
                    lastDate=f.getDocuments().get(f.getDocuments().size()-1).getDate("lastWritten");
                }
                First_load=false;
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityManager cn=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        network=new network(this);
        IntentFilter wifip2pFilter = new IntentFilter();
        wifip2pFilter.addAction(cn.CONNECTIVITY_ACTION);
        registerReceiver(network, wifip2pFilter);



    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(network);
    }
    public void showbanner(){
        net.setVisibility(View.VISIBLE);
    }
    public void hidebanner(){
        net.setVisibility(View.GONE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(role==1){
        getMenuInflater().inflate(R.menu.manage, menu);}
        if(role==4){
            getMenuInflater().inflate(R.menu.menu_inbox, menu);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings && role==1) {
            startActivity(new Intent(this, ManageActivity.class));
            return true;
        }
        if (id == R.id.action_logout) {
            FirebaseFirestore.getInstance().collection("Tokens").document(myEmail).delete();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }
        if (id == R.id.action_replies && role==1) {
            startActivity(new Intent(this, EditSavedReply.class));
            return true;
        }
        if (id == R.id.action_apo && role==1) {
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("Trigger").document("Appointment");
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(inbox.this);
                            LayoutInflater inflater = inbox.this.getLayoutInflater();
                            View xml = inflater.inflate(R.layout.saved_reply, null);
                            final EditText a = (EditText) xml.findViewById(R.id.title);
                            final EditText b = (EditText) xml.findViewById(R.id.body);

                            a.setText(document.getString("head"));
                            b.setText(document.getString("body"));


                            builder.setView(xml)

                                    .setTitle("Appointment info:")
                                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Map<String, Object> data = new HashMap<>();

                                            data.put("head",a.getText().toString());
                                            data.put("body",b.getText().toString());

                                            CollectionReference replie=FirebaseFirestore.getInstance().collection("Trigger");
                                            replie.document("Appointment").set(data);
                                        }
                                    })
                                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    });
                            builder.show();
                        } else {
                        }
                    }
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }
    public void openChat(String email, String name, boolean onetoone){
        //utils.toast(inbox.this,email+4);
        Intent in= new Intent(inbox.this,chat_user.class);
        in.putExtra("email",email);
        in.putExtra("name",name);
        in.putExtra("role",1);
        in.putExtra("onetoone",onetoone);
        startActivity(in);
    }
}
