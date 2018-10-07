package jp.techacademy.tomokazu.kawano.taskapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_TASK = "jp.techacademy.tomokazu.kawano.taskapp.TASK";
    private Realm mRealm;
    private RealmChangeListener mRealmListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            reloadSpinner();
        }
    };

    private ListView mListView;
    private TaskAdapter mTaskAdapter;
    private CategoryAdapter mCategoryAdapter;
    private Spinner mCategorySpinner;
    private Category item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                startActivity(intent);
            }
        });

        // Realmの設定
        mRealm = Realm.getDefaultInstance();
        mRealm.addChangeListener(mRealmListener);

        // Spinnerの設定
        mCategoryAdapter = new CategoryAdapter(MainActivity.this);
        mCategorySpinner = (Spinner) findViewById(R.id.spinner);

        // Spinnerを選択した時の設定
        mCategorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item = (Category) mCategorySpinner.getSelectedItem();

                // category入力有りの場合、該当データをフィルタリング
                RealmResults<Task> taskRealmResultsFiltered = mRealm.where(Task.class).equalTo("categoryId", item.getId()).sort("date", Sort.DESCENDING).findAll();
                // 上記の結果を、TaskList としてセットする
                mTaskAdapter.setTaskList(mRealm.copyFromRealm(taskRealmResultsFiltered));

                // TaskのListView用のアダプタに渡す
                mListView.setAdapter(mTaskAdapter);
                // 表示を更新するために、アダプターにデータが変更されたことを知らせる
                mTaskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // category未選択          の場合、Realmデータベースから、「全てのデータを取得して新しい日時順に並べた結果」を取得
                RealmResults<Task> taskRealmResultsAll = mRealm.where(Task.class).sort("date", Sort.DESCENDING).findAll();
                // 上記の結果を、TaskList としてセットする
                mTaskAdapter.setTaskList(mRealm.copyFromRealm(taskRealmResultsAll));

                // TaskのListView用のアダプタに渡す
                mListView.setAdapter(mTaskAdapter);
                // 表示を更新するために、アダプターにデータが変更されたことを知らせる
                mTaskAdapter.notifyDataSetChanged();
            }
        });

        // ListViewの設定
        mTaskAdapter = new TaskAdapter(MainActivity.this);
        mListView = (ListView) findViewById(R.id.listView1);

        // ListViewを押した時の設定
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 入力・編集する画面へ遷移
                Task task = (Task) parent.getAdapter().getItem(position);

                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                intent.putExtra(EXTRA_TASK, task.getId());

                startActivity(intent);
            }
        });

        // ListViewを長押しした時の設定
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                // タスクを削除する
                final Task task = (Task) parent.getAdapter().getItem(position);

                //ダイアログを表示する
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("削除");
                builder.setMessage(task.getTitle() + "を削除しますか");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        RealmResults<Task> results = mRealm.where(Task.class).equalTo("id", task.getId()).findAll();

                        mRealm.beginTransaction();
                        results.deleteAllFromRealm();
                        mRealm.commitTransaction();

                        Intent resultIntent = new Intent(getApplicationContext(), TaskAlarmReceiver.class);
                        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(
                                MainActivity.this,
                                task.getId(),
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.cancel(resultPendingIntent);
                    }
                });

                builder.setNegativeButton("CANCEL", null);

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            }
        });

        reloadSpinner();
    }

    protected void onResume() {
        super.onResume();
        reloadSpinner();
    }

    private void reloadSpinner() {
        // category未入力の場合、Realmデータベースから、「全てのデータを取得してcategoryを昇順にソートした結果」を取得
        RealmResults<Category> categoryRealmResults = mRealm.where(Category.class).sort("category", Sort.ASCENDING).findAll();
        // 上記の結果を、CategoryList としてセットする
        mCategoryAdapter.setCategoryList(mRealm.copyFromRealm(categoryRealmResults));

        // CategoryのSpinner用のアダプタに渡す
        mCategorySpinner.setAdapter(mCategoryAdapter);
        // 表示を更新するために、アダプターにデータが変更されたことを知らせる
        mCategoryAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}