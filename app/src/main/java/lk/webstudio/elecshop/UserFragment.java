package lk.webstudio.elecshop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lk.webstudio.elecshop.model.User;


public class UserFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_user, container, false);

        try {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore
                    .collection("user")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                try {
                                    QuerySnapshot querySnapshot = task.getResult();

                                    ArrayList<User> userList = new ArrayList<>();
                                    for (QueryDocumentSnapshot qs : querySnapshot) {

                                        userList.add(
                                                new User(
                                                        qs.getString("firstName"),
                                                        qs.getString("lastName"),
                                                        qs.getString("email"),
                                                        qs.getString("mobile"),
                                                        qs.getString("password"),
                                                        null,
                                                        0,
                                                        0,
                                                        Integer.parseInt(String.valueOf(qs.getLong("status"))),
                                                        qs.getId()


                                                )
                                        );
                                    }
                                    RecyclerView recyclerView1 = rootView.findViewById(R.id.recyclerView8);
                                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                                    recyclerView1.setLayoutManager(layoutManager);

                                    UserAdapter userAdapter = new UserAdapter(userList);
                                    recyclerView1.setAdapter(userAdapter);

                                } catch (Exception e) {
                                    Log.i("ElecLog", String.valueOf(e));
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            Log.i("ElecLog", String.valueOf(e));
        }


        return rootView;
    }
}


class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final ArrayList<User> userArrayList;

    public UserAdapter(ArrayList<User> userArrayList) {
        this.userArrayList = userArrayList;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public ImageView userImage;

        public SwitchCompat userStatus;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.textView50);
            userImage = itemView.findViewById(R.id.imageView12);
            userStatus = itemView.findViewById(R.id.switch1);

        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_fragment_list, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = userArrayList.get(position);
        holder.userName.setText(user.getFirstName() + " " + user.getLastName());



        if (user.getStatus() == 1) {
            holder.userStatus.setChecked(true);
        } else if (user.getStatus() == 2) {
            holder.userStatus.setChecked(true);
        } else if (user.getStatus() == 3) {
            holder.userStatus.setChecked(false);
        }


        Glide.with(holder.userImage.getContext())
                .load(R.drawable.avatar)
                .placeholder(R.drawable.product2)
                .error(R.drawable.product2)
                .into(holder.userImage);

        holder.userStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                   FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                   HashMap<String,Object> userlist = new HashMap<>();
                   userlist.put("status",2);
                   firebaseFirestore
                           .collection("user")
                           .document(user.getUserId())
                           .update(userlist)
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful()){
                                       Log.i("ElecLog", "enabled");
                                   }
                               }
                           })
                   ;
                } else {
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                    HashMap<String,Object> userlist = new HashMap<>();
                    userlist.put("status",3);
                    firebaseFirestore
                            .collection("user")
                            .document(user.getUserId())
                            .update(userlist)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.i("ElecLog", "disabled");
                                    }
                                }
                            })
                    ;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }
}
