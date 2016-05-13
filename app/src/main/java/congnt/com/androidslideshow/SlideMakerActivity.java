package congnt.com.androidslideshow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.darsh.multipleimageselect.models.Image;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SlideMakerActivity extends AppCompatActivity {

    private static final int SELECT_MUSIC = 12;
    private static final String CURRENT_ID = "currentid";
    @BindView(R.id.et_query)
    EditText etQuery;
    @BindView(R.id.btn_query)
    Button btnQuery;
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.btn_music)
    Button btnMusic;
    private FFmpeg ffmpeg;
    private String musicUri = "";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_maker);
        ButterKnife.bind(this);
        initFFmpeg();
        String query = "-version";
//        etQuery.setText(query);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
        if (getIntent() != null) {
            ArrayList<Image> images;
            Intent intent = getIntent();
            if ((images = (ArrayList<Image>) intent.getSerializableExtra("images")) != null) {
                for (int i = 0; i < images.size(); i++) {
                    executeFFMPEG(scaleImage(images.get(i), 730, 456));
                }
                etQuery.setText(filterComplexQuery(images, 730, 456));
            }
        }
    }

    private void executeFFMPEG(String query) {
        // to execute "ffmpeg -version" command you just need to pass "-version"
        try {
            ffmpeg.execute(query.split(" "), new ExecuteBinaryResponseHandler() {
                @Override
                public void onStart() {
                }

                @Override
                public void onProgress(String message) {
                    Log.d("AA", message);
                }

                @Override
                public void onFailure(String message) {
                }

                @Override
                public void onSuccess(String message) {
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }
    }

    private String simpleQuery(ArrayList<Image> images) {
        String query = "";
        for (int i = 0; i < images.size(); i++) {
            query += " -loop 1 -t 3 -i " + images.get(i).path;
        }
        query += " -filter_complex ";
        for (int i = 0; i < images.size(); i++) {
            query += "[" + i + ":v]";
        }
        query += "concat=n=" + images.size() + ":v=1:a=0,format=yuv420p[v] -r 10 -map [v]";
        query += " -c:v libx264 -profile:v baseline -c:a libfaac -ar 44100 -ac 2 -b:a 128k -movflags faststart " +
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                "/out_" + prefs.getInt(CURRENT_ID, 0) + ".mp4";
        return query;
    }

    private String filterComplexQuery2(ArrayList<Image> images, int width, int heigh) {
        String query = "";
        for (int i = 0; i < images.size(); i++) {
            query += " -loop 1 -t 1 -i " + images.get(i).path;
        }
        query += " -filter_complex ";
        for (int i = 0; i < images.size(); i++) {
            query += "[" + i + ":v]scale=w=" + width + ":h=" + heigh + ",setsar=sar=1/1[sar" + i + "];";
        }
//        for (int i = 0; i < images.size() - 1; i++) {
//            query += "[sar" + (i + 1) + "]" + "[sar" + i + "]blend=all_expr='A*(if(gte(T,0.5),1,T/0.5))+B*(1-(if(gte(T,0.5),1,T/0.5)))'[b" + (i + 1) + "v];";
//        }
//        for (int i = 0; i < images.size(); i++) {
//            query += "[" + i + ":v]scale=w="+width+":h="+heigh+",setsar=sar=1/1[sar"+i+"];";
//        }
        for (int i = 0; i < images.size(); i++) {
            query += "[sar" + (i) + "]";
        }
        query += "concat=n=" + (images.size()) + ":v=1:a=0,format=yuv420p[v] -map [v]";
        query += " -c:v libx264 -profile:v baseline -c:a libfaac -ar 44100 -ac 2 -b:a 128k -movflags faststart "
                + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                "/out_" + prefs.getInt(CURRENT_ID, 0) + ".mp4";
        return query;
    }

    private String scaleImage(Image image, int width, int heigh) {
        String newFile = new File(image.path).getParent() + "/resize_" + image.name;
        if (new File(newFile).exists()){
            new File(newFile).delete();
        }
        String query = "";
        query += "-i " +
                image.path +
                " -vf scale=" +
                width +
                ":" +
                heigh + ",setsar=1:1 " +
                newFile;
        return query;
    }

    private String filterComplexQuery(ArrayList<Image> images, int width, int heigh) {
        String query = "";
        for (int i = 0; i < images.size(); i++) {
            query += " -loop 1 -t 1 -i " +  new File(images.get(i).path).getParent() + "/resize_" + images.get(i).name;
        }
        query += " -filter_complex ";
        for (int i = 0; i < images.size() - 1; i++) {
            query += "[" +
                    (i + 1) +
                    ":v][" +
                    i +
                    ":v]blend=all_expr='A*(if(gte(T,0.5),1,T/0.5))+B*(1-(if(gte(T,0.5),1,T/0.5)))'[b" + (i + 1) + "v];";
        }
        query += "[0:v]";
        for (int i = 1; i < images.size(); i++) {
            query += "[b" + (i) + "v]" + "[" +
                    (i) +
                    ":v]";
        }
        query += "concat=n=" + (images.size() * 2 - 1) + ":v=1:a=0,format=yuv420p[v] -map [v]";
        query += " -c:v libx264 -profile:v baseline -c:a libfaac -ar 44100 -ac 2 -b:a 128k -movflags faststart "
                + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                "/out_0.mp4";
        return query;
    }

    private String generateFadeQuery() {
        return null;
    }

    private String mergeAudio() {
        String tmp = "-i " + musicUri + "" +
                " -i " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                "/out_" + prefs.getInt(CURRENT_ID, 0) + ".mp4" +
                " -map 0:a -map 1:v -strict -2 -shortest -profile:v baseline -movflags faststart " +
                "" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                "/output_with_music_" + prefs.getInt(CURRENT_ID, 0) + ".mp4";
        return tmp;
    }

    private void initFFmpeg() {
        ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onFailure() {
                }

                @Override
                public void onSuccess() {
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }
    }

    @OnClick({R.id.btn_query, R.id.btn_music})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_query:
                try {
                    // to execute "ffmpeg -version" command you just need to pass "-version"
                    ffmpeg.execute(etQuery.getText().toString().split(""), new ExecuteBinaryResponseHandler() {

                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onProgress(String message) {
                            tvResult.append(message);
                            Log.d("AA", message);
                        }

                        @Override
                        public void onFailure(String message) {
//                    Log.e("AA", message);
//                    tvResult.setText(message);
                        }

                        @Override
                        public void onSuccess(String message) {
//                    tvResult.setText(message);
                        }

                        @Override
                        public void onFinish() {
                        }
                    });
                } catch (FFmpegCommandAlreadyRunningException e) {
                    // Handle if FFmpeg is already running
                }
                break;
            case R.id.btn_music:
                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, SELECT_MUSIC);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == SELECT_MUSIC) {
                musicUri = data.getData().toString();
                Log.d("AAA", "AAAa " + " " + musicUri);
                if (musicUri.contains("content://")) {
                    musicUri = getRealPathFromURI(this, data.getData());
                } else {
                    musicUri = data.getData().getPath();
                }
                Log.d("AAA", "AAAa " + " " + musicUri);
                etQuery.setText(mergeAudio());
            }
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        String filePath = "";
        String wholeID = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            wholeID = DocumentsContract.getDocumentId(contentUri);
        } else {
            return getRealPathFromURI_API11to18(context, contentUri);
        }

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

}
