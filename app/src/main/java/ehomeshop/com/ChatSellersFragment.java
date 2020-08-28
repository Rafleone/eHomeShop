package ehomeshop.com;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ehomeshop.com.adapters.AdapterUsersChat;
import ehomeshop.com.models.ModelUsersChat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatSellersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatSellersFragment extends Fragment {

    RecyclerView recyclerView;
    AdapterUsersChat adapterUsersChat;
    List<ModelUsersChat> usersList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatSellersFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatSellersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatSellersFragment newInstance(String param1, String param2) {
        ChatSellersFragment fragment = new ChatSellersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_sellers, container, false);

        //init recyclerview
        recyclerView = view.findViewById(R.id.users_chat_recyclerView);

        //set its properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //init user list
        usersList = new ArrayList<>();

        //getAll users
        getAllUsers();

        return view;
    }

    private void getAllUsers() {
        //get current user
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = firebaseUser.getUid();

        //get path of database named "Users" containing users info
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        //get and show All Sellers
        Query getSellers = ref.orderByChild("accountType").equalTo("Seller");

        //get and show All Users AccountType Users , show only Users
        Query getUsers = ref.orderByChild("accountType").equalTo("User");


        getSellers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelUsersChat modelUsersChat = ds.getValue(ModelUsersChat.class);

                    //get all users except currently signed in
                    if (modelUsersChat.getUid() != null && !modelUsersChat.getUid().equals(firebaseUser.getUid())) {
                        usersList.add(modelUsersChat);
                    }

                    //adapter
                    adapterUsersChat = new AdapterUsersChat(getActivity(), usersList);

                    //set adapter
                    recyclerView.setAdapter(adapterUsersChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}