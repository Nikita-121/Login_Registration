package helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {

    private static String TAG=SessionManager.class.getSimpleName();

    SharedPreferences pref;

    Editor editor;
    Context _context;

    int private_mode=0;

    private static final String PREF_NAME="Login";
    private static final String KEY_IS_LOGGEDIN="isLoggedIn";

    public SessionManager(Context context){
        this._context=context;
        pref=_context.getSharedPreferences(PREF_NAME,private_mode);
        editor=pref.edit();
    }

    public void setLogin(boolean isLoggedIn){
        editor.putBoolean(KEY_IS_LOGGEDIN,isLoggedIn);
        editor.commit();
        Log.d(TAG,"User login session modified!");
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN,false);
    }

}
