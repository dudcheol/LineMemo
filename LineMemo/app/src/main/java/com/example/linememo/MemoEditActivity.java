package com.example.linememo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class MemoEditActivity extends AppCompatActivity {
    private EditText titleEdit;
    private EditText contentEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_edit);

        findView();
        initDisplay();
        initRoomSetting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.memo_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.save:
                saveDB();
                getDB();
//                intent = new Intent(this, MemoListActivity.class);
//                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void findView(){
        titleEdit = findViewById(R.id.titleEdit);
        contentEdit = findViewById(R.id.contentEdit);
    }

    void initDisplay() {
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle("메모 작성");
        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);
    }

    void saveDB(){
        // database 객체 생성
        AppDatabase db = Room
                .databaseBuilder(this, AppDatabase.class, "memo-db")
                .build();
        db.memoDAO()
                .insert(new Memo(titleEdit.getText().toString(),contentEdit.getText().toString(),""));
    }

    void getDB(){
        AppDatabase db = Room
                .databaseBuilder(this, AppDatabase.class, "memo-db")
                .build();
        contentEdit.setText(db.memoDAO().getAll().toString());
    }
}
