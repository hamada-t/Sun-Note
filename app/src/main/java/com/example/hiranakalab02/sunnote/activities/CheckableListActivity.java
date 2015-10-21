package com.example.hiranakalab02.sunnote.activities;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hiranakalab02.sunnote.R;
import com.example.hiranakalab02.sunnote.realm.SunNote;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by HiranakaLab02 on 2015/10/16.
 */
public class CheckableListActivity extends ListActivity {
    private Realm mRealm;
    private SunNote mSunNote;
    private RealmResults<SunNote> results;
    private RealmQuery<SunNote> query;
    private int n;
    private String[] data = new  String[n];

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(this);
        mSunNote = new SunNote();

        // すべてを探してくるクエリをビルド
        query = mRealm.where(SunNote.class);
        results = query.findAll();
        n = results.size();

        try {
            for (int i = 0; i < n; i++) {
                mSunNote = results.get(i);
                data[i] = mSunNote.getDateText();
            }
        } catch (Exception e) {
            Log.v("error", String.valueOf(e));
        }

        setContentView(R.layout.activity_checkable_list);
        setListAdapter(new MyAdapter(this, R.layout.checkable_list_item, data));
    }

    /**
     * A simple array adapter that creates a lists.
     */
    private class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int itemLayoutId;

        public MyAdapter(Context context, int itemLayoutId, String[] itemList) {
            super();
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.itemLayoutId = itemLayoutId;
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public String getItem(int position) {
            return data[position];
        }

        @Override
        public long getItemId(int position) {
            return data[position].hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.activity_checkable_list, container, false);
            }

            ((TextView) convertView.findViewById(android.R.id.text1)).setText(getItem(position));
            return convertView;
        }
    }
}
