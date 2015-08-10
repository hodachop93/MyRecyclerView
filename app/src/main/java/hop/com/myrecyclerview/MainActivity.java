package hop.com.myrecyclerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private List<Item> mItems;

    private ActionMode mActionMode;

    private ActionModeCallBack mActionModeCallBack = new ActionModeCallBack();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true));
        //mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Random rd = new Random();
        mItems = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Item item = new Item("Item " + i, "This is subtitle of item number " + i, rd.nextBoolean());
            mItems.add(item);
        }

        mAdapter = new RecyclerViewAdapter(mItems, this);
        mAdapter.setOnRVItemClickListener(new RecyclerViewAdapter.RVOnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (mActionMode==null){
                    Toast.makeText(getApplicationContext(), "Item "
                            + position + " was clicked", Toast.LENGTH_SHORT).show();

                }else{
                    mAdapter.toggleSelection(position);
                }
            }

            @Override
            public void onTitleClick(View v, int position) {
                Toast.makeText(getApplicationContext(), "Title of item " + position + " was clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View v, int position) {
                if (mActionMode == null) {
                    mActionMode = startSupportActionMode(mActionModeCallBack);
                }
                toogleSelection(position);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Enable selected mode of the item at the position if it was not selected or
     * deselect it if it was selected.
     * @param position
     */
    private void toogleSelection(int position) {
        //enable selected mode of the item
        mAdapter.toggleSelection(position);

        //count the number of selected items and display on the ActionBar
        int count = mAdapter.getSelectedItemCount();
        if (count == 0){
            mActionMode.finish();
        }else{
            mActionMode.setTitle(String.valueOf(count));
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private class ActionModeCallBack implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.selected_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    mAdapter.removeItems(mAdapter.getSelectedItems());
                    mode.finish();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelection();
            mActionMode = null;
        }
    }
}
