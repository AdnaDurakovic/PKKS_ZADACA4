package com.ibu.libu.libu;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ListOfBooksActivity extends ActionBarActivity {

    String[] allBooks = {"..."};
    String[] allBooksClone = {"..."};

    String dt;
    int i = 0;

    ListView listOfBooks;
    ListAdapter bookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_books);

        /* Firebase config */
        Firebase.setAndroidContext(this);
        final Firebase libu = new Firebase("https://libu.firebaseio.com/");
        /**************/

        final Button btnSearch = (Button)findViewById(R.id.btnSearch);
        final EditText searchField = (EditText)findViewById(R.id.searchField);

        listOfBooks = (ListView)findViewById(R.id.listOfBooks);

        listing();

        libu.child("book").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                allBooks = new String[]{};
                dt = "";
                int i = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    dt += child.getKey() + ",";
                    i++;
                }

                allBooks = dt.split(",");

                listing();


                allBooksClone = allBooks;
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        listOfBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String book = String.valueOf(parent.getItemAtPosition(position));

                Intent intent = new Intent(ListOfBooksActivity.this, BookDetailsActivity.class);

                intent.putExtra("name", book);

                Bundle bookData = new Bundle();
                bookData.putString("data", "Book details");
                intent.putExtras(bookData);

                startActivity(intent);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                allBooks = allBooksClone;

                final List<String> list =  new ArrayList<String>();
                Collections.addAll(list, allBooks);

                String search = searchField.getText().toString();

                for (int i=0; i<allBooks.length; i++)
                {
                    if (!allBooks[i].contains(search))
                    {
                        list.remove(allBooks[i].toString());
                    }
                }

                allBooks = list.toArray(new String[list.size()]);
                listing();
            }
        });

    }

    public void listing(){
        bookAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allBooks);
        listOfBooks.setAdapter(bookAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_of_books, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                Intent intent = new Intent(ListOfBooksActivity.this, UserActivity.class);
                startActivity(intent);

                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
