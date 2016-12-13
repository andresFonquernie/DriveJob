package es.fonkyprojects.drivejob.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import es.fonkyprojects.drivejob.activity.R;
import es.fonkyprojects.drivejob.model.Ride;

/**
 * Created by andre on 11/12/2016.
 */

public class RideViewHolder extends RecyclerView.ViewHolder {

    public TextView authorView;
    public TextView placeGoingView;
    public TextView placeReturnView;

    public RideViewHolder(View itemView) {
        super(itemView);

        authorView = (TextView) itemView.findViewById(R.id.ride_author);
        placeGoingView = (TextView) itemView.findViewById(R.id.ride_placeGoing);
        placeReturnView = (TextView) itemView.findViewById(R.id.ride_placeReturn);
    }

    public void bindToPost(Ride ride) {
        authorView.setText(ride.author);
        placeGoingView.setText(ride.placeGoing);
        placeReturnView.setText(ride.placeReturn);
    }
}