package jp.techacademy.tomokazu.kawano.taskapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
        categoryEditText = (EditText) findViewById(R.id.category_edit_text);

        // EXTRA_TASK から Task の id を取得して、 id から Task のインスタンスを取得する
        Intent intent = getIntent();
        int taskId = intent.getIntExtra(MainActivity.EXTRA_TASK, -1);
        Realm realm = Realm.getDefaultInstance();
        mCategory = realm.where(Category.class).equalTo("id", taskId).findFirst();
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

        realm.beginTransaction();

        if (mCategory == null) {
            // 新規作成の場合
            mCategory = new Category();

            RealmResults<Task> taskRealmResults = realm.where(Task.class).findAll();

            int identifier;
            if (taskRealmResults.max("id") != null) {
                identifier = taskRealmResults.max("id").intValue() + 1;
            } else {
                identifier = 0;
            }
            mCategory.setId(identifier);
        }

        String category = categoryEditText.getText().toString();
        mCategory.setCategory(category);

        Intent intent = new Intent(InputCategory.this, InputActivity.class);
        intent.putExtra(MainActivity.EXTRA_TASK, mCategory.getId());
        startActivity(intent);
    }
}