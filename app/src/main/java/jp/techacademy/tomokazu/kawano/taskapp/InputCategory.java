package jp.techacademy.tomokazu.kawano.taskapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;

public class InputCategory extends AppCompatActivity {

    private Category mCategory;
    private EditText categoryEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_input);

        // UI部品の設定
        findViewById(R.id.createButton).setOnClickListener(mCreateClickListener);
        categoryEditText = (EditText) findViewById(R.id.categoryEditText);

        Intent intent = getIntent();
        Realm realm = Realm.getDefaultInstance();

        realm.close();
    }

    public View.OnClickListener mCreateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addCategory();
            finish();
        }
    };

    private void addCategory() {
        Realm realm = Realm.getDefaultInstance();

        String category = categoryEditText.getText().toString();

        RealmResults<Category> existingCategory = realm.where(Category.class).equalTo("category", category).findAll();

        if (existingCategory.size() == 0) {
            // カテゴリ名が重複していない場合の設定

            realm.beginTransaction();

            if (mCategory == null) {
                // 新規作成の場合
                mCategory = new Category();

                RealmResults<Category> categoryRealmResults = realm.where(Category.class).findAll();

                int identifier;

                if (categoryRealmResults.max("id") != null) {
                    identifier = categoryRealmResults.max("id").intValue() + 1;
                } else {
                    identifier = 0;
                }
                mCategory.setId(identifier);


                mCategory.setCategory(category);

                realm.copyToRealmOrUpdate(mCategory);
                realm.commitTransaction();
                realm.close();
            }

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(InputCategory.this);

            builder.setTitle("重複");
            builder.setMessage("このカテゴリ名は既に登録されています。");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}