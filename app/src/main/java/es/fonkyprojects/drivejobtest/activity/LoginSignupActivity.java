package es.fonkyprojects.drivejob.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.fonkyprojects.drivejob.model.User;
import es.fonkyprojects.drivejob.restMethods.Users.UserPostTask;
import es.fonkyprojects.drivejob.utils.Constants;

public class LoginSignupActivity extends Activity {
    private static final String TAG = "LoginSignupActivity";

    private FirebaseAuth mAuth;

    @BindView(R.id.input_name) EditText nameText;
    @BindView(R.id.input_surname) EditText surnameText;
    @BindView(R.id.input_email) EditText emailText;
    @BindView(R.id.input_password) EditText passwordText;
    @BindView(R.id.input_reEnterPassword) EditText reEnterPasswordText;
    @BindView(R.id.btn_signup) Button signupButton;
    @BindView(R.id.link_login) TextView loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);
        ButterKnife.bind(this);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
    }

    public void signup(View view) {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginSignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.creating_account));
        progressDialog.show();

        //Get data
        final String name = nameText.getText().toString();
        final String surname = surnameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        // Auth user and email
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            onSignupSuccess(task.getResult().getUser(), name, surname);
                        } else {
                            Toast.makeText(LoginSignupActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void login(View view) {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }


    public void onSignupSuccess(FirebaseUser user, String name, String surname) {

        // Write new user
        writeNewUser(user.getUid(), name, surname, user.getEmail());

        // Go to MenuActivity
        startActivity(new Intent(LoginSignupActivity.this, MenuActivity.class));
        finish();
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name, String surname, String email) {
        User user = new User(userId, name, surname, email);

        UserPostTask upt = new UserPostTask(this);
        upt.setUserPost(user);
        try {
            String result = upt.execute(Constants.BASE_URL + "user").get();
            Log.e(TAG, "RESULT: " + result);
            User u = new Gson().fromJson(result, User.class);
            Log.i(TAG, u.getUserId());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    // [END basic_write]

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Error in form", Toast.LENGTH_LONG).show();
        signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();
        String surname = surnameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String reEnterPassword = reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("At least 3 characters");
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (surname.isEmpty() || surname.length() < 3) {
            surnameText.setError("At least 3 characters");
            valid = false;
        } else {
            surnameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("Between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            reEnterPasswordText.setError("Password do not match");
            valid = false;
        } else {
            reEnterPasswordText.setError(null);
        }

        return valid;
    }
}