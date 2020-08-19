package ehomeshop.com.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

import ehomeshop.com.R;
import ehomeshop.com.adapters.AdapterOrderShop;
import ehomeshop.com.models.ModelChat;

public class ChatActivity extends AppCompatActivity {

    private String textMessage;
    private String userMessage;
    private String timeMessage;

    private TextView message_user, message_time, message_text;
    private FloatingActionButton sendBtn;
    private RelativeLayout activity_chat;
    private EditText messageField;

    private static final int SIGN_IN_REQUEST_CODE = 1;

    private FirebaseListAdapter<ModelChat> adapter;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                Snackbar.make(activity_chat, "Jus PRisijunges", Snackbar.LENGTH_LONG).show();

                displayAllMessages();
            } else {
                Snackbar.make(activity_chat, "Jus ne PRisijunges", Snackbar.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseAuth = FirebaseAuth.getInstance();

        message_user = findViewById(R.id.message_user);
        message_time = findViewById(R.id.message_time);
        message_text = findViewById(R.id.message_text);
        messageField = findViewById(R.id.messageField);
        activity_chat = findViewById(R.id.activity_chat);
        sendBtn = findViewById(R.id.btnSend);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText textField = findViewById(R.id.messageField);
                if (textField.getText().toString() == ""){
                    return;
                }

                String timestamp = "" + System.currentTimeMillis();
                textMessage = messageField.getText().toString().trim();
                timeMessage = timestamp;

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("messageText", textMessage);
                hashMap.put("messageTime", timestamp);
                hashMap.put("messageUser", FirebaseAuth.getInstance().getCurrentUser().getEmail());

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                ref.child(firebaseAuth.getUid()).child("Chat").child(timestamp).setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                //veikiantis
//                FirebaseDatabase.getInstance().getReference("Users")
//                        .child("Chat")
//                        .push()
//                        .setValue(new ModelChat
//                                (FirebaseAuth.getInstance().getCurrentUser().getEmail(),
//                                        textField.getText().toString()
//                                )
//                        );
//                textField.setText("");
            }
        });

        if (FirebaseAuth.getInstance() == null){
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        } else {
            Snackbar.make(activity_chat, "Jus Prisijunges", Snackbar.LENGTH_LONG).show();

            displayAllMessages();
        }

    }

    private void displayAllMessages() {
        ListView listOfMessages = findViewById(R.id.list_of_messages);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Chat");



        Query query = FirebaseDatabase.getInstance().getReference("Users").child("Chat");

        FirebaseListOptions<ModelChat> options =
                new FirebaseListOptions.Builder<ModelChat>()
                        .setQuery(ref, ModelChat.class)
                        .setLayout(R.layout.list_item_message)
                        .build();
        adapter = new FirebaseListAdapter<ModelChat>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull ModelChat model, int position) {
                TextView mess_user, mess_time, mess_text;
                mess_user = v.findViewById(R.id.message_user);
                mess_time = v.findViewById(R.id.message_time);
                mess_text = v.findViewById(R.id.message_text);

                mess_user.setText(model.getMessageUser());
                mess_text.setText(model.getMessageText());
                mess_time.setText(DateFormat.format("dd-mm-YYYY HH:mm:ss", model.getMessageTime()));


            }
        };
        listOfMessages.setAdapter(adapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}