package ca.beauvais_la.soundlist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import ca.beauvais_la.soundlist.model.FBUser;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author alacasse (10/1/16)
 */
public class LoginActivity extends Activity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private TextView btnLogin;
    private ProgressDialog progressDialog;

    FBUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        currentUser = PrefUtils.getCurrentUser(this);
//        currentUser = null;
        if (currentUser != null) {
            goToSoundcloudPlaylistActivity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.setReadPermissions("user_actions.music, user_likes");

        btnLogin = (TextView) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();

                loginButton.performClick();

                loginButton.setPressed(true);

                loginButton.invalidate();

                loginButton.registerCallback(callbackManager, mCallBack);

                loginButton.setPressed(false);

                loginButton.invalidate();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void goToSoundcloudPlaylistActivity() {
        Intent intent = new Intent(LoginActivity.this, SoundcloudPlaylistActivity.class);

        Bundle extras = new Bundle();
        FBUser currentUser = PrefUtils.getCurrentUser(this);

        extras.putSerializable("musicLikes", JsonUtil.serialize(currentUser.musicLikes));

        intent.putExtras(extras);
        startActivity(intent);
        finish();
    }

    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            progressDialog.dismiss();

            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {

                            Log.e(Soundlist.TAG, response + "");
                            try {
                                currentUser = JsonUtil.deserializeAs(response.getRawResponse(), FBUser.class);

                                PrefUtils.setCurrentUser(currentUser, LoginActivity.this);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            goToSoundcloudPlaylistActivity();
                        }

                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,music");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            progressDialog.dismiss();
        }

        @Override
        public void onError(FacebookException e) {
            progressDialog.dismiss();
        }
    };

}
