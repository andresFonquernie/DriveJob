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
import es.fonkyprojects.drivejob.model.UserDays;
import es.fonkyprojects.drivejob.utils.MyApp;

public class UserRequestViewAdapter extends RecyclerView.Adapter<UserRequestViewAdapter.UserRequestHolder> {

    private List<UserDays> data = new ArrayList<>();
    private final UserRequestViewAdapter.OnItemClickListener listener;
    private final UserRequestViewAdapter.OnAcceptClickListener listenerAccept;
    private final UserRequestViewAdapter.OnRefuseClickListener listenerRefuse;
    private String[] daysOfWeek;

    public interface OnItemClickListener{
        void onItemClick(UserDays item);
    }

    public interface OnAcceptClickListener {
        void onAcceptClick(UserDays item);
    }

    public interface OnRefuseClickListener {
        void onRefuseClick(UserDays item);
    }

    public UserRequestViewAdapter(List<UserDays> data, UserRequestViewAdapter.OnItemClickListener listener, UserRequestViewAdapter.OnAcceptClickListener listenerAccept,
                                  UserRequestViewAdapter.OnRefuseClickListener listenerRefuse) {
        this.data = data;
        this.listener = listener;
        this.listenerAccept = listenerAccept;
        this.listenerRefuse = listenerRefuse;
        daysOfWeek = MyApp.getAppContext().getResources().getStringArray(R.array.shortdaysofweek);

    }

    @Override
    public UserRequestViewAdapter.UserRequestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userrequest_holder, parent, false);
        return new UserRequestViewAdapter.UserRequestHolder(view);
    }

    @Override
    public void onBindViewHolder(UserRequestViewAdapter.UserRequestHolder holder, final int position) {

        UserDays user = data.get(position);
        String[] userDays = user.getDays().split(",");
        String shortDays = "(";
        for(int i = 0; i<userDays.length; i++){
            shortDays = shortDays + daysOfWeek[Integer.parseInt(userDays[i])] + ", ";
        }
        shortDays = shortDays.substring(0, shortDays.length()-2) + ")";

        holder.txtUsername.setText(user.getUsername() + " " + shortDays);
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

        public void bind(final UserDays user, final UserRequestViewAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(user);
                }
            });
        }

        void bindToAccept(final UserDays user, final UserRequestViewAdapter.OnAcceptClickListener listener) {
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAcceptClick(user);
                }
            });
        }

        void bindToRefuse(final UserDays user, final UserRequestViewAdapter.OnRefuseClickListener listener) {
            btnRefuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRefuseClick(user);
                }
            });
        }
    }
}