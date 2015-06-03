package com.ibu.libu.libu;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


public class BookDetailsActivity extends ActionBarActivity {

    String message;

    String name;
    String rented;
    String year;
    String isbn;
    String writer;

    int resID;

    String auth;

    int rentInt;

    String lendUsers = "";
    String[] users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        final TextView txtBookName = (TextView)findViewById(R.id.txtBookName);
        final TextView txtBookRented = (TextView)findViewById(R.id.txtBookRented);
        final TextView txtBookYear = (TextView)findViewById(R.id.txtBookYear);
        final TextView txtISBN = (TextView)findViewById(R.id.txtISBN);
        final TextView txtWriter = (TextView)findViewById(R.id.txtWriter);

        ImageView imgBookCover = (ImageView)findViewById(R.id.imgBookCover);

        Button btnRent = (Button)findViewById(R.id.btnRent);

        Firebase.setAndroidContext(this);

        final Firebase libu = new Firebase("https://libu.firebaseio.com/");

        auth = libu.getAuth().getProviderData().get("email").toString();
        auth = auth.substring(0,auth.indexOf("@"));

        Intent intent = getIntent();

        // 2. get message value from intent
        message = intent.getStringExtra("name");

        resID = getResources().getIdentifier(message, "drawable", getPackageName());
        imgBookCover.setImageResource(resID);

        libu.child("book").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                name = snapshot.child(message + "/name").getValue().toString();
                rented = snapshot.child(message + "/available").getValue().toString();
                year = snapshot.child(message + "/year").getValue().toString();
                isbn = snapshot.child(message + "/isbn").getValue().toString();
                writer = snapshot.child(message + "/writer").getValue().toString();

                rentInt = Integer.parseInt(rented);

                users = new String[]{};
                lendUsers = "";

                for (DataSnapshot child : snapshot.child(message +"/borrowing").getChildren()) {
                    lendUsers += child.getKey() + ",";
                }

                users = lendUsers.split(",");

                txtBookName.setText(name);
                txtBookRented.setText(rented);
                txtBookYear.setText(year);
                txtISBN.setText(isbn);
                txtWriter.setText(writer);

            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        btnRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(rentInt>0 && !Arrays.asList(users).contains(auth)){
                    rentInt--;

                    libu.child("book/" + message + "/available").setValue(String.valueOf(rentInt));

                    Date dt = new Date();
                    String date = new SimpleDateFormat("yyyy-MM-dd").format(dt);
                    libu.child("book/"+message+"/borrowing").child(auth).setValue(date);
                }
                else {
                    Toast.makeText(getApplicationContext(), "You already reserved this book!",
                            Toast.LENGTH_LONG).show();
                }


            }
        });

        // 4. get bundle from intent
        Bundle bundle = intent.getExtras();

        // 5. get status value from bundle
        String status = bundle.getString("data");

        // 6. show status on Toast
        Toast toast = Toast.makeText(this, status, Toast.LENGTH_LONG);
        toast.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_details, menu);
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
