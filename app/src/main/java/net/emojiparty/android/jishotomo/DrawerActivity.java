package net.emojiparty.android.jishotomo;

import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.huma.room_for_asset.RoomAsset;
import net.emojiparty.android.jishotomo.data.AppDatabase;
import net.emojiparty.android.jishotomo.ui.DataBindingAdapter;
import net.emojiparty.android.jishotomo.ui.PagedEntryViewModel;

public class DrawerActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_drawer);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    setupFab();
    setupDrawer(toolbar);
    setupNavigationView();
    setupRecyclerView();

    AsyncTask.execute(new Runnable() {
      @Override public void run() {
        AppDatabase db =
            RoomAsset.databaseBuilder(getApplicationContext(), AppDatabase.class, "jishotomo.db")
                .build();
        db.entryDao().getPrimarySenseEntry(77566);
      }
    });
  }

  private void setupRecyclerView() {
    RecyclerView searchResults = findViewById(R.id.search_results_rv);
    searchResults.setLayoutManager(new LinearLayoutManager(DrawerActivity.this));



    PagedEntryViewModel viewModel =
        ViewModelProviders.of(this).get(PagedEntryViewModel.class);
    DataBindingAdapter adapter = new DataBindingAdapter(R.layout.list_item_entry);
    viewModel.entries.observe(this, adapter::submitList);
    searchResults.setAdapter(adapter);
  }

  private void setupFab() {
    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show();
      }
    });
  }

  private void setupDrawer(Toolbar toolbar) {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();
  }

  private void setupNavigationView() {
    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
  }

  @Override public void onBackPressed() {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.drawer, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  // this is for the drawer, maybe move to another class
  @SuppressWarnings("StatementWithEmptyBody") @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.nav_camera) {
      // Handle the camera action
    } else if (id == R.id.nav_gallery) {

    } else if (id == R.id.nav_slideshow) {

    } else if (id == R.id.nav_manage) {

    } else if (id == R.id.nav_share) {

    } else if (id == R.id.nav_send) {

    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }
}
