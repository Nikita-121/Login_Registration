package activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nikitajain.nkj.MainActivity;
import com.nikitajain.nkj.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;
import helper.SessionManager;

public class LoginActivity extends Activity {
    private static final String TAG=RegisterActivity.class.getSimpleName();
    private EditText inputEmail;
    private EditText inputPassword;
    private Button login_btn;
    private Button register_btn;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail= (EditText)findViewById(R.id.email);
        inputPassword= (EditText)findViewById(R.id.password);
        login_btn= (Button)findViewById(R.id.login_btn);
        register_btn= (Button)findViewById(R.id.register_btn);

        pDialog=new ProgressDialog(this);
        pDialog.setCancelable(false);

        db=new SQLiteHandler(getApplicationContext());

        session=new SessionManager(getApplicationContext());

        inputEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputEmail.getText().clear();
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                String email=inputEmail.getText().toString().trim();
                String password=inputPassword.getText().toString().trim();

                if(!email.isEmpty() && !password.isEmpty()){
                    checkLogin(email,password);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please enter the credentials!",Toast.LENGTH_LONG).show();

                }
            }

        });


        register_btn.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    private void checkLogin(final String email, final String password){

        String tag_string_req="req_login";

        pDialog.setMessage("Logging in.....");
        showDialog();

        StringRequest stringRequest=new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean("error");

                    if (!error) {
                        session.setLogin(true);

                        String uid = obj.getString("uid");
                        JSONObject user = obj.getJSONObject("user");
                        String email = user.getString("email");
                        String name = user.getString("name");
                        String created_at = user.getString("created_at");

                        db.addUser(name, email, uid, created_at);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMessage = obj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "JSON error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener(){
            public void onErrorResponse(VolleyError e){
                Log.e(TAG,"Login Error: "+e.getMessage());
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }){
            protected Map<String,String> getParams(){
                Map<String,String> params =new HashMap<String, String>();
                params.put("email",email);
                params.put("password",password);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest,tag_string_req);


    }
    private void showDialog(){
        if(!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog(){
        if(pDialog.isShowing())
            pDialog.dismiss();
    }
}
