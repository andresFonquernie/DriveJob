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

    private List<User> data = new ArrayList<User>();
    private final UserJoinViewAdapter.OnItemClickListener listener;
    private final UserJoinViewAdapter.OnRefuseClickListener listenerRefuse;

    public interface OnItemClickListener{
        void onItemClick(User item);
    }

    public interface OnRefuseClickListener {
        void onRefuseClick(User item);
    }

    public UserJoinViewAdapter(List<User> data, UserJoinViewAdapter.OnItemClickListener listener, UserJoinViewAdapter.OnRefuseClickListener listenerRefuse) {
        this.data = data;
        this.listener = listener;
        this.listenerRefuse = listenerRefuse;
    }

    @Override
    public UserJoinViewAdapter.UserJoinHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userjoin_holder, parent, false);
        return new UserJoinViewAdapter.UserJoinHolder(view);
    }

    @Override
    public void onBindViewHolder(UserJoinViewAdapter.UserJoinHolder holder, final int position) {

        User user = data.get(position);
        holder.txtUsername.setText(user.getUsername() + " " + user.getSurname());
        holder.bind(data.get(position), listener);
        holder.bindToRefuse(data.get(position), listenerRefuse);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class UserJoinHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView txtUsername;
        ImageButton btnRefuse;

        public UserJoinHolder(View view) {
            super(view);
            txtUsername = (TextView) view.findViewById(R.id.joinUsername);
            btnRefuse = (ImageButton) view.findViewById(R.id.btnRefuse);
        }

        public void bind(final User user, final UserJoinViewAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(user);
                }
            });
        }

        public void bindToRefuse(final User user, final UserJoinViewAdapter.OnRefuseClickListener listener) {
            btnRefuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRefuseClick(user);
                }
            });
        }
    }
}
