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

public class RegisterActivity extends Activity {

    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private Button register;
    private Button login;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    private SessionManager session;
    private static final String TAG=RegisterActivity.class.getSimpleName();

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFullName=(EditText)findViewById(R.id.fullname);
        inputEmail=(EditText)findViewById(R.id.email);
        inputPassword=(EditText)findViewById(R.id.password);
        register=(Button)findViewById(R.id.register);
        login=(Button)findViewById(R.id.login);

        pDialog=new ProgressDialog(this);
        pDialog.setCancelable(false);

        session=new SessionManager(getApplicationContext());

        db=new SQLiteHandler(getApplicationContext());
        inputFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputFullName.getText().clear();

            }
        });

        inputEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputEmail.getText().clear();
            }
        });

        inputPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputPassword.getText().clear();
            }
        });

//        if(session.isLoggedIn()){
//            Intent intent=new Intent(RegisterActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=inputEmail.getText().toString().trim();
                String name =inputFullName.getText().toString().trim();
                String password=inputPassword.getText().toString().trim();

                if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
                    registerUser(name, email,password);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please enter your details!",Toast.LENGTH_LONG).show();
                }

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    private void registerUser(final String name, final String email, final String password){
        String tag_string_req="req_register";

        pDialog.setMessage("Registering user..");
        showDialog();

        StringRequest stringRequest=new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean("error");

                    if (!error) {
                        //stored in SQl; storing in SQLite
                        String uid = obj.getString("uid");
                        JSONObject user = obj.getJSONObject("user");
                        String name = user.getString("name");
                        String created_at = user.getString("created_at");
                        String email = user.getString("email");

                        db.addUser(name, email, uid, created_at);
                        Toast.makeText(getApplicationContext(), "User registered successfully!", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        //error in registration
                        String err = obj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
    }){
            protected Map<String,String> getParams(){
               Map<String, String>  params=new HashMap<>();
               params.put("name",name);
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
