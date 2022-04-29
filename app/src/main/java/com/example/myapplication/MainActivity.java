package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showMessage(View view) {
        TextView text = findViewById(R.id.textView);
        TextView edit_text = findViewById(R.id.editText);
        if (edit_text.getText().toString() == null || edit_text.getText().toString().length() == 0) {
            new AlertDialog.Builder(this).setMessage("输入框不能为空").setPositiveButton("好的", null).show();
        } else {
            text.setText(edit_text.getText().toString());
            text.setVisibility(View.VISIBLE);
        }

    }

    public void jumps(View view) {
        Intent intent = new Intent(this, DisplayActivity.class);
        startActivity(intent);
    }
}