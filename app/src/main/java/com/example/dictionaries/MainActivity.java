package com.example.dictionaries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private MyDBHelper dbHelper;
    private SQLiteDatabase db;
    private EditText editText;
    private String word;
    private TextView textView;
    private Cursor cursor;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String res = msg.obj.toString();
            String showStr = "单词：" + word + "\n释义：" + res;
            textView.setText(showStr);

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new MyDBHelper(this, "dict.db", null, 1);
        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textView.setText("");
                db = dbHelper.getReadableDatabase();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) { // 文本发生改变后执行查找
                word = s.toString();
                cursor = db.rawQuery("select * from dicttab where word='" + word + "'", null);
                String str = "";
                while (cursor.moveToNext()) {
                    str = cursor.getString(2);
                }
                if (str == null || str.equals("")) {
                    return;
                }
                Message msg = new Message();
                msg.obj = str;
                handler.sendMessage(msg);
            }
        });
    }

    //    public void fyBtn(View view){// 翻译
//        if(editText.getText().toString().equals("")){
//            return;
//        }
//        db=dbHelper.getReadableDatabase();
//        word=editText.getText().toString().trim();
//        Cursor cursor=db.rawQuery("select * from dicttab where word='"+word+"'",null);
//        String str="";
//        while(cursor.moveToNext()){
//            str=cursor.getString(2);
//        }
//        Message msg=new Message();
//        msg.obj=str;
//        handler.sendMessage(msg);
//    }
    public void addBtn(View view) {// 添加
        db = dbHelper.getWritableDatabase();
        System.out.println("awdawdwa");
        final ContentValues values = new ContentValues();
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.add_layout, null);
        final EditText et1 = textEntryView.findViewById(R.id.add_word);
        final EditText et2 = textEntryView.findViewById(R.id.add_cn);
        AlertDialog.Builder dg = new AlertDialog.Builder(MainActivity.this);
        dg.setTitle("生词添加");
        dg.setView(textEntryView);
        dg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                String word = et1.getText().toString().trim();
                String cn = et2.getText().toString().trim();
                if (word.equals("") || cn.equals("")) {
                    return;
                }
                values.put("word", word);
                values.put("cnexplain", cn);
                long s = db.insert("dicttab", null, values);
                if (s > 0) {
                    Toast.makeText(MainActivity.this, "添加成功！", Toast.LENGTH_SHORT).show();
                    et1.setText("");
                    et2.setText("");
                }
                db.close();
            }
        });
        dg.setNegativeButton("取消", null);
        dg.show();
    }

    public void upBtn(View view) {// 修改
        db = dbHelper.getWritableDatabase();
        final ContentValues values = new ContentValues();
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.up_layout, null);
        final EditText et1 = textEntryView.findViewById(R.id.up_word);
        final EditText et2 = textEntryView.findViewById(R.id.up_cn);
        AlertDialog.Builder dg = new AlertDialog.Builder(MainActivity.this);
        dg.setTitle("生词修改");
        dg.setView(textEntryView);
        dg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                String word = et1.getText().toString().trim();
                String cn = et2.getText().toString().trim();
                if (word.equals("") || cn.equals("")) {
                    return;
                }
                values.put("word", word);
                values.put("cnexplain", cn);
                long s = db.update("dicttab", values, "word=?", new String[]{word});
                if (s > 0) {
                    Toast.makeText(MainActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
                    et1.setText("");
                    et2.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "修改失败！", Toast.LENGTH_SHORT).show();
                }
                db.close();
            }
        });
        dg.setNegativeButton("取消", null);
        dg.show();
    }

    public void delBtn(View view) {// 删除
        db = dbHelper.getWritableDatabase();
        final ContentValues values = new ContentValues();
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.del_layout, null);
        final EditText et1 = textEntryView.findViewById(R.id.del_word);
        AlertDialog.Builder dg = new AlertDialog.Builder(MainActivity.this);
        dg.setTitle("生词添加");
        dg.setView(textEntryView);
        dg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                String word = et1.getText().toString().trim();
                if (word.equals("")) {
                    return;
                }
                values.put("word", word);
                long s = db.delete("dicttab", "word=?", new String[]{word});
                if (s > 0) {
                    Toast.makeText(MainActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                    et1.setText("");

                } else {
                    Toast.makeText(MainActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
                }
                db.close();
            }
        });
        dg.setNegativeButton("取消", null);
        dg.show();
    }
}
