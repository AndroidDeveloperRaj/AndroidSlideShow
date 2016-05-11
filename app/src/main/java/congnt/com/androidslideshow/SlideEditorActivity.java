package congnt.com.androidslideshow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

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

public class SlideEditorActivity extends AppCompatActivity {

    private static final int SELECT_MUSIC = 12;
    @BindView(R.id.btn_save)
    ImageButton btnSave;
    @BindView(R.id.btn_effect)
    ImageButton btnEffect;
    @BindView(R.id.btn_music)
    ImageButton btnMusic;
    @BindView(R.id.ll_nav)
    LinearLayout llNav;
    @BindView(R.id.videoView)
    VideoView videoView;
    AlertDialog dialog;
    private String musicUri = "";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private FFmpeg ffmpeg;
    private MediaController mediaControls;

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
        setContentView(R.layout.activity_slide_editor);
        ButterKnife.bind(this);
        initFFmpeg();
        String query = "-version";
//        etQuery.setText(query);//set the media controller buttons
        if (mediaControls == null) {
            mediaControls = new MediaController(this);
        }
        videoView.setMediaController(mediaControls);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
        dialog = new AlertDialog.Builder(this)
                .setTitle("Executing Video...")
                .setMessage("Please wait....")
                .create();
        if (getIntent() != null) {
            final ArrayList<Image> images;
            Intent intent = getIntent();
            if ((images = (ArrayList<Image>) intent.getSerializableExtra("images")) != null) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        //Scale all selected image
                        for (int i = 0; i < images.size(); i++) {
                            executeFFMPEG(scaleImage(images.get(i), 730, 456), false, "");
                        }
                        executeFFMPEG(filterComplexQuery(images, 730, 456), true, "out.mp4");
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                    }
                }.execute();
            }
        }
    }

    private void onExcuteFinish(String output) {
        videoView.setVideoPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                "/"+output);
        videoView.start();
        dialog.dismiss();
    }

    private String scaleImage(Image image, int width, int heigh) {
        String newFile = new File(image.path).getParent() + "/resize_" + image.name;
        deleteTempFile(newFile);
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

    private void executeFFMPEG(String query, final boolean isShowDialog, final String output) {
        // to execute "ffmpeg -version" command you just need to pass "-version"
        if (output.equalsIgnoreCase("out.mp4")){

            deleteTempFileFromDownload("out.mp4");
        }
        try {
            ffmpeg.execute(query, new ExecuteBinaryResponseHandler() {
                @Override
                public void onStart() {
                    if (isShowDialog){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.show();
                            }
                        });
                    }
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
//                    dialog.dismiss();
                    if (isShowDialog) {
                        onExcuteFinish(output);
                    }
                }

                @Override
                public void onFinish() {
//                    dialog.dismiss();
                    if (isShowDialog) {
                        onExcuteFinish(output);
                    }
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }
    }

    private void deleteTempFile(String path) {
        File fileOut = new File(path);
        if (fileOut.exists()) {
            if (fileOut.delete()) {
            } else {
            }
        } else {
        }
    }

    private void deleteTempFileFromDownload(String fileName) {
        File fileOut = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                "/" + fileName);
        if (fileOut.exists()) {
            if (fileOut.delete()) {
            } else {
            }
        } else {
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
                "/out.mp4";
        return query;
    }

    private String filterComplexQuery(ArrayList<Image> images, int width, int heigh) {
        String query = "";
        for (int i = 0; i < images.size(); i++) {
            query += " -loop 1 -t 1 -i " + new File(images.get(i).path).getParent() + "/resize_" + images.get(i).name;
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
                "/out.mp4";
        return query;
    }

    @OnClick({R.id.btn_save, R.id.btn_effect, R.id.btn_music})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                break;
            case R.id.btn_effect:
                break;
            case R.id.btn_music:
                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, SELECT_MUSIC);
                break;
        }
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

    private String mergeAudio(String musicUri) {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                "/out.mp4";
        String tmp = "-i " + musicUri + "" +
                " -i " + path +
                " -map 0:a -map 1:v -strict -2 -shortest -profile:v baseline -movflags faststart " +
                "" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                "/output_with_music.mp4";
        return tmp;
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
                executeFFMPEG(mergeAudio(musicUri), true, "output_with_music.mp4");
                Log.d("AAA", "AAAa " + " " + musicUri);

            }
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        String filePath = "";
        String wholeID = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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
