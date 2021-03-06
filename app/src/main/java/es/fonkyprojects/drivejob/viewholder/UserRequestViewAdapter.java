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

public class UserRequestViewAdapter extends RecyclerView.Adapter<UserRequestViewAdapter.UserRequestHolder> {

    private List<UsernameDays> data = new ArrayList<>();
    private boolean visible;
    private boolean showUser;
    private final UserRequestViewAdapter.OnItemClickListener listener;
    private final UserRequestViewAdapter.OnAcceptClickListener listenerAccept;
    private final UserRequestViewAdapter.OnRefuseClickListener listenerRefuse;
    private String[] daysOfWeek;

    public interface OnItemClickListener{
        void onItemClick(UsernameDays item);
    }

    public interface OnAcceptClickListener {
        void onAcceptClick(UsernameDays item);
    }

    public interface OnRefuseClickListener {
        void onRefuseClick(UsernameDays item);
    }

    public UserRequestViewAdapter(List<UsernameDays> data, UserRequestViewAdapter.OnItemClickListener listener, UserRequestViewAdapter.OnAcceptClickListener listenerAccept,
                                  UserRequestViewAdapter.OnRefuseClickListener listenerRefuse, boolean visible, boolean showUser) {
        this.data = data;
        this.listener = listener;
        this.listenerAccept = listenerAccept;
        this.listenerRefuse = listenerRefuse;
        daysOfWeek = MyApp.getAppContext().getResources().getStringArray(R.array.shortdaysofweek);
        this.visible = visible;
        this.showUser = showUser;

    }

    @Override
    public UserRequestViewAdapter.UserRequestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_userrequest, parent, false);
        return new UserRequestViewAdapter.UserRequestHolder(view);
    }

    @Override
    public void onBindViewHolder(UserRequestViewAdapter.UserRequestHolder holder, final int position) {

        UsernameDays user = data.get(position);
        List<Integer> listUserDays = user.getDays();
        String shortDays = "(";
        for(int i = 0; i<listUserDays.size(); i++){
            shortDays = shortDays + daysOfWeek[listUserDays.get(i)] + ", ";
        }
        shortDays = shortDays.substring(0, shortDays.length()-2) + ")";

        holder.setShowUser(showUser);
        holder.txtUsername.setText(user.getUsername() + " " + shortDays);
        holder.bind(data.get(position), listener);
        holder.bindToAccept(data.get(position), listenerAccept);
        holder.bindToRefuse(data.get(position), listenerRefuse);

        if(visible){
            holder.setButtonVisibility();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class UserRequestHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.authorImage) ImageView img;
        @BindView (R.id.requestUsername) TextView txtUsername;
        @BindView(R.id.btnAccept) ImageButton btnAccept;
        @BindView(R.id.btnRefuse) ImageButton btnRefuse;

        private boolean showUser;

        UserRequestHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            showUser = false;
        }

        public void setButtonVisibility(){
            btnAccept.setVisibility(View.VISIBLE);
            btnRefuse.setVisibility(View.VISIBLE);
        }

        public void bind(final UsernameDays user, final UserRequestViewAdapter.OnItemClickListener listener) {
            if(showUser)
                itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(user);
                }
            });
        }

        void bindToAccept(final UsernameDays user, final UserRequestViewAdapter.OnAcceptClickListener listener) {
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAcceptClick(user);
                }
            });
        }

        void bindToRefuse(final UsernameDays user, final UserRequestViewAdapter.OnRefuseClickListener listener) {
            btnRefuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRefuseClick(user);
                }
            });
        }

        public void setShowUser(boolean showUser){
            this.showUser = showUser;
        }
    }
}