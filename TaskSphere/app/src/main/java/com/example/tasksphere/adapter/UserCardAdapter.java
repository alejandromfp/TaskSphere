package com.example.tasksphere.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tasksphere.ProfilePage;
import com.example.tasksphere.R;
import com.example.tasksphere.modelo.entidad.User;

import java.util.List;

public class UserCardAdapter extends RecyclerView.Adapter<UserCardAdapter.UserViewHolder> {

    private Context context;
    private List<User> employeeList;


    public UserCardAdapter(Context context, List<User> employeeList) {
        this.context = context;
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User employee = employeeList.get(position);
        holder.bind(employee);
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView profileImage;
        private TextView nameTextView, roleTextView , goProfileButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImg);
            nameTextView = itemView.findViewById(R.id.username);
            roleTextView = itemView.findViewById(R.id.userRole);
            goProfileButton = itemView.findViewById(R.id.button);

        }

        public void bind(User user) {

            Glide.with(context)
                    .load(user.getProfileImage())
                    .centerCrop()
                    .placeholder(R.drawable.defaultavatar)
                    .into(profileImage);

            nameTextView.setText(user.getNombre() + " " + user.getApellidos());
            roleTextView.setText(user.getRol());

            goProfileButton.setOnClickListener(v -> {

                Intent intent = new Intent(context, ProfilePage.class);
                intent.putExtra("userId", user.getUserId());
                context.startActivity(intent);
            });
        }
    }
}
