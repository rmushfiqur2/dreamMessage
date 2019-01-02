package cloud.mushfiq.buet.dreamsychology;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentListenOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryListenOptions;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class chat_user extends AppCompatActivity {

    ArrayList<message> messages;
    RecyclerView list;
    chatAdapter adapter;
    ImageButton attach, send, picture, love;
    TextView net;
    EditText text1;
    boolean hidden;
    String email;
    String myEmail;
    int role;
    String name;
    boolean onetoone;
    FirebaseFirestore db;
    CollectionReference conversations;
    boolean First_load;
    //DocumentListenOptions options;
    QueryListenOptions options;

    //ArrayList<String> sentIDs;
    ArrayList<message> sent;
    network network;
    LinearLayoutManager LI;
    Date lastDate;
    boolean loaded;
    ProgressBar pro;
    Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            name=extras.getString("name");
            email=extras.getString("email");
            role=extras.getInt("role");
        }
        setContentView(R.layout.activity_chat_user);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater li=LayoutInflater.from(this);
        View mCus=li.inflate(R.layout.action_bar_user,null);
        getSupportActionBar().setCustomView(mCus);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        pro=(ProgressBar)findViewById(R.id.progress);

        if(role!=3){
            onetoone=extras.getBoolean("onetoone");
            TextView t1=(TextView) mCus.findViewById(R.id.text);
            t1.setText(name);
            ImageView image=(ImageView)findViewById(R.id.image);
            image.setImageResource(R.drawable.profile);
            myEmail=FirebaseAuth.getInstance().getCurrentUser().getEmail();
            if(myEmail==null||myEmail.length()==0){
                myEmail=FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            }
        }


        list =  findViewById(R.id.list);
        net=(TextView) findViewById(R.id.network);
        LI=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(LI);
        loaded=false;

        messages= new ArrayList<>();
        //sentIDs = new ArrayList<>();
        sent = new ArrayList<>();

        adapter = new chatAdapter(messages);
        list.setAdapter(adapter);
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (LI.findFirstVisibleItemPosition() == 0 && lastDate!=null) {
                    if(!loaded){
                        loaded=true;
                    CollectionReference conversations = FirebaseFirestore.getInstance().collection(email);
                    conversations.whereLessThan("timestamp", lastDate).orderBy("timestamp", Query.Direction.DESCENDING).limit(15)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Calendar cl = Calendar.getInstance();

                                        //for (DocumentSnapshot document : task.getResult()) {
                                        for(int i=0;i<task.getResult().size();i++){
                                            DocumentSnapshot document=task.getResult().getDocuments().get(i);
                                            if( i==task.getResult().size()-1){
                                                lastDate=(Date) document.getData().get("timestamp");
                                            }
                                            message m = new message();
                                            m.text = document.get("text").toString();
                                            m.type = (int) (long) document.getData().get("type");
                                            Date date = (Date) document.getData().get("timestamp");

                                            SimpleDateFormat localDateFormat = new SimpleDateFormat("hh:mm a");
                                            long diff = cl.getTimeInMillis() - date.getTime();
                                            long days = diff / 86400000;
                                            //int here = cl.getTime().compareTo(date);
                                            if (days < 1) {
                                                m.time = localDateFormat.format(date);
                                            } else if (days < 7) {
                                                m.time = date.toString().substring(0, 3) + " at " + localDateFormat.format(date);
                                            } else {
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yy hh:mm a");
                                                m.time = dateFormat.format(date);
                                            }


                                            messages.add(0, m);
                                        }

                                    }
                                    adapter.notifyDataSetChanged();
                                    if (LI.findFirstVisibleItemPosition() == 0){
                                    LI.scrollToPosition(15);}
                                    if(task.getResult().size()==15){
                                        loaded=false;
                                    }
                                }
                            });
                }
            }
            }
        });

        options = new QueryListenOptions().includeQueryMetadataChanges();


        First_load=true;

        CollectionReference conversations = FirebaseFirestore.getInstance().collection(email);
        conversations.orderBy("timestamp", Query.Direction.DESCENDING).limit(15)
                .addSnapshotListener(options, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot f,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            //Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        Boolean flip=false;
                        int ad_count=0;
                        for(int i=0;i<f.getDocumentChanges().size();i++){
                            DocumentChange dc=f.getDocumentChanges().get(i);
                            if(dc.getType().equals(DocumentChange.Type.ADDED)){
                                ad_count++;
                            }
                        }
                        if(ad_count>1 && !First_load){
                            flip=true;
                        }
                        //utils.toast(chat_user.this,Integer.toString(value.size()));
                        //for (DocumentChange dc : f.getDocumentChanges()) {
                        for(int i=0;i<f.getDocumentChanges().size();i++){
                            DocumentChange dc=f.getDocumentChanges().get(i);
                            if(flip){
                                dc=f.getDocumentChanges().get(f.getDocumentChanges().size()-1-i);
                            }
                            if(First_load && i==f.getDocumentChanges().size()-1){
                                lastDate=dc.getDocument().getDate("timestamp");
                            }

                            switch (dc.getType()) {
                                case ADDED:
                                    message m=new message();
                                    m.text=dc.getDocument().get("text").toString();
                                    m.type=(int)(long)dc.getDocument().getData().get("type");
                                    //if(f.getMetadata().hasPendingWrites()){
                                    if(dc.getDocument().getData().get("timestamp")==null){
                                        m.time="waiting..";
                                        sent.add(m);
                                    }
                                    else{
                                        Date date=(Date)dc.getDocument().getData().get("timestamp");
                                        Calendar cl=Calendar.getInstance();

                                        SimpleDateFormat localDateFormat = new SimpleDateFormat("hh:mm a");
                                        //int here=cl.getTime().compareTo(date);
                                        long diff = cl.getTimeInMillis() - date.getTime();
                                        long days = diff / 86400000;
                                        if(days<1){
                                            m.time=localDateFormat.format(date);
                                        }
                                        else if(days<7){
                                            m.time=date.toString().substring(0,3)+" at " +localDateFormat.format(date);
                                        }
                                        else{
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yy hh:mm a");
                                            m.time=dateFormat.format(date);
                                        }
                                    }


                                    if(First_load){
                                        messages.add(0,m);
                                    }
                                    else{
                                        messages.add(m);
                                    }

                                    adapter.notifyDataSetChanged();
                                    //Log.d(TAG, "New city: " + dc.getDocument().getData());
                                    break;
                                case MODIFIED:
                                    //utils.toast(chat_user.this,Integer.toString(sent.size()));
                                    //utils.toast(chat_user.this,"modify called");
                                    if(sent.size()>0) {
                                        message m1=sent.get(0);
                                        //message m1 = new message();

                                        //m1.text = dc.getDocument().get("text").toString();
                                        //m1.type = (int) (long) dc.getDocument().getData().get("type");

                                        Date date = (Date) dc.getDocument().getData().get("timestamp");
                                        Calendar cl = Calendar.getInstance();

                                        SimpleDateFormat localDateFormat = new SimpleDateFormat("hh:mm a");
                                        //int here = cl.getTime().compareTo(date);
                                        long diff = cl.getTimeInMillis() - date.getTime();
                                        long days = diff / 86400000;
                                        if (days < 1) {
                                            m1.time = localDateFormat.format(date);
                                        } else if (days < 7) {
                                            m1.time = date.toString().substring(0, 3) + " at " + localDateFormat.format(date);
                                        } else {
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yy hh:mm a");
                                            m1.time = dateFormat.format(date);
                                        }
                                        messages.set(messages.indexOf(sent.get(0)), m1);
                                        sent.remove(0);
                                        //messages.add(messages.indexOf(sent.get(0)),m1);


                                        adapter.notifyDataSetChanged();
                                    }

                                    break;
                                case REMOVED:
                                    //Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }
                        Map<String, Object> data = new HashMap<>();
                        if(role!=3){
                        data.put(myEmail,FieldValue.serverTimestamp());}

                        CollectionReference index=FirebaseFirestore.getInstance().collection("Index");
                        index.document(email).set(data,SetOptions.merge());
                        //utils.toast(chat_user.this,email);
                        list.getLayoutManager().scrollToPosition(messages.size()-1);
                        First_load=false;
                    }
                });


        attach=(ImageButton) findViewById(R.id.attachment);
        picture=(ImageButton) findViewById(R.id.picture);
        love=(ImageButton) findViewById(R.id.others);
        send=(ImageButton) findViewById(R.id.send);
        text1=(EditText) findViewById(R.id.edittext);
        text1.addTextChangedListener(T1);
        hidden=false;


        text1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()){
                    if(!hidden) {
                        attach.setVisibility(View.GONE);
                        picture.setVisibility(View.GONE);
                        hidden = true;
                        text1.setHint(" Are you Okhay?");
                    }
                }

                return false;
            }

        });

        //db = FirebaseFirestore.getInstance();
        //CollectionReference conversations = db.collection("Conversations");

    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }



    private final TextWatcher T1=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(text1.getText().toString().equals("")){
            }
            else{
                send.setImageResource(R.drawable.send);
                if(!hidden){
                attach.setVisibility(View.GONE);
                picture.setVisibility(View.GONE);
                hidden=true;}
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.length()==0){
                send.setImageResource(R.drawable.rose);
                attach.setVisibility(View.VISIBLE);
                picture.setVisibility(View.VISIBLE);
                text1.setHint("Abc");
                hidden=false;
            }
        }
    };

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(role==3){
        getMenuInflater().inflate(R.menu.menu_main, menu);}
        else if(role==2){
            if(onetoone){
                getMenuInflater().inflate(R.menu.vol_menu1, menu);
            }
            else{
                getMenuInflater().inflate(R.menu.vol_menu, menu);
            }
        }
        else{
            if(onetoone){
                getMenuInflater().inflate(R.menu.admin_menu1, menu);
            }
            else{
                getMenuInflater().inflate(R.menu.admin_menu, menu);
            }

        }

        this.menu=menu;
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //list.smoothScrollToPosition(100);
            return true;
        }
        if (id == R.id.action_logout) {
            if(role==3){
                FirebaseFirestore.getInstance().collection("Tokens").document(email).delete();
            }
            else{
                FirebaseFirestore.getInstance().collection("Tokens").document(myEmail).delete();
            }
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
            return true;
        }
        if(id==R.id.action_one2one){
            pro.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            final CollectionReference index = FirebaseFirestore.getInstance().collection("Index");
            index.document(email).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.getMetadata().isFromCache()) {
                                    pro.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    utils.toast(chat_user.this, "Connect to internet and re-try.");
                                } else {
                                    Boolean pickedUp = (Boolean) document.getData().get("pickedUp");
                                    if (!onetoone) {
                                        if (!pickedUp) {
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("pickedUp", true);
                                            data.put("pickedBy", myEmail);
                                            data.put("pickerName", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                            index.document(email).set(data, SetOptions.merge())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            pro.setVisibility(View.GONE);
                                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                            utils.toast(chat_user.this, "You have taken this thread.");
                                                            MenuItem item = menu.findItem(R.id.action_one2one);
                                                            item.setTitle("Release (!)");
                                                            MenuItem item1 = menu.findItem(R.id.action_settings);
                                                            item1.setIcon(R.drawable.onetoone);
                                                            onetoone=true;
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            pro.setVisibility(View.GONE);
                                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                            utils.toast(chat_user.this, "Please Re-try.");
                                                        }
                                                    });
                                        } else {
                                            pro.setVisibility(View.GONE);
                                            if(role==1){
                                                MenuItem item = menu.findItem(R.id.action_one2one);
                                                item.setTitle("Release (!)");
                                                MenuItem item1 = menu.findItem(R.id.action_settings);
                                                item1.setIcon(R.drawable.onetoone);
                                                onetoone=true;
                                            }
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            utils.toast(chat_user.this, "Opps! Already taken by someone.");
                                        }
                                    }
                                    else {
                                        if (pickedUp) {
                                            String pickedBy = (String) document.getData().get("pickedBy");
                                            if (pickedBy.equals(myEmail) || role ==1){
                                                Map<String, Object> data = new HashMap<>();
                                            data.put("pickedUp", false);
                                            data.put("pickedBy", "");
                                            data.put("pickerName", "");
                                            index.document(email).set(data, SetOptions.merge())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            pro.setVisibility(View.GONE);
                                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                            utils.toast(chat_user.this, "You have released this thread.");
                                                            MenuItem item = menu.findItem(R.id.action_one2one);
                                                            item.setTitle("Pick Up");
                                                            MenuItem item1 = menu.findItem(R.id.action_settings);
                                                            item1.setIcon(R.drawable.public_icon);
                                                            onetoone=false;
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            pro.setVisibility(View.GONE);
                                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                            utils.toast(chat_user.this, "Please Re-try.");
                                                        }
                                                    });
                                        }
                                        else{
                                                pro.setVisibility(View.GONE);
                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                utils.toast(chat_user.this, "This conversation has taken by another person.");
                                            }
                                    }else {
                                            pro.setVisibility(View.GONE);
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            utils.toast(chat_user.this, "Opps! This was a open thread.");
                                            MenuItem item = menu.findItem(R.id.action_one2one);
                                            item.setTitle("Pick Up");
                                            MenuItem item1 = menu.findItem(R.id.action_settings);
                                            item1.setIcon(R.drawable.public_icon);
                                            onetoone=false;
                                        }

                                    }
                                }
                                }
                            else{
                                    pro.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    utils.toast(chat_user.this, "Connect to internet and re-try.");
                                }

                        }
                    });

        }

        return super.onOptionsItemSelected(item);
    }
    public void others(View v){
        if(role!=3) {
            saveDialogFragment alertDialog = new saveDialogFragment();// myFragment.newInstance("Some title");
            alertDialog.setCancelable(true);
            alertDialog.show(getSupportFragmentManager(), "abba");
        }
    }
    public void picture(View v){
    }

    public void attachment(View v){
        if(role!=3) {
            FirebaseFirestore.getInstance().collection("Replies").orderBy("title")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<String> all = new ArrayList<>();
                                final ArrayList<String> al = new ArrayList<>();
                                final ArrayList<Boolean> a = new ArrayList<>();
                                for (DocumentSnapshot document : task.getResult()) {
                                    all.add(document.getString("title"));
                                    al.add(document.getString("message"));
                                    a.add(document.getBoolean("personalize"));
                                }
                                AlertDialog.Builder builder = new AlertDialog.Builder(chat_user.this);
                                builder.setTitle("Saved Replies")
                                        .setItems(all.toArray(new CharSequence[all.size()]), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (a.get(which)) {
                                                    text1.setText(name + ", " + al.get(which));
                                                } else {
                                                    text1.setText(al.get(which));
                                                }
                                            }
                                        });
                                builder.show();
                            } else {
                                utils.toast(chat_user.this, "Connection problem.");
                            }
                        }
                    });
        }

    }
    public void showbanner(){
        net.setVisibility(View.VISIBLE);
    }
    public void hidebanner(){
        net.setVisibility(View.GONE);
    }
public void send(View v){
        String msg=text1.getText().toString().trim();
        if(msg.length()>0){
            text1.setText("");
            /*message m=new message();
            m.type=2; m.text=msg; m.seenicon=false; m.time="waiting..";
            messages.add(m);
            adapter.notifyDataSetChanged();
            list.getLayoutManager().scrollToPosition(messages.size()-1);*/


            Map<String, Object> data = new HashMap<>();
            Map<String, Object> ind = new HashMap<>();
            Map<String, Object> notidata = new HashMap<>();
            notidata.put("chn",email);

            if(role==3){
                data.put("type",2);
                ind.put("ourMessage",false);
            }
            else{
                data.put("type",1);
                ind.put("ourMessage",true);
                ind.put("comment",false);
            }
            data.put("text",msg);
            data.put("timestamp",FieldValue.serverTimestamp());


            ind.put("lastWritten",FieldValue.serverTimestamp());
            ind.put("text",msg);

            CollectionReference conversations = FirebaseFirestore.getInstance().collection(email);
            CollectionReference index=FirebaseFirestore.getInstance().collection("Index");
            CollectionReference noti=FirebaseFirestore.getInstance().collection("Notification");
            noti.document("examine").set(notidata);
            index.document(email).set(ind,SetOptions.merge());
            conversations.add(data);

        }
    }
    public static class saveDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View xml = inflater.inflate(R.layout.saved_reply, null);
            final EditText a = (EditText) xml.findViewById(R.id.title);
            final EditText b = (EditText) xml.findViewById(R.id.body);
            final CheckBox c=(CheckBox) xml.findViewById(R.id.personalize);

            builder.setView(xml)

            .setTitle(R.string.dialog_save)
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Map<String, Object> data = new HashMap<>();

                            data.put("title",a.getText().toString());
                            data.put("message",b.getText().toString());
                            data.put("personalize",c.isChecked());

                            CollectionReference replies=FirebaseFirestore.getInstance().collection("Replies");
                            replies.add(data);

                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            return builder.create();
        }
    }
}
