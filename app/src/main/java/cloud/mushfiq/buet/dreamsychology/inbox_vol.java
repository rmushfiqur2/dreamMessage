package cloud.mushfiq.buet.dreamsychology;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryListenOptions;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.Console;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class inbox_vol extends AppCompatActivity implements listToChat{
    ArrayList<msg_preview> messages;
    ArrayList<Date> timeline;
    ArrayList<msg_preview> msg_pub;
    ArrayList<msg_preview> msg_slf;
    network network;
    //Menu menu;
    String myEmail;
    RecyclerView list;
    Iadapter adapter;
    TextView net;
    LinearLayoutManager LI;
    boolean loaded;
    boolean First_load;
    boolean First_load1;
    Date lastDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            myEmail=extras.getString("email");
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
        msg_pub= new ArrayList<>();
        msg_slf= new ArrayList<>();
        adapter=new Iadapter(messages,this);
        list.setAdapter(adapter);
        list.hasFixedSize();
        final CollectionReference conversations = FirebaseFirestore.getInstance().collection("Index");
        final Query open=conversations.whereEqualTo("pickedUp",false).orderBy("lastWritten", Query.Direction.DESCENDING).limit(10);
        final Query self=conversations.whereEqualTo("pickedBy",myEmail).orderBy("lastWritten", Query.Direction.DESCENDING).limit(10);
        final Query open1=conversations.whereEqualTo("pickedUp",false).orderBy("lastWritten", Query.Direction.DESCENDING);
        final Query self1=conversations.whereEqualTo("pickedBy",myEmail).orderBy("lastWritten", Query.Direction.DESCENDING);
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (LI.findLastVisibleItemPosition() == messages.size()-1  && !loaded && lastDate!=null && !First_load && !First_load1) {
                    //utils.toast(inbox_vol.this,"0 runned");
                    loaded=true;
                    open1.startAfter(lastDate).limit(10).get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot documentSnapshots) {
                                    final int size1=documentSnapshots.size();
                                    final Date date1=(size1>0)? documentSnapshots.getDocuments().get(documentSnapshots.size() - 1).getDate("lastWritten"):null;

                                    self1.startAfter(lastDate).limit(10).get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot documentSnapshots) {
                                                    int size2=documentSnapshots.size();
                                                    Date date2=(size2>0)? documentSnapshots.getDocuments().get(documentSnapshots.size() - 1).getDate("lastWritten"):null;
                                                    if(size1<1 && size2<1){
                                                        return;
                                                    }
                                                    else if(size2<9){
                                                        date2=date1;
                                                    }
                                                    else if(size1<9){
                                                    }
                                                    else{
                                                        if(date2.compareTo(date1)>0){
                                                        }
                                                        else{
                                                            date2=date1;
                                                        }
                                                    }
                                                    final Date peak=lastDate;

                                                    conversations.whereEqualTo("pickedUp",false).orderBy("lastWritten", Query.Direction.DESCENDING).startAfter(lastDate).endAt(date2)
                                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onEvent(@Nullable QuerySnapshot f,
                                                                                    @Nullable FirebaseFirestoreException e) {
                                                                    if (e != null) {
                                                                        return;
                                                                    }
                                                                    for(DocumentChange dc : f.getDocumentChanges()){
                                                                        switch (dc.getType()) {
                                                                            case ADDED:
                                                                                Date myseen;
                                                                                Date heWroteAt;
                                                                                msg_preview m=new msg_preview();

                                                                                m.name=(String)dc.getDocument().getData().get("name");
                                                                                m.email=(String)dc.getDocument().getId();

                                                                                m.text=(String)dc.getDocument().getData().get("text");
                                                                                m.namekey=(String)dc.getDocument().getData().get("nameKey");
                                                                                m.isTaken=(Boolean) dc.getDocument().getData().get("pickedUp");
                                                                                m.our_msg=(Boolean) dc.getDocument().getData().get("ourMessage");

                                                                                if(dc.getDocument().getData().get(myEmail)==null){
                                                                                    m.newmsg=true;
                                                                                }
                                                                                else{
                                                                                    myseen=(Date)dc.getDocument().getData().get(myEmail);
                                                                                    if(!m.our_msg ){
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

                                                                                if(dc.getDocument().getData().get("lastWritten")==null){
                                                                                    m.time="Just Now";
                                                                                    timeline.add(Calendar.getInstance().getTime());
                                                                                }
                                                                                else{
                                                                                    Date date=(Date)dc.getDocument().getData().get("lastWritten");
                                                                                    timeline.add(date);

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
                                                                                messages.add(m);
                                                                                msg_pub.add(m);


                                                                                break;
                                                                            case MODIFIED:

                                                                                break;
                                                                            case REMOVED:
                                                                                int pos1=dc.getOldIndex();
                                                                                for(int i=0;i<timeline.size();i++){
                                                                                    if(timeline.get(i).compareTo(peak)<0){
                                                                                        messages.remove(i+pos1);
                                                                                        timeline.remove(i+pos1);
                                                                                        break;
                                                                                    }
                                                                                }

                                                                                break;
                                                                        }
                                                                    }
                                                                    adapter.notifyDataSetChanged();
                                                                }
                                                            });
                                                    conversations.whereEqualTo("pickedBy",myEmail).orderBy("lastWritten", Query.Direction.DESCENDING).startAfter(lastDate).endAt(date2)
                                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onEvent(@Nullable QuerySnapshot f,
                                                                                    @Nullable FirebaseFirestoreException e) {
                                                                    if (e != null) {
                                                                        return;
                                                                    }

                                                                    for(DocumentChange dc :f.getDocumentChanges()){
                                                                        switch (dc.getType()) {
                                                                            case ADDED:
                                                                                Date myseen;
                                                                                Date heWroteAt;
                                                                                msg_preview m=new msg_preview();

                                                                                m.name=(String)dc.getDocument().getData().get("name");
                                                                                m.email=(String)dc.getDocument().getId();



                                                                                m.text=(String)dc.getDocument().getData().get("text");
                                                                                m.namekey=(String)dc.getDocument().getData().get("nameKey");
                                                                                m.isTaken=(Boolean) dc.getDocument().getData().get("pickedUp");
                                                                                m.our_msg=(Boolean) dc.getDocument().getData().get("ourMessage");

                                    if(dc.getDocument().getData().get(myEmail)==null){
                                        m.newmsg=true;
                                    }
                                    else{
                                        myseen=(Date)dc.getDocument().getData().get(myEmail);
                                        if(!m.our_msg ){
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
                                                                                int whre=-1;
                                                                                if(dc.getDocument().getData().get("lastWritten")==null){
                                                                                    m.time="Just Now";
                                                                                    whre=0;
                                                                                    timeline.add(0,Calendar.getInstance().getTime());
                                                                                }
                                                                                else{
                                                                                    Date date=(Date)dc.getDocument().getData().get("lastWritten");
                                                                                    for(int s=timeline.size()-1;s>-1;s--){
                                                                                        if(date.compareTo(timeline.get(s))>0){
                                                                                            whre=s;
                                                                                            timeline.add(s,date);
                                                                                            break;
                                                                                        }
                                                                                    }
                                                                                    if(whre==-1){
                                                                                        timeline.add(date);
                                                                                    }

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
                                                                                if(whre!=-1) {
                                                                                    messages.add(whre, m);
                                                                                    msg_slf.add(msg_slf.size(),m);
                                                                                }
                                                                                else{
                                                                                    messages.add(m);
                                                                                    msg_slf.add(m);
                                                                                }
                                                                                break;
                                                                            case MODIFIED:

                                                                            case REMOVED:
                                                                                int pos1=dc.getOldIndex();
                                                                                for(int i=0;i<timeline.size();i++){
                                                                                    if(timeline.get(i).compareTo(peak)<0){
                                                                                        messages.remove(i+pos1);
                                                                                        timeline.remove(i+pos1);
                                                                                        break;
                                                                                    }
                                                                                }
                                                                                break;
                                                                        }
                                                                    }
                                                                    adapter.notifyDataSetChanged();
                                                                    loaded=false;
                                                                }
                                                            });
                                                    lastDate=date2;


                                                }
                                            });
                                }
                            });

                }
            }
        });

        open.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                final int size1=documentSnapshots.size();
                final Date date1=(size1>0)? documentSnapshots.getDocuments().get(documentSnapshots.size() - 1).getDate("lastWritten"):null;

                self.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        int size2=documentSnapshots.size();
                        Date date2=(size2>0)? documentSnapshots.getDocuments().get(documentSnapshots.size() - 1).getDate("lastWritten"):null;
                        if(size1<1 && size2<1){
                            return;
                        }
                        else if(size2<9){
                            date2=date1;
                        }
                        else if(size1<9){
                        }
                        else{
                            if(date2.compareTo(date1)>0){
                            }
                            else{
                                date2=date1;
                            }
                        }
                        lastDate=date2;
                        conversations.whereEqualTo("pickedUp",false).orderBy("lastWritten", Query.Direction.DESCENDING).endAt(date2)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot f, @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            return;
                                        }
                                        for(DocumentChange dc : f.getDocumentChanges()){
                                            switch (dc.getType()) {
                                                case ADDED:
                                                    Log.d("Edit pub add",dc.getDocument().getId()+" "+First_load);
                                                    Date myseen;
                                                    Date heWroteAt;
                                                    msg_preview m=new msg_preview();

                                                    m.name=(String)dc.getDocument().getData().get("name");
                                                    m.email=(String)dc.getDocument().getId();



                                                            m.text=(String)dc.getDocument().getData().get("text");
                                                            m.namekey=(String)dc.getDocument().getData().get("nameKey");
                                                    m.isTaken=(Boolean) dc.getDocument().getData().get("pickedUp");
                                                            m.our_msg=(Boolean) dc.getDocument().getData().get("ourMessage");

                                                    if(dc.getDocument().getData().get(myEmail)==null){
                                                        m.newmsg=true;
                                                    }
                                                    else{
                                                        myseen=(Date)dc.getDocument().getData().get(myEmail);
                                                        if(!m.our_msg ){
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
                                                    int df=-1;
                                                    if(dc.getDocument().getData().get("lastWritten")==null){
                                                        m.time="Just Now";
                                                        df=0;
                                                        timeline.add(0,Calendar.getInstance().getTime());
                                                    }
                                                    else{
                                                        Date date=(Date)dc.getDocument().getData().get("lastWritten");
                                                        if(First_load){
                                                            timeline.add(date);
                                                        }
                                                        else{
                                                            for(int s=0;s<timeline.size();s++){
                                                                if(date.compareTo(timeline.get(s))>0){
                                                                    df=s;
                                                                    timeline.add(s,date);
                                                                    break;
                                                                }
                                                            }
                                                            if(df==-1){
                                                                timeline.add(date);
                                                            }
                                                        }
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
                                                        msg_pub.add(m);
                                                    }
                                                    else{
                                                        if(df==-1){
                                                            messages.add(m);
                                                            msg_pub.add(m);
                                                        }
                                                        else {
                                                            messages.add(df, m);
                                                            msg_pub.add(dc.getNewIndex(), m);
                                                        }
                                                        Log.d("Edit Pub add",dc.getDocument().getId()+ "at"+Integer.toString(df));
                                                    }

                                                    break;
                                                case MODIFIED:
                                                    //utils.toast(inbox_vol.this,dc.getDocument().getId());
                                                    Log.d("Edit Pub",dc.getDocument().getId()+" "+Integer.toString(msg_pub.size()));
                                                    int pos=dc.getNewIndex();
                                                    int pre=dc.getOldIndex();
                                                    int po=messages.indexOf(msg_pub.get(pre));
                                                    Log.d("Edit pub del :", msg_pub.get(pre).name);
                                                    /*if(pos==pre){
                                                        msg_preview cor=msg_pub.get(pre);
                                                        cor.isTaken=(Boolean) dc.getDocument().getData().get("pickedUp");
                                                        if(cor.isTaken){
                                                            cor.taker=dc.getDocument().get("pickerName").toString();
                                                        }
                                                        messages.set(po,cor);
                                                        msg_pub.set(pos,cor);
                                                    }*/
                                                    //else {
                                                        messages.remove(po);
                                                        timeline.remove(po);
                                                        msg_pub.remove(pre);

                                                        msg_preview m1 = new msg_preview();
                                                        m1.email = (String) dc.getDocument().getId();
                                                        m1.name = (String) dc.getDocument().getData().get("name");
                                                        m1.namekey = (String) dc.getDocument().getData().get("nameKey");
                                                        m1.text = (String) dc.getDocument().getData().get("text");
                                                        m1.isTaken = (Boolean) dc.getDocument().getData().get("pickedUp");
                                                        m1.our_msg = (Boolean) dc.getDocument().getData().get("ourMessage");
                                                        //m1.newmsg = !m1.our_msg;
                                                    if(dc.getDocument().getData().get(myEmail)==null){
                                                        m1.newmsg=true;
                                                        Log.d("Edit Pub new_msg","null");
                                                    }
                                                    else{
                                                        myseen=(Date)dc.getDocument().getData().get(myEmail);
                                                        if(!m1.our_msg ){
                                                            heWroteAt=(Date)dc.getDocument().getData().get("lastWritten");
                                                            if(heWroteAt.compareTo(myseen)>0){
                                                                m1.newmsg=true;
                                                                Log.d("Edit Pub new_msg","it is new msg");
                                                            }
                                                            else{
                                                                m1.newmsg=false;
                                                                Log.d("Edit Pub new_msg","it is old msg");
                                                            }
                                                        }
                                                        else{
                                                            m1.newmsg=false;
                                                        }

                                                    }
                                                    if(m1.isTaken){
                                                        m1.taker=dc.getDocument().get("pickerName").toString();
                                                    }
                                                    int df1=-1;
                                                    if(dc.getDocument().getData().get("lastWritten")==null){
                                                        Log.d("Edit Pub","null");
                                                        m1.time="Just Now";
                                                        df1=0;
                                                        timeline.add(0,Calendar.getInstance().getTime());
                                                        //Log.d("lastWritten","first");
                                                    }
                                                    else{
                                                        Date date=(Date)dc.getDocument().getData().get("lastWritten");


                                                            for(int s=0;s<timeline.size();s++){
                                                                if(date.compareTo(timeline.get(s))>0){
                                                                    df1=s;
                                                                    timeline.add(s,date);
                                                                    break;
                                                                }
                                                            }
                                                            if(df1==-1){
                                                                timeline.add(date);
                                                            }

                                                            Calendar cl=Calendar.getInstance();
                                                            long diff = cl.getTimeInMillis() - date.getTime();
                                                            long days = diff / 86400000;

                                                            SimpleDateFormat localDateFormat = new SimpleDateFormat("hh:mm a");

                                                            if(days<1){
                                                                m1.time=localDateFormat.format(date);
                                                            }
                                                            else{
                                                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
                                                                m1.time=dateFormat.format(date);
                                                            }
                                                            Log.d("Edit pub count",Integer.toString(messages.size()));
                                                    }
                                                        if(df1==-1){
                                                            messages.add(m1);
                                                            msg_pub.add(m1);
                                                        }
                                                        else {
                                                            messages.add(df1, m1);
                                                            msg_pub.add(dc.getNewIndex(), m1);
                                                            Log.d("Edit pub count",Integer.toString(messages.size()));
                                                        }


                                                    break;
                                                case REMOVED:
                                                    Log.d("Edit Pub del",dc.getDocument().getId());
                                                    int pos1=dc.getOldIndex();
                                                    int po1=messages.indexOf(msg_pub.get(pos1));
                                                    messages.remove(po1);
                                                    timeline.remove(po1);
                                                    msg_pub.remove(pos1);

                                                    break;
                                            }
                                        }
                                        adapter.notifyDataSetChanged();
                                        First_load=false;
                                    }
                                });
                        conversations.whereEqualTo("pickedBy",myEmail).orderBy("lastWritten", Query.Direction.DESCENDING).endAt(date2)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot f, @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            return;
                                        }

                                        for(DocumentChange dc :f.getDocumentChanges()){
                                            switch (dc.getType()) {
                                                case ADDED:
                                                    Log.d("Edit slf add",dc.getDocument().getId()+" "+First_load1);
                                                    Date myseen;
                                                    Date heWroteAt;
                                                    msg_preview m=new msg_preview();

                                                    m.name=(String)dc.getDocument().getData().get("name");
                                                    m.email=(String)dc.getDocument().getId();


                                                            m.text=(String)dc.getDocument().getData().get("text");
                                                            m.namekey=(String)dc.getDocument().getData().get("nameKey");
                                                            m.isTaken=(Boolean) dc.getDocument().getData().get("pickedUp");
                                                    m.our_msg=(Boolean) dc.getDocument().getData().get("ourMessage");

                                    if(dc.getDocument().getData().get(myEmail)==null){
                                        m.newmsg=true;
                                    }
                                    else{
                                        myseen=(Date)dc.getDocument().getData().get(myEmail);
                                        if(!m.our_msg ){
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
                                                    int whre=-1;
                                                    if(dc.getDocument().getData().get("lastWritten")==null){
                                                        m.time="Just Now";
                                                        whre=0;
                                                        timeline.add(0,Calendar.getInstance().getTime());
                                                    }
                                                    else{
                                                        Date date=(Date)dc.getDocument().getData().get("lastWritten");
                                                        for(int s=0;s<timeline.size();s++){
                                                            if(date.compareTo(timeline.get(s))>0){
                                                                whre=s;
                                                                timeline.add(s,date);
                                                                break;
                                                            }
                                                        }
                                                        if(whre==-1){
                                                            timeline.add(date);
                                                        }

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
                                                    if(whre!=-1) {
                                                        messages.add(whre, m);
                                                        msg_slf.add(dc.getNewIndex(),m);
                                                    }
                                                    else{
                                                        messages.add(m);
                                                        msg_slf.add(m);
                                                    }
                                                    Log.d("Edit Pub add",dc.getDocument().getId()+ "at"+Integer.toString(whre));
                                                    break;
                                                case MODIFIED:
                                                    int pos=dc.getNewIndex();
                                                    int pre=dc.getOldIndex();
                                                    int po=messages.indexOf(msg_slf.get(pre));

                                                    messages.remove(po);
                                                    timeline.remove(po);
                                                    msg_slf.remove(pre);

                                                    msg_preview m1 = new msg_preview();
                                                    m1.email = (String) dc.getDocument().getId();
                                                    m1.name = (String) dc.getDocument().getData().get("name");
                                                    m1.namekey = (String) dc.getDocument().getData().get("nameKey");
                                                    m1.text = (String) dc.getDocument().getData().get("text");
                                                    m1.isTaken = (Boolean) dc.getDocument().getData().get("pickedUp");
                                                    m1.our_msg = (Boolean) dc.getDocument().getData().get("ourMessage");
                                                    //m1.newmsg = !m1.our_msg;
                                                    if(dc.getDocument().getData().get(myEmail)==null){
                                                        m1.newmsg=true;
                                                    }
                                                    else{
                                                        myseen=(Date)dc.getDocument().getData().get(myEmail);
                                                        if(!m1.our_msg ){
                                                            heWroteAt=(Date)dc.getDocument().getData().get("lastWritten");
                                                            if(heWroteAt.compareTo(myseen)>0){
                                                                m1.newmsg=true;
                                                            }
                                                            else{
                                                                m1.newmsg=false;
                                                            }
                                                        }
                                                        else{
                                                            m1.newmsg=false;
                                                        }

                                                    }
                                                    if(m1.isTaken){
                                                        m1.taker=dc.getDocument().get("pickerName").toString();
                                                    }
                                                    int df1=-1;
                                                    if(dc.getDocument().getData().get("lastWritten")==null){
                                                        m1.time="Just Now";
                                                        df1=0;
                                                        timeline.add(0,Calendar.getInstance().getTime());
                                                    }
                                                    else{
                                                        Date date=(Date)dc.getDocument().getData().get("lastWritten");


                                                        for(int s=0;s<timeline.size();s++){
                                                            if(date.compareTo(timeline.get(s))>0){
                                                                df1=s;
                                                                timeline.add(s,date);
                                                                break;
                                                            }
                                                        }
                                                        if(df1==-1){
                                                            timeline.add(date);
                                                        }

                                                        Calendar cl=Calendar.getInstance();
                                                        long diff = cl.getTimeInMillis() - date.getTime();
                                                        long days = diff / 86400000;

                                                        SimpleDateFormat localDateFormat = new SimpleDateFormat("hh:mm a");

                                                        if(days<1){
                                                            m1.time=localDateFormat.format(date);
                                                        }
                                                        else{
                                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
                                                            m1.time=dateFormat.format(date);
                                                        }
                                                        Log.d("Edit pub count",Integer.toString(messages.size()));
                                                    }
                                                    if(df1==-1){
                                                        messages.add(m1);
                                                        msg_slf.add(m1);
                                                    }
                                                    else {
                                                        messages.add(df1, m1);
                                                        msg_slf.add(dc.getNewIndex(), m1);
                                                        Log.d("Edit pub count",Integer.toString(messages.size()));
                                                    }
                                                    break;
                                                case REMOVED:
                                                    Log.d("Edit slf del",dc.getDocument().getId());
                                                    int pos1=dc.getOldIndex();
                                                    int po1=messages.indexOf(msg_slf.get(pos1));
                                                    messages.remove(po1);
                                                    timeline.remove(po1);
                                                    msg_slf.remove(pos1);
                                                    break;
                                            }
                                        }
                                        adapter.notifyDataSetChanged();
                                        First_load1=false;
                                    }
                                });

                            }
                        });
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
        getMenuInflater().inflate(R.menu.menu_inbox, menu);

        //this.menu=menu;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_logout) {
            FirebaseFirestore.getInstance().collection("Tokens").document(myEmail).delete();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void openChat(String email, String name, boolean onetoone){
        //utils.toast(inbox_vol.this,email+677);
        Intent in= new Intent(inbox_vol.this,chat_user.class);
        in.putExtra("email",email);
        in.putExtra("name",name);
        in.putExtra("role",2);
        in.putExtra("onetoone",onetoone);
        startActivity(in);
    }
}
