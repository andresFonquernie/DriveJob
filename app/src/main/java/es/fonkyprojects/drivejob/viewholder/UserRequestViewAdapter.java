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
 * Created by andre on 03/04/2017.
 */

public class UserRequestViewAdapter extends RecyclerView.Adapter<UserRequestViewAdapter.UserRequestHolder> {

    private List<User> data = new ArrayList<>();
    private final UserRequestViewAdapter.OnItemClickListener listener;
    private final UserRequestViewAdapter.OnAcceptClickListener listenerAccept;
    private final UserRequestViewAdapter.OnRefuseClickListener listenerRefuse;

    public interface OnItemClickListener{
        void onItemClick(User item);
    }

    public interface OnAcceptClickListener {
        void onAcceptClick(User item);
    }

    public interface OnRefuseClickListener {
        void onRefuseClick(User item);
    }

    public UserRequestViewAdapter(List<User> data, UserRequestViewAdapter.OnItemClickListener listener,
                                  UserRequestViewAdapter.OnAcceptClickListener listenerAccept,
                                  UserRequestViewAdapter.OnRefuseClickListener listenerRefuse) {
        this.data = data;
        this.listener = listener;
        this.listenerAccept = listenerAccept;
        this.listenerRefuse = listenerRefuse;
    }

    @Override
    public UserRequestViewAdapter.UserRequestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userrequest_holder, parent, false);
        return new UserRequestViewAdapter.UserRequestHolder(view);
    }

    @Override
    public void onBindViewHolder(UserRequestViewAdapter.UserRequestHolder holder, final int position) {

        User user = data.get(position);
        holder.txtUsername.setText(user.getUsername() + " " + user.getSurname());
        holder.bind(data.get(position), listener);
        holder.bindToAccept(data.get(position), listenerAccept);
        holder.bindToRefuse(data.get(position), listenerRefuse);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class UserRequestHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView txtUsername;
        ImageButton btnAccept;
        ImageButton btnRefuse;

        UserRequestHolder(View view) {
            super(view);
            txtUsername = (TextView) view.findViewById(R.id.requestUsername);
            btnAccept = (ImageButton) view.findViewById(R.id.btnAccept);
            btnRefuse = (ImageButton) view.findViewById(R.id.btnRefuse);
        }

        public void bind(final User user, final UserRequestViewAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(user);
                }
            });
        }

        void bindToAccept(final User user, final UserRequestViewAdapter.OnAcceptClickListener listener) {
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAcceptClick(user);
                }
            });
        }

        void bindToRefuse(final User user, final UserRequestViewAdapter.OnRefuseClickListener listener) {
            btnRefuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRefuseClick(user);
                }
            });
        }
    }
}