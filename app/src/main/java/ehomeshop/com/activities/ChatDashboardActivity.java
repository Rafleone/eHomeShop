package ehomeshop.com.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ehomeshop.com.ChatSellersFragment;
import ehomeshop.com.R;
import ehomeshop.com.fragments.ChatListFragment;
import ehomeshop.com.fragments.ChatUsersFragment;

public class ChatDashboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dashboard);

        actionBar = getSupportActionBar();
        //actionBar.setTitle("Chat With Seller");

        firebaseAuth = FirebaseAuth.getInstance();

        //bottom nav
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

//        NavigationView nav = (NavigationView) findViewById(R.id.navigation);
//        Menu menu = navigationView.getMenu();
//        MenuItem nav_users = menu.findItem(R.id.nav_users);


        //pagrindinis page kuriame atsidaro kaip paspuadi per seller home page i chato iconele per Dashboarda
        //users fragment transaction
       // actionBar.setTitle("Sellers"); //change actionbar title
        getAccountType();
        ChatUsersFragment fragment = new ChatUsersFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, fragment, "");
        ft.commit();
    }

    private void getAccountType() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String accountType = "" + ds.child("accountType").getValue();
                            if (accountType.equals("Seller")) {

                                //user is seller and setting actionBar title
                                actionBar.setTitle("Users"); //change actionbar title

                                //Bottom Nav View Setting Title from chat_menu_nav.xml item
                                BottomNavigationView nav = (BottomNavigationView) findViewById(R.id.navigation);
                                Menu menu = nav.getMenu();
                                MenuItem nav_users = menu.findItem(R.id.nav_users);
                                nav_users.setTitle("Users");

                            } else {

                                //user is buyer and setting actionBar title
                                actionBar.setTitle("Sellers"); //change actionbar title

                                //Bottom Nav View Setting Title from chat_menu_nav.xml item
                                BottomNavigationView nav = (BottomNavigationView) findViewById(R.id.navigation);
                                Menu menu = nav.getMenu();
                                MenuItem nav_users = menu.findItem(R.id.nav_users);
                                nav_users.setTitle("Sellers");

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    //handle item clicks
                    switch (item.getItemId()){
                        case R.id.nav_users:
                             //users fragment transaction
                            getAccountType();
                            ChatUsersFragment fragment = new ChatUsersFragment();
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.content, fragment, "");
                            ft.commit();
                            return true;
                        case R.id.nav_chat:
                            actionBar.setTitle("Chats"); //change actionbar title
                            ChatListFragment fragment2 = new ChatListFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content, fragment2, "");
                            ft2.commit();
                            return true;
                    }

                    return false;
                }
            };
}