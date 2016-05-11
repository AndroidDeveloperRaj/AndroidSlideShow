package congnt.com.androidslideshow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SINGLE = 1;
    private static final int REQUEST_CODE_MULTI = 10;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.lv_sets)
    RecyclerView lvSets;
    @BindView(R.id.fab_add_set)
    FloatingActionButton fabAddSet;
    @BindView(R.id.fab_add_item)
    FloatingActionButton fabAddItem;
    @BindView(R.id.fam)
    FloatingActionsMenu fam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
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

    @OnClick({R.id.fab_add_set, R.id.fab_add_item})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.fab_add_set:
                intent = new Intent(this, AlbumSelectActivity.class);
//set limit on number of images that can be selected, default is 10
                intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 10);
                startActivityForResult(intent, REQUEST_CODE_MULTI);
                break;
            case R.id.fab_add_item:
                intent = new Intent(this, AlbumSelectActivity.class);
//set limit on number of images that can be selected, default is 10
                intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 1);
                startActivityForResult(intent, REQUEST_CODE_SINGLE);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            switch (requestCode){
                case REQUEST_CODE_MULTI:
                    Intent intent = new Intent(this, SlideEditorActivity.class);
                    intent.putExtra("images", images);
                    startActivity(intent);
                    break;
                case REQUEST_CODE_SINGLE:

                    break;
            }

        }
    }
}
