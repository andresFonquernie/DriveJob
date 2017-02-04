package es.fonkyprojects.drivejob.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.fonkyprojects.drivejob.activity.R;
import es.fonkyprojects.drivejob.model.User;

/**
 * Created by andre on 03/02/2017.
 */

public class UserViewAdapter extends RecyclerView.Adapter<UserViewAdapter.UserHolder> {

    List<User> data = new ArrayList<User>();
    private final UserViewAdapter.OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(User item);
    }

    public UserViewAdapter(List<User> data, UserViewAdapter.OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public UserViewAdapter.UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usersjoin_holder, parent, false);
        UserViewAdapter.UserHolder rh = new UserViewAdapter.UserHolder(view);
        return rh;
    }

    @Override
    public void onBindViewHolder(UserViewAdapter.UserHolder holder, int position) {

        User user = data.get(position);
        holder.txtUsername.setText(user.getUsername() + " " + user.getSurname());

        holder.bind(data.get(position), listener);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class UserHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView txtUsername;

        public UserHolder(View view) {
            super(view);
            txtUsername = (TextView) view.findViewById(R.id.joinUsername);
        }

        public void bind(final User user, final UserViewAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(user);
                }
            });
        }
    }
}
