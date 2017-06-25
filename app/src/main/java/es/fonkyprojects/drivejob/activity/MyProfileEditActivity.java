package es.fonkyprojects.drivejob.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.model.User;
import es.fonkyprojects.drivejob.restMethods.Users.UserPutTask;
import es.fonkyprojects.drivejob.utils.Constants;

public class MyProfileEditActivity extends AppCompatActivity {

    private static final String TAG = "MyProfileActivity";
    public static final String EXTRA_USER = "user";

    @BindView(R.id.edit_userImage) ImageView userImage;
    @BindView(R.id.edit_userName) TextView userName;
    @BindView(R.id.edit_userSurname) TextView userSurname;
    @BindView(R.id.edit_userEmail) TextView userEmail;
    @BindView(R.id.edit_userPhone) TextView userPhone;
    @BindView(R.id.btn_editUser) TextView btnEditUser;

    private User user;
    private String userId;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        ButterKnife.bind(this);

        // Get user from intent
        user = (User) getIntent().getSerializableExtra(EXTRA_USER);
        if (user == null) {
            throw new IllegalArgumentException("Must pass EXTRA_USER");
        }
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
            User putUser = new User(userId, txtName, txtSurname, "", user.getEmailVerify());
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
