package com.example.hiranakalab02.sunnote.activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.hiranakalab02.sunnote.R;
import com.example.hiranakalab02.sunnote.realm.SunNote;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class PlayListActivity extends ListActivity {
    private Realm mRealm;
    private SunNote mSunNote;
    private RealmResults<SunNote> results;
    private RealmQuery<SunNote> query;
    private BaseAdapter adapter;
    private int n;

    // 要素をArrayListで設定
    private List<String> items = new ArrayList<String>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        setListAdapter(mListAdapter);

        mRealm = Realm.getInstance(this);
        mSunNote = new SunNote();

        // すべてを探してくるクエリをビルド
        query = mRealm.where(SunNote.class);
        results = query.findAll();
        n = results.size();
        Log.v("n", String.valueOf(n));

        try {
            for (int i = 0; i < n; i++) {
                mSunNote = results.get(i);
                items.add(i, mSunNote.getDateText());
            }
        } catch (Exception e) {
            Log.v("error", String.valueOf(e));
        }

        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private BaseAdapter mListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return n;
        }

        @Override
        public Object getItem(int position) {
            Log.v("getItem", String.valueOf(position));
            return null;
        }

        @Override
        public long getItemId(int position) {
            Log.v("getItemId", String.valueOf(position));
            return position + 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.activity_play_list_item, container, false);
            }
            Log.v("getView", String.valueOf(position));

            //TextView textView = (TextView)findViewById(R.id.play_list_text1);
            //textView.setText(items.get(position));
            getItem(position);

            // Because the list item contains multiple touch targets, you should not override
            // onListItemClick. Instead, set a click listener for each target individually.
            convertView.findViewById(R.id.primary_target).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            /*
                            Toast.makeText(PlayListActivity.this,
                                    R.string.touched_primary_message,
                                    Toast.LENGTH_SHORT).show();
                                    */
                        }
                    });

            convertView.findViewById(R.id.secondary_action).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            /*
                            Toast.makeText(PlayListActivity.this,
                                    R.string.touched_secondary_message,
                                    Toast.LENGTH_SHORT).show();
                                    */
                        }
                    });

            return convertView;
        }
    };

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*
            case R.id.docs_link:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, DOCS_URI));
                } catch (ActivityNotFoundException ignored) {
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
