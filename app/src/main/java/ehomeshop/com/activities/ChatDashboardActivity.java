package ehomeshop.com.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import ehomeshop.com.R;

public class ChatDashboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dashboard);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Chat");

        firebaseAuth = FirebaseAuth.getInstance();

        //bottom nav
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);


        //pagrindinis page kuriame atsidaro kaip paspuadi per seller home page i chato iconele per Dashboarda
        //users fragment transaction
        actionBar.setTitle("Sellers"); //change actionbar title
        ChatUsersFragment fragment = new ChatUsersFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, fragment, "");
        ft.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    //handle item clicks
                    switch (item.getItemId()){
                        case R.id.nav_users:
                             //users fragment transaction
                            actionBar.setTitle("Sellers"); //change actionbar title
                            ChatUsersFragment fragment = new ChatUsersFragment();
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.content, fragment, "");
                            ft.commit();
                            return true;
                        case R.id.nav_chat:
                            actionBar.setTitle("Chats"); //change actionbar title
                            ChatListFragment fragment1 = new ChatListFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content, fragment1, "");
                            ft1.commit();
                            return true;
                    }

                    return false;
                }
            };
}