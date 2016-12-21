package es.fonkyprojects.drivejob.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.model.Ride;
import es.fonkyprojects.drivejob.viewholder.RideViewHolder;

public class SearchResultActivity extends Activity {

    public static final String EXTRA_TIME_GOING = "time_going";
    public static final String EXTRA_PLACE_GOING = "place_going";
    public static final String EXTRA_TIME_RETURN = "time_going";
    public static final String EXTRA_PLACE_RETURN = "place_return";

    private DatabaseReference mDatabase;


    private FirebaseRecyclerAdapter<Ride, RideViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecycler = (RecyclerView) findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Ride, RideViewHolder>(Ride.class, R.layout.item_ride,
                RideViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final RideViewHolder viewHolder, final Ride model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch RideDetailActivity
                        Intent intent = new Intent(SearchResultActivity.this, RideDetailActivity.class);
                        intent.putExtra(RideDetailActivity.EXTRA_RIDE_KEY, postKey);
                        startActivity(intent);
                    }
                });

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model);
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    public Query getQuery(DatabaseReference databaseReference){
        String placeGoing = getIntent().getStringExtra(EXTRA_PLACE_GOING);
        Query result = databaseReference.child("rides");
        return result;
    }
}
