package com.example.hiranakalab02.sunnote.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hiranakalab02.sunnote.common.Preservation;
import com.example.hiranakalab02.sunnote.R;
import com.example.hiranakalab02.sunnote.realm.SunNote;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by HiranakaLab02 on 2015/10/15.
 */
public class PlayListView extends Activity implements AdapterView.OnItemClickListener{
    private Realm mRealm;
    private SunNote mSunNote;
    private RealmResults<SunNote> results;
    private RealmQuery<SunNote> query;
    private BaseAdapter adapter;
    private int n;

    // 要素をArrayListで設定
    private List<String> items = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView listView = new ListView(this);
        setContentView(listView);

        mRealm = Realm.getInstance(this);
        mSunNote = new SunNote();

        // すべてを探してくるクエリをビルド
        query = mRealm.where(SunNote.class);
        results = query.findAll();
        n = results.size();
        // 最初のリスト
        String[] INITIAL_LIST = new String[n];

        try {
            for (int i = 0; i < n; i++) {
                mSunNote = results.get(i);
                items.add(i, mSunNote.getDateText());
            }
        } catch (Exception e) {
            Log.v("error", String.valueOf(e));
        }

        adapter = new ListViewAdapter(this, R.layout.list_item, INITIAL_LIST);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        Intent intent = new Intent(this.getApplicationContext(), PlayActivity.class);
        // Activity をスイッチする
        startActivity(intent);
    }

    class ViewHolder {
        TextView textView;
    }

    class ListViewAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int itemLayoutId;

        public ListViewAdapter(Context context, int itemLayoutId, String[] itemList) {
            super();
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.itemLayoutId = itemLayoutId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            // 最初だけ View を inflate して、それを再利用する
            if (convertView == null) {
                // activity_main.xml の ＜ListView .../＞ に list_items.xml を inflate して convertView とする
                convertView = inflater.inflate(itemLayoutId, parent, false);
                // ViewHolder を生成
                holder = new ViewHolder();
                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                convertView.setTag(holder);
            }
            // holder を使って再利用
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 現在の position にあるファイル名リストを holder の textView にセット
            holder.textView.setText(items.get(position));

            return convertView;
        }

        @Override
        public int getCount() {
            // items の全要素数を返す
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            Preservation preservation = new Preservation();
            preservation.setIndex(position);
            return position;
        }
    }
}
