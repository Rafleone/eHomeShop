package ehomeshop.com.activities;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.PropertyName;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ehomeshop.com.R;
import ehomeshop.com.adapters.AdapterChat;
import ehomeshop.com.models.ModelChat;

public class ChatActivity extends AppCompatActivity {

    //views from xml
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIv;
    TextView nameTv, userStatusTv;
    EditText messageEt;
    ImageButton sendBtn;


    //firebase auth
    FirebaseAuth firebaseAuth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;

    //for checking if use has senn message or not
    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;

    List<ModelChat> chatList;
    AdapterChat adapterChat;

    String hisUid;
    String myUid;
    String hisImage;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //init views
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView = findViewById(R.id.chat_recyclerView);
        profileIv = findViewById(R.id.profileIv);
        nameTv = findViewById(R.id.nameTv);
        userStatusTv = findViewById(R.id.userStatusTv);
        messageEt = findViewById(R.id.messageEt);
        sendBtn = findViewById(R.id.sendBtn);

        //Layout (LinearLayout) for RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        //recyclerView properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDbRef = firebaseDatabase.getReference("Users");

        //Search user to get that users info
        Query userQuery = usersDbRef.orderByChild("uid").equalTo(hisUid);

        //get user picture and name
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check until required info is received
                for (DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    String name = ""+ ds.child("name").getValue();
                    hisImage = "" +ds.child("profileImage").getValue();
                    String typingStatus = "" + ds.child("typingTo").getValue();

                    //check typing statys
                    if (typingStatus.equals(myUid)){
                        userStatusTv.setText("Typing...");
                    }
                    else {
                        //get value of online Status
                        String onlineStatus = ""+ ds.child("onlineStatus").getValue();
                        if (onlineStatus.equals("online")){
                            userStatusTv.setText(onlineStatus);
                        } else {
                            //Convert timeStamp to proper time date
                            //convert time stamp to dd/mm/yyyy hh:mm am/pm
                            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                            calendar.setTimeInMillis(Long.parseLong(onlineStatus));
                            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
                            userStatusTv.setText("Last seen at: " + dateTime);
                        }
                    }

                    //set data
                    nameTv.setText(name);
                    try {
                        //image received, set to imageview in toolbar
                        Picasso.get().load(hisImage).placeholder(R.drawable.ic_face_white).into(profileIv);

                    } catch (Exception e){
                        //there is exetion getting picture, set default picture
                        Picasso.get().load(R.drawable.ic_face_white).into(profileIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //click button to send message
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get text from edit text
                String message = messageEt.getText().toString().trim();
                //check if text is empty or not
                if (TextUtils.isEmpty(message)){
                    //text empty
                    Toast.makeText(ChatActivity.this, "Cannot send the empty message...", Toast.LENGTH_SHORT).show();
                } else {
                    //text not empty
                    sendMessage(message);
                }
            }
        });

        //check Edit text change listener
        messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0){
                    checkTypingStatus("noOne");
                }
                else {
                    checkTypingStatus(hisUid); // uid of receiver
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        readMessages();

        seenMessage();

    }

    private void seenMessage() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Users").child("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)){
                        HashMap<String, Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("isSeen", true);
                        ds.getRef().updateChildren(hasSeenHashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessages() {
        chatList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid) ||
                            chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid)){
                        chatList.add(chat);
                    }

                    //adapter
                    adapterChat = new AdapterChat(ChatActivity.this, chatList, hisImage);
                    adapterChat.notifyDataSetChanged();
                    //set adapter to recyclerView
                    recyclerView.setAdapter(adapterChat);
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void sendMessage(String message) {



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        //databaseReference.child(firebaseAuth.getUid());

        String timeStamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUid);
        hashMap.put("message", message);
        hashMap.put("timestamp", timeStamp);
        hashMap.put("isSeen", false);
        databaseReference.child("Chats").push().setValue(hashMap);

        //reset editText after sending message
        messageEt.setText("");
    }

    private void checkUserStatus(){
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            //user is signed in stay here
            //set email of logged in user
            //mProfileTv.settext(user.getEmail()));
            myUid = user.getUid(); // currently signed in users uid
        } else {
            //user not signed in, go to main activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void checkOnlineStatus(String status){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);

        //update value of onlineStatus of Current User
        ref.updateChildren(hashMap);
    }

    private void checkTypingStatus(String typing){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);

        //update value of onlineStatus of Current User
        ref.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        //set online
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //get timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());

        //set offline with last seen time stamp
        checkOnlineStatus(timestamp);

        checkTypingStatus("noOne");
        userRefForSeen.removeEventListener(seenListener);
    }

    @Override
    protected void onResume() {
        //set online
        checkOnlineStatus("online");
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu_nav, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.logoutBtn){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}