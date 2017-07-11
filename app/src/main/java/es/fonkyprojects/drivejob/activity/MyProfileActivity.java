package es.fonkyprojects.drivejob.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.model.User;
import es.fonkyprojects.drivejob.restMethods.GetTask;
import es.fonkyprojects.drivejob.restMethods.Users.UserPutTask;
import es.fonkyprojects.drivejob.utils.Constants;
import es.fonkyprojects.drivejob.utils.FirebaseUser;

public class MyProfileActivity extends AppCompatActivity {

    private static final String TAG = "MyProfileActivity";
    public static final String EXTRA_USER_ID = "userId";

    private String userId;
    @BindView(R.id.userImage) ImageView userImage;
    @BindView(R.id.nameText) TextView userName;
    @BindView(R.id.emailText) TextView userEmail;
    @BindView(R.id.phoneText) TextView userPhone;
    @BindView(R.id.checkImage) ImageView verifyEmail;
    @BindView(R.id.btnCheck) Button btnCheck;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;

    private User user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Get post key from intent
        userId = (String) getIntent().getSerializableExtra(EXTRA_USER_ID);
        if (userId == null) {
            userId = FirebaseUser.getUid();
        }
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            user = getUser(userId);
            toolbar.setTitle(user.getUsername() + " " + user.getSurname());
            userName.setText(user.getUsername() + " " + user.getSurname());
            userEmail.setText(user.getEmail());
            collapsingToolbar.setTitle(user.getUsername() + " " + user.getSurname());
            userImage.setImageResource(R.drawable.ln_logo);

            if (!user.getEmailVerify()) {
                verifyEmail.setImageResource(R.drawable.ic_cancel_white_24dp);
                btnCheck.setVisibility(View.VISIBLE);
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void checkEmail(View view) {
        final com.google.firebase.auth.FirebaseUser firebaseUser = mAuth.getCurrentUser();
        firebaseUser.reload();
        boolean verified = firebaseUser.isEmailVerified();
        if (verified) {
            try {
                user.setEmailVerify(true);
                UserPutTask upt = new UserPutTask(this);
                upt.setUserPut(user);
                String result = upt.execute(Constants.BASE_URL + "user/" + user.get_id()).get();
                btnCheck.setVisibility(View.INVISIBLE);
                verifyEmail.setImageResource(R.drawable.ic_check_circle_white_24dp);

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private User getUser(String userId) throws ExecutionException, InterruptedException {
        String result;
        GetTask ugt = new GetTask(this);
        result = ugt.execute(Constants.BASE_URL + "user/?userId=" + userId).get();
        Log.e(TAG, "RESULT GET USER: " + result);
        Type type = new TypeToken<List<User>>(){}.getType();
        List<User> inpList = new Gson().fromJson(result, type);
        user = inpList.get(0);

        return user;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(userId.equals(FirebaseUser.getUid())) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.mnu_profile, menu);
            return true;
        } else {
            getMenuInflater().inflate(R.menu.mnu_blank, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_verify:
                final com.google.firebase.auth.FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                    firebaseUser.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MyProfileActivity.this,
                                                "Verification email sent to " + user.getEmail(), Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(MyProfileActivity.this,
                                                "Failed to send verification email.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                break;
            case R.id.mnu_edit:
                Intent intent = new Intent(MyProfileActivity.this, MyProfileEditActivity.class);
                intent.putExtra(MyProfileEditActivity.EXTRA_USER, user);
                startActivity(intent);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
