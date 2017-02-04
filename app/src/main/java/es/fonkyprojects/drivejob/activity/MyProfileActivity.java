package es.fonkyprojects.drivejob.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;

import es.fonkyprojects.drivejob.model.User;
import es.fonkyprojects.drivejob.restMethods.Users.UserGetTask;
import es.fonkyprojects.drivejob.utils.Constants;
import es.fonkyprojects.drivejob.utils.FirebaseUser;

public class MyProfileActivity extends Activity {

    private static final String TAG = "MyProfileActivity";
    public static final String EXTRA_USER_ID = "userId";

    private CollapsingToolbarLayout userToolbar;
    private Toolbar toolbar;

    private String userId;
    private ImageView userImage;
    private TextView userName;
    private TextView userEmail;
    private TextView userPhone;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);

        // Get post key from intent
        userId = (String) getIntent().getSerializableExtra(EXTRA_USER_ID);
        if (userId == null) {
            userId = FirebaseUser.getUid();
        }

        // Initialize Views
        userImage = (ImageView) findViewById(R.id.userImage);
        userName = (TextView) findViewById(R.id.nameText);
        userEmail = (TextView) findViewById(R.id.emailText);
        userPhone = (TextView) findViewById(R.id.phoneText);
        userToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            user = getUser(userId);
            toolbar.setTitle(user.getUsername() + " " + user.getSurname());
            userName.setText(user.getUsername() + " " + user.getSurname());
            userEmail.setText(user.getEmail());
            userToolbar.setTitle(user.getUsername() + " " +  user.getSurname());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private User getUser(String userId) throws ExecutionException, InterruptedException {
        String result;
        UserGetTask ugt = new UserGetTask(this);
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
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_edit:
                Intent intent = new Intent(MyProfileActivity.this, ProfileEditActivity.class);
                intent.putExtra(ProfileEditActivity.EXTRA_USER, user);
                startActivity(intent);
                break;
            case R.id.mnu_settings:
                Intent itemSettings = new Intent(this, SettingsActivity.class);
                startActivity(itemSettings);
                break;
            case R.id.mnu_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
