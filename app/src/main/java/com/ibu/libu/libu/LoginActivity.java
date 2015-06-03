package com.ibu.libu.libu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class LoginActivity extends ActionBarActivity {

    Firebase libu;
    String err = "Error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* Firebase config */
        Firebase.setAndroidContext(this);
        libu = new Firebase("https://libu.firebaseio.com/");
        /**************/

        Button btnLogin = (Button)findViewById(R.id.btnLogin);
        final EditText inputEmail = (EditText)findViewById(R.id.inputEmail);
        final EditText inputPass = (EditText)findViewById(R.id.inputPass);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString();
                String pass = inputPass.getText().toString();

                libu.authWithPassword(email, pass, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {

                        Intent intent = new Intent(LoginActivity.this, ListOfBooksActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        inputEmail.setLinkTextColor(Color.parseColor("#FF0000"));
                        inputPass.setLinkTextColor(Color.parseColor("#FF0000"));
                    }
                });


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
