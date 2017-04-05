package es.fonkyprojects.drivejob.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.fonkyprojects.drivejob.activity.R;
import es.fonkyprojects.drivejob.model.User;

/**
 * Created by andre on 03/02/2017.
 */

public class UserJoinViewAdapter extends RecyclerView.Adapter<UserJoinViewAdapter.UserJoinHolder> {

    List<User> data = new ArrayList<User>();
    private final UserJoinViewAdapter.OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(User item);
    }

    public UserJoinViewAdapter(List<User> data, UserJoinViewAdapter.OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public UserJoinViewAdapter.UserJoinHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userjoin_holder, parent, false);
        UserJoinViewAdapter.UserJoinHolder rh = new UserJoinViewAdapter.UserJoinHolder(view);
        return rh;
    }

    @Override
    public void onBindViewHolder(UserJoinViewAdapter.UserJoinHolder holder, final int position) {

        User user = data.get(position);
        holder.txtUsername.setText(user.getUsername() + " " + user.getSurname());
        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.bind(data.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class UserJoinHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView txtUsername;
        ImageButton btnCancel;

        public UserJoinHolder(View view) {
            super(view);
            txtUsername = (TextView) view.findViewById(R.id.joinUsername);
            btnCancel = (ImageButton) view.findViewById(R.id.btnRefuse);
        }

        public void bind(final User user, final UserJoinViewAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(user);
                }
            });
        }
    }
}
