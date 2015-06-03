package com.ibu.libu.libu;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class UserActivity extends ActionBarActivity {

    Firebase fireUser;
    String date;

    String[] allBooks = {"..."};

    String name;
    String id;
    String auth;

    String[] bookID;

    int resID;

    ListAdapter bookAdapter;
    String dt;
    String dtBooks;

    ListView listOfTakenBooks;

    String rentInt;
    String[] rentIntArr;

    int av;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        final TextView txtName = (TextView)findViewById(R.id.txtName);
        final TextView txtId = (TextView)findViewById(R.id.txtId);
        final ImageView imgUser = (ImageView)findViewById(R.id.imgUser);

        listOfTakenBooks = (ListView)findViewById(R.id.listOfTakenBooks);

        Firebase.setAndroidContext(this);

        fireUser = new Firebase("https://libu.firebaseio.com/");

        auth = fireUser.getAuth().getProviderData().get("email").toString();
        auth = auth.substring(0,auth.indexOf("@"));

        fireUser.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                resID = getResources().getIdentifier(auth, "drawable", getPackageName());
                imgUser.setImageResource(resID);
                name = snapshot.child(auth+"/name").getValue().toString();
                id = snapshot.child(auth+"/id").getValue().toString();

                txtName.setText(name);
                txtId.setText(id);
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        fireUser.child("book").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                allBooks = new String[]{};
                dt = "";

                String test = "";

                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.child("borrowing/" + auth).getValue() != null) {
                        dt += "," + child.child("borrowing/" + auth).getValue() + " / " + child.getKey();
                        dtBooks += "," + child.getKey();
                        rentInt += "," + child.child("available").getValue().toString();
                    }
                }

                allBooks = dt.split(",");
                bookID = dtBooks.split(",");
                rentIntArr = rentInt.split(",");

                checkingRservation();
                listing();
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        listing();

    }

    public void checkingRservation(){

        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        date = new SimpleDateFormat("yyyy-MM-dd").format(dt);

        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        cal.add(Calendar.DATE, 5); //minus number would decrement the days
        dt = cal.getTime();
        date = new SimpleDateFormat("yyyy-MM-dd").format(dt);

        for (int i=0; i < allBooks.length; i++){
            if(allBooks[i].contains(date)){
                allBooks[i] = "";

                av = Integer.parseInt(rentIntArr[i]);
                av++;

                fireUser.child("book/" + bookID[i] + "/available").setValue(String.valueOf(av));
                fireUser.child("book/"+bookID[i]+"/borrowing").child(auth).removeValue();
            }
        }
    }

    public void listing(){
        bookAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allBooks);

        listOfTakenBooks.setAdapter(bookAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
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
