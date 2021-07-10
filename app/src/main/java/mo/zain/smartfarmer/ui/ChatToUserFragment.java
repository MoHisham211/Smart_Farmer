package mo.zain.smartfarmer.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import mo.zain.smartfarmer.R;
import mo.zain.smartfarmer.adpter.ChatAdapter;
import mo.zain.smartfarmer.model.ChatModel;


public class ChatToUserFragment extends Fragment {

    CircleImageView profile,circleImageView;
    TextView Name,status;
    DatabaseReference df;
    String id,hisImage;
    EditText editTextTextPersonName;
    RecyclerView recyclerView;
    List<ChatModel> chatModels=new ArrayList<>();;
    ChatAdapter adapterChat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chat_to_user, container, false);
        id = getArguments().get("MyId").toString();
        recyclerView=view.findViewById(R.id.rv);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        status=view.findViewById(R.id.status);
        recyclerView.setLayoutManager(linearLayoutManager);
        profile=view.findViewById(R.id.profileImage);
        Name=view.findViewById(R.id.profileName);
        editTextTextPersonName=view.findViewById(R.id.editTextTextPersonName);
        circleImageView=view.findViewById(R.id.circleImageView);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(editTextTextPersonName.getText().toString());
                editTextTextPersonName.setText("");
            }
        });
        loadInfo();
        readMessages();
        return view;
    }
    private void sendMessage(final String message) {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

        String timestamp=String.valueOf(System.currentTimeMillis());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
        hashMap.put("receiver",id);
        hashMap.put("message",message);
        hashMap.put("timestamp",timestamp);
        hashMap.put("isSeen",false);

        databaseReference.child("Chats").push().setValue(hashMap);

        String msg=message;

    }

    private void loadInfo()
    {
        df= FirebaseDatabase.getInstance()
                .getReference("User")
                .child(id);
        Query query=df;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> map = (Map) snapshot.getValue();

                Name.setText(map.get("UserName"));
                if (!map.get("imageURL").equals(""))
                    Glide.with(getContext()).load(map.get("imageURL")).into(profile);
                else
                    Glide.with(getContext()).load(R.drawable.ic_profile).into(profile);

                hisImage=map.get("imageURL") ;
                status.setText("onlineStatus");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FancyToast.makeText(getContext(),error.getMessage(),FancyToast.LENGTH_LONG, FancyToast.ERROR,false).show();
            }
        });

    }
    private void readMessages() {
        DatabaseReference dbRef=FirebaseDatabase.getInstance()
                .getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatModels.clear();
                String myId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                for (DataSnapshot ds:snapshot.getChildren()){
                    ChatModel chat=ds.getValue(ChatModel.class);
                    if ((chat.getReceiver()
                            .equals(myId) && chat.getSender().equals(id) )||
                            (chat.getReceiver().equals(id)&& chat.getSender().equals(myId))) {

                        chatModels.add(chat);

                    }
                    adapterChat=new ChatAdapter(getContext(),chatModels,hisImage);
                    adapterChat.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterChat);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkOnlineStatus(String status)
    {
        DatabaseReference dbRef=FirebaseDatabase.getInstance()
                .getReference("Company")
                .child(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("onlineStatus",status);
        dbRef.updateChildren(hashMap);
    }

    @Override
    public void onStart() {
        super.onStart();
        checkOnlineStatus("online");
    }

    @Override
    public void onStop() {
        super.onStop();
        checkOnlineStatus("offline");
    }


}