package es.fonkyprojects.drivejob.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import es.fonkyprojects.drivejob.model.User;
import es.fonkyprojects.drivejob.restMethods.Users.UserPutTask;
import es.fonkyprojects.drivejob.utils.Constants;

public class ProfileEditActivity extends AppCompatActivity {

    private static final String TAG = "MyProfileActivity";
    public static final String EXTRA_USER = "user";

    private ImageView userImage;
    private TextView userName;
    private TextView userSurname;
    private TextView userEmail;
    private TextView userPhone;

    private Button btnEditUser;

    private User user;
    private String userId;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // Get user from intent
        user = (User) getIntent().getSerializableExtra(EXTRA_USER);
        if (user == null) {
            throw new IllegalArgumentException("Must pass EXTRA_USER");
        }

        // Initialize Views
        userName = (TextView) findViewById(R.id.edit_userName);
        userSurname = (TextView) findViewById(R.id.edit_userSurname);
        userEmail = (TextView) findViewById(R.id.edit_userEmail);

        btnEditUser = (Button) findViewById(R.id.btn_editUser);
        btnEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editUser(view);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

        if(user != null) {
            user_id = user.get_id();
            userId = user.getUserId();
            userName.setText(user.getUsername());
            userSurname.setText(user.getSurname());
            userEmail.setText(user.getEmail());
        }
    }

    public void editUser(View view){
        final String txtName = userName.getText().toString();
        final String txtSurname = userSurname.getText().toString();

        if (validate(txtName, txtSurname)) {

            btnEditUser.setEnabled(false);
            Toast.makeText(this, "Put", Toast.LENGTH_LONG).show();

            String putKey = writeEditUser(txtName, txtSurname);

            if (putKey.equals("Update")) {
                Intent intent = new Intent(this, MyProfileActivity.class);
                intent.putExtra(MyProfileActivity.EXTRA_USER_ID, userId);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getBaseContext(), "Error. Try again later", Toast.LENGTH_LONG).show();
                btnEditUser.setEnabled(true);
            }
        }
    }

    private String writeEditUser(String txtName, String txtSurname) {
        String result = "";
        try {
            User putUser = new User(userId, txtName, txtSurname, "");
            UserPutTask upt = new UserPutTask(this);
            upt.setUserPut(putUser);
            result = upt.execute(Constants.BASE_URL + "user/" + user_id).get();
            Log.e(TAG, "RESULT PUT RIDE: " + result);
            return result;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean validate(String txtName, String txtSurname) {
        boolean valid = true;

        if (txtName == null || txtName.length()==0) {
            userName.setError("Not null");
            valid = false;
        } else {
            userName.setError(null);
        }

        if (txtSurname == null || txtSurname.length()==0) {
            userSurname.setError("Not null");
            valid = false;
        } else {
            userSurname.setError(null);
        }

        return valid;
    }
}
