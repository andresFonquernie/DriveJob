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

import butterknife.BindView;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.activity.R;
import es.fonkyprojects.drivejob.model.local.UsernameDays;
import es.fonkyprojects.drivejob.utils.MyApp;

public class UserJoinViewAdapter extends RecyclerView.Adapter<UserJoinViewAdapter.UserJoinHolder> {

    private List<UsernameDays> data = new ArrayList<>();
    private final UserJoinViewAdapter.OnItemClickListener listener;
    private final UserJoinViewAdapter.OnRefuseClickListener listenerRefuse;
    private String[] daysOfWeek;

    public interface OnItemClickListener{
        void onItemClick(UsernameDays item);
    }

    public interface OnRefuseClickListener {
        void onRefuseClick(UsernameDays item);
    }

    public UserJoinViewAdapter(List<UsernameDays> data, UserJoinViewAdapter.OnItemClickListener listener, UserJoinViewAdapter.OnRefuseClickListener listenerRefuse) {
        this.data = data;
        this.listener = listener;
        this.listenerRefuse = listenerRefuse;
        daysOfWeek = MyApp.getAppContext().getResources().getStringArray(R.array.shortdaysofweek);
    }

    @Override
    public UserJoinViewAdapter.UserJoinHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_userjoin, parent, false);
        return new UserJoinViewAdapter.UserJoinHolder(view);
    }

    @Override
    public void onBindViewHolder(UserJoinViewAdapter.UserJoinHolder holder, final int position) {
        UsernameDays user = data.get(position);
        List<Integer> listUserDays = user.getDays();
        String shortDays = "(";
        for(int i = 0; i<listUserDays.size(); i++){
            shortDays = shortDays + daysOfWeek[listUserDays.get(i)] + ", ";
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
        @BindView(R.id.authorImage) ImageView img;
        @BindView(R.id.joinUsername) TextView txtUsername;
        @BindView(R.id.btnRefuse) ImageButton btnRefuse;

        UserJoinHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(final UsernameDays user, final UserJoinViewAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(user);
                }
            });
        }

        void bindToRefuse(final UsernameDays user, final UserJoinViewAdapter.OnRefuseClickListener listener) {
            btnRefuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRefuseClick(user);
                }
            });
        }
    }
}
