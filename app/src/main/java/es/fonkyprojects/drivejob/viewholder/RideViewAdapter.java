package es.fonkyprojects.drivejob.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.fonkyprojects.drivejob.activity.R;
import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.utils.MyApp;

public class RideViewAdapter extends RecyclerView.Adapter<RideViewAdapter.RideHolder> {

    private List<Ride> data = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(Ride item);
    }

    public RideViewAdapter(List<Ride> data, OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public RideHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listride_holder, parent, false);
        return new RideHolder(view);
    }

    @Override
    public void onBindViewHolder(RideHolder holder, int position) {

        Ride ride = data.get(position);
        holder.txtUsername.setText(ride.getAuthor());
        holder.txtPlaceGoing.setText(MyApp.getAppContext().getResources().getString(R.string.fromDetail, ride.getPlaceGoing()));
        holder.txtPlaceReturn.setText(MyApp.getAppContext().getResources().getString(R.string.toDetail, ride.getPlaceReturn()));
        String[] timeGoingSplit = ride.getTimeGoing().split(":");
        holder. txtTimeGoing.setText(timeGoingSplit[0] + ":" + timeGoingSplit[1]);
        String[] timeReturnSplit = ride.getTimeReturn().split(":");
        holder.txtTimeReturn.setText(timeReturnSplit[0] + ":" + timeReturnSplit[1]);

        holder.bind(data.get(position), listener);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    static class RideHolder extends RecyclerView.ViewHolder {
        //ImageView img;
        TextView txtUsername;
        TextView txtPlaceGoing;
        TextView txtPlaceReturn;
        TextView txtTimeGoing;
        TextView txtTimeReturn;

        RideHolder(View view) {
            super(view);
            txtUsername = (TextView) view.findViewById(R.id.username);
            txtPlaceGoing = (TextView) view.findViewById(R.id.placeFrom);
            txtPlaceReturn = (TextView) view.findViewById(R.id.placeTo);
            txtTimeGoing = (TextView) view.findViewById(R.id.timeFrom);
            txtTimeReturn = (TextView) view.findViewById(R.id.timeTo);
        }

        public void bind(final Ride ride, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(ride);
                }
            });
        }
    }
}