package com.shantikama.yogini;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;

public class StartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setupContent();
    }

    private void setupContent() {
        final Reader indexJson = new InputStreamReader(getResources().openRawResource(R.raw.index));
        final Index index = GsonUtils.newGson().fromJson(indexJson, Index.class);

        final ListView listView = (ListView) findViewById(R.id.practices);
        if (listView != null) {
            listView.setAdapter(new ArrayAdapter<Practice>(this,
                    R.layout.list_item_practice, R.id.name, index.practices) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = getLayoutInflater().inflate(R.layout.list_item_practice, listView, false);
                    }

                    final Practice practice = index.practices.get(position);
                    ((TextView) convertView.findViewById(R.id.name)).setText(practice.name);
                    ((TextView) convertView.findViewById(R.id.num_minutes)).setText(
                            String.format(Locale.getDefault(), "%d", practice.timeMinutes));

                    return convertView;
                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final Practice practice = index.practices.get(position);
                    Intent intent = new Intent(StartActivity.this, PracticeActivity.class);
                    intent.putExtra(PracticeActivity.KEY_PRACTICE_NAME, practice.name);
                    intent.putExtra(PracticeActivity.KEY_PRACTICE_JSON, practice.json);
                    startActivity(intent);
                }
            });
        }

//        for (Practice p :index.practices) {
//            View v = inflater.inflate(R.layout.list_item_practice, list, false);
//            list.addView(v);
//        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_begin) {
            final Intent intent = new Intent(this, PracticeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            final Intent intent = new Intent(this, PracticeActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
