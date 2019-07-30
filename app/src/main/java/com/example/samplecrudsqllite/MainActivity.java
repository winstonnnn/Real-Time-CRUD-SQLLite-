package com.example.samplecrudsqllite;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edt_username, edt_password;
    Button btn_submit, btn_delteData;
    DatabaseHelper myDb;

    ArrayAdapter adapter;
    ArrayList<String> arrayList;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
        btn_submit = findViewById(R.id.btn_submit);
        btn_delteData = findViewById(R.id.btn_deleteData);
        listView = findViewById(R.id.listView);

        myDb = new DatabaseHelper(this);
        btn_submit.setOnClickListener(this);
        btn_delteData.setOnClickListener(this);
        arrayList= new ArrayList<>();
        if (arrayList != null) {
            adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.activity_listview, arrayList);
        }
        listView.setAdapter(adapter);

        getAllData();

        //this is for realtime fetching of data
        Runnable runnable = new CountdownRunner();
        Thread thread = new Thread(runnable);
        thread.start();
    }

    //this is for realtime fetching of data
    class CountdownRunner implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()){
                try{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getAllData();
                        }
                    });
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }



    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_submit:
                boolean isInserted = myDb.insertData(edt_username.getText().toString(), edt_password.getText().toString());
                if (edt_username.getText().toString().trim().length() == 0 || edt_password.getText().toString().trim().length() == 0) {
                    Toast.makeText(this, "Please fill the fields", Toast.LENGTH_SHORT).show();
                }else{
                    if (isInserted == true) {
                        Toast.makeText(this, "Sucess", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to insert", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.btn_deleteData:
                boolean isDeleted = myDb.deleteData(edt_username.getText().toString());
                if (isDeleted){
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void getAllData(){
        arrayList.clear();
        Cursor cursor = myDb.getAllData();
        if (cursor.getCount() == 0){
            Toast.makeText(this, "No Value", Toast.LENGTH_SHORT).show();
        }

        while (cursor.moveToNext()){
            arrayList.add(cursor.getString(0));
            listView.invalidateViews();
            Log.d("Main", "onClick: " + cursor.getString(0) + ", " + cursor.getString(1));
        }
    }
}
