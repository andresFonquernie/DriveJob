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

public class UserJoinViewAdapter extends RecyclerView.Adapter<UserJoinViewAdapter.UserJoinHolder> {

    private List<UserDays> data = new ArrayList<>();
    private final UserJoinViewAdapter.OnItemClickListener listener;
    private final UserJoinViewAdapter.OnRefuseClickListener listenerRefuse;
    private String[] daysOfWeek;

    public interface OnItemClickListener{
        void onItemClick(UserDays item);
    }

    public interface OnRefuseClickListener {
        void onRefuseClick(UserDays item);
    }

    public UserJoinViewAdapter(List<UserDays> data, UserJoinViewAdapter.OnItemClickListener listener, UserJoinViewAdapter.OnRefuseClickListener listenerRefuse) {
        this.data = data;
        this.listener = listener;
        this.listenerRefuse = listenerRefuse;
        daysOfWeek = MyApp.getAppContext().getResources().getStringArray(R.array.shortdaysofweek);
    }

    @Override
    public UserJoinViewAdapter.UserJoinHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userjoin_holder, parent, false);
        return new UserJoinViewAdapter.UserJoinHolder(view);
    }

    @Override
    public void onBindViewHolder(UserJoinViewAdapter.UserJoinHolder holder, final int position) {
        UserDays user = data.get(position);
        String[] userDays = user.getDays().split(",");
        String shortDays = "(";
        for(int i = 0; i<userDays.length; i++){
            shortDays = shortDays + daysOfWeek[Integer.parseInt(userDays[i])] + ", ";
        }
        shortDays = shortDays.substring(0, shortDays.length()-2) + ")";

        holder.txtUsername.setText(user.getUsername() + " " + shortDays);
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

        UserJoinHolder(View view) {
            super(view);
            txtUsername = (TextView) view.findViewById(R.id.joinUsername);
            btnRefuse = (ImageButton) view.findViewById(R.id.btnRefuse);
        }

        public void bind(final UserDays user, final UserJoinViewAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(user);
                }
            });
        }

        void bindToRefuse(final UserDays user, final UserJoinViewAdapter.OnRefuseClickListener listener) {
            btnRefuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRefuseClick(user);
                }
            });
        }
    }
}
