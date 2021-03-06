package com.example.app_profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_profile.Room.AppDatabase_foreign;
import com.example.app_profile.Room.AppDatabase_school;
import com.example.app_profile.Room.User_foreign;
import com.example.app_profile.Room.User_school;

public class SaveMemoActivity_school extends AppCompatActivity {

    private final int REQUEST_CODE = 200;
    private EditText description;
    private EditText description2;
    private EditText description3;
    private AppDatabase_school db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_memo_school);

        initialized();
    }

    private void initialized() {
        description = findViewById(R.id.description_school);
        description2 = findViewById(R.id.description2_school);
        description3 = findViewById(R.id.description3_school);

        db = AppDatabase_school.getInstance(this);

    }

    //메모저장하는 버튼
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_memo_menu_school, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.save_school:
                make_title();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void make_title() {

        EditText editText = new EditText(getApplicationContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("제목을 입력하세요");
        builder.setView(editText);

        builder.setPositiveButton("저장", (dialog, which) -> {
            String s = editText.getText().toString();
            // db에 저장하기
            User_school memo = new User_school(s, description.getText().toString(), description2.getText().toString(), description3.getText().toString());
            db.userDao().insert(memo);
            Toast.makeText(getApplicationContext(),"저장되었습니다", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            finish();
        });

        builder.setNegativeButton("취소", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.show();
    }
}
