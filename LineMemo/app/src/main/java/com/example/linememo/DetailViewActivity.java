package com.example.linememo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DetailViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        initDisplay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.memo_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.edit:
                intent = new Intent(this, MemoEditActivity.class);
                startActivity(intent);
                return true;
            case R.id.delete:
                // Todo 정말 삭제하겠냐는 alert 메시지 띄움
                Toast.makeText(this, "삭제", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void initDisplay() {
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle("메모 상세 보기");
        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);
    }
}
