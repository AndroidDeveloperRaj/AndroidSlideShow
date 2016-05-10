package congnt.com.androidslideshow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.darsh.multipleimageselect.models.Image;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SlideMakerActivity extends AppCompatActivity {

    @BindView(R.id.et_query)
    EditText etQuery;
    @BindView(R.id.btn_query)
    Button btnQuery;
    @BindView(R.id.tv_result)
    TextView tvResult;
    private FFmpeg ffmpeg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_maker);
        ButterKnife.bind(this);
        initFFmpeg();
        String query = "-version";
//        etQuery.setText(query);
        if (getIntent()!=null){
            ArrayList<Image> images;
            Intent intent = getIntent();

            if ((images = (ArrayList<Image>) intent.getSerializableExtra("images")) != null){
                query = "";
                for (int i = 0; i < images.size(); i++) {
                    query+=" -loop 1 -t 1 -i "+images.get(i).path;
                }
                query+= " -filter_complex ";
                for (int i = 0; i < images.size(); i++) {
                    query+="["+i+":v]";
                }
                query+="concat=n="+images.size()+":v=1:a=0,format=yuv420p[v] -r 10 -map [v]";
                query+=" -c:v libx264 -profile:v baseline -c:a libfaac -ar 44100 -ac 2 -b:a 128k -movflags faststart /storage/emulated/0/Download/out.mp4";
                etQuery.setText(query.trim());
//                etQuery.setText(generateQuery(images).trim());
            }
        }
    }
    private String generateQuery(ArrayList<Image> images){
        String query ="";
        for (int i = 0; i < images.size(); i++) {
            query+=" -loop 1 -t 1 -i "+images.get(i).path;
        }
        query+= " -filter_complex ";
        for (int i = 0; i < images.size()-1; i++) {
            query+="["+(i+1)+":v]"+"["+i+":v]blend=all_expr='A*(if(gte(T,0.5),1,T/0.5))+B*(1-(if(gte(T,0.5),1,T/0.5)))'[b"+(i+1)+"v]; ";
        }
        query+="[0:v]";
        for (int i = 0; i < images.size()-1; i++) {
            query+="["+(i+1)+":v][b"+(i+1)+"v]";
        }
        query+="concat=n="+(images.size()*2-1)+":v=1:a=0,format=yuv420p[v] -map [v]";
        query+=" -c:v libx264 -profile:v baseline -c:a libfaac -ar 44100 -ac 2 -b:a 128k -movflags faststart /storage/emulated/0/Download/out.mp4";
        return query;
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
    @OnClick(R.id.btn_query)
    public void onClick() {
        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(etQuery.getText().toString(), new ExecuteBinaryResponseHandler() {

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
    }
}
