package congnt.com.androidslideshow;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FramagicActivity extends AppCompatActivity{

    ImageView mainPIC, mainSTK, mainFRM;
    Bitmap bitmap, frameBmp;
    RelativeLayout main_edit;
    Bitmap temp;
    HorizontalScrollView frame_sv, sticker_sv, filter_sv;
    RelativeLayout text_sv;
    EditText editText;
    TextView textView;
    private static final int CAMERA_REQUEST = 1;
    private static final int GALERRY_REQUEST = 2;
    String imgDecodableString;
    String m_Text;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       /* //set fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(Build.VERSION.SDK_INT < 19) { //View.SYSTEM_UI_FLAG_IMMERSIVE is only on API 19+
            this.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        } else {
            this.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }*/

        setContentView(R.layout.activity_framagic);

        main_edit = (RelativeLayout)findViewById(R.id.main_edit);
        mainPIC = (ImageView)findViewById(R.id.main_pic);
        mainSTK = (ImageView)findViewById(R.id.main_sticker);
        mainSTK.setEnabled(false);
        mainFRM = (ImageView)findViewById(R.id.main_frame);



        frame_sv = (HorizontalScrollView) findViewById(R.id.frame_scrollView);
        filter_sv = (HorizontalScrollView) findViewById(R.id.filter_scrollView);
        sticker_sv = (HorizontalScrollView) findViewById(R.id.sticker_scrollView);
        text_sv = (RelativeLayout) findViewById(R.id.text_scrollView);
        text_sv.setVisibility(View.INVISIBLE);

//        Toast.makeText(MainActivity.this, main_edit.getHeight() + " - " + mainFRM.getHeight(),
//                Toast.LENGTH_SHORT).show();

        editText = (EditText)findViewById(R.id.editText);
        textView = (TextView)findViewById(R.id.main_text);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textView.setText(editText.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        mainPIC.setOnTouchListener(new MultiTouchListener());
        mainSTK.setOnTouchListener(new MultiTouchListener());
        textView.setOnTouchListener(new MultiTouchListener());

        BitmapDrawable drawable = (BitmapDrawable) mainPIC.getDrawable();
        bitmap = drawable.getBitmap();
    }

    //region [show edit scrollview]
    public void showListFrame(View v){
        if(frame_sv.getVisibility()==View.VISIBLE){
            frame_sv.setVisibility(View.INVISIBLE);
        } else {
            sticker_sv.setVisibility(View.INVISIBLE);
            filter_sv.setVisibility(View.INVISIBLE);
            text_sv.setVisibility(View.INVISIBLE);
            frame_sv.setVisibility(View.VISIBLE);
        }
    }

    public void showListFilter(View v){
        if(filter_sv.getVisibility()==View.VISIBLE){
            filter_sv.setVisibility(View.INVISIBLE);
        } else {
            sticker_sv.setVisibility(View.INVISIBLE);
            frame_sv.setVisibility(View.INVISIBLE);
            text_sv.setVisibility(View.INVISIBLE);
            filter_sv.setVisibility(View.VISIBLE);
        }
    }

    public void showListText(View v){
        if(text_sv.getVisibility()==View.VISIBLE){
            text_sv.setVisibility(View.INVISIBLE);
        } else {
            frame_sv.setVisibility(View.INVISIBLE);
            filter_sv.setVisibility(View.INVISIBLE);
            sticker_sv.setVisibility(View.INVISIBLE);
            text_sv.setVisibility(View.VISIBLE);
        }
    }

    public void showListSticker(View v){
        if(sticker_sv.getVisibility()==View.VISIBLE){
            sticker_sv.setVisibility(View.INVISIBLE);
        } else {
            frame_sv.setVisibility(View.INVISIBLE);
            filter_sv.setVisibility(View.INVISIBLE);
            text_sv.setVisibility(View.INVISIBLE);
            sticker_sv.setVisibility(View.VISIBLE);
        }
    }


    public void choseFrame(View v){
        switch (v.getId()){
            case (R.id.frame0):
                mainFRM.setImageResource(R.drawable.frame0);
                break;
            case (R.id.frame1):
                mainFRM.setImageResource(R.drawable.frame1);
                break;
            case (R.id.frame2):
                mainFRM.setImageResource(R.drawable.frame2);
                break;
            case (R.id.frame3):
                mainFRM.setImageResource(R.drawable.frame3);
                break;
            case (R.id.frame4):
                mainFRM.setImageResource(R.drawable.frame4);
                break;
            case (R.id.frame5):
                mainFRM.setImageResource(R.drawable.frame5);
                break;
            case (R.id.frame6):
                mainFRM.setImageResource(R.drawable.frame6);
                break;
            case (R.id.frame7):
                mainFRM.setImageResource(R.drawable.frame7);
                break;
            case (R.id.frame8):
                mainFRM.setImageResource(R.drawable.frame8);
                break;
            case (R.id.frame9):
                mainFRM.setImageResource(R.drawable.frame9);
                break;
            default:
                break;
        }

    }

    public void choseSticker(View v){
        switch (v.getId()){
            case (R.id.sticker0):
                mainSTK.setImageResource(R.drawable.emoji_empty);
                mainSTK.setEnabled(false);
                break;
            case (R.id.sticker1):
                mainSTK.setEnabled(true);
                mainSTK.setImageResource(R.drawable.emoji01);
                break;
            case (R.id.sticker2):
                mainSTK.setEnabled(true);
                mainSTK.setImageResource(R.drawable.emoji02);
                break;
            case (R.id.sticker3):
                mainSTK.setEnabled(true);
                mainSTK.setImageResource(R.drawable.emoji03);
                break;
            case (R.id.sticker4):
                mainSTK.setEnabled(true);
                mainSTK.setImageResource(R.drawable.emoji04);
                break;
            case (R.id.sticker5):
                mainSTK.setEnabled(true);
                mainSTK.setImageResource(R.drawable.emoji05);
                break;
            case (R.id.sticker6):
                mainSTK.setEnabled(true);
                mainSTK.setImageResource(R.drawable.emoji06);
                break;
            case (R.id.sticker7):
                mainSTK.setEnabled(true);
                mainSTK.setImageResource(R.drawable.emoji07);
                break;
            case (R.id.sticker8):
                mainSTK.setEnabled(true);
                mainSTK.setImageResource(R.drawable.emoji08);
                break;
            case (R.id.sticker9):
                mainSTK.setEnabled(true);
                mainSTK.setImageResource(R.drawable.emoji09);
                break;
            case (R.id.sticker10):
                mainSTK.setEnabled(true);
                mainSTK.setImageResource(R.drawable.emoji10);
                break;
            case (R.id.sticker11):
                mainSTK.setEnabled(true);
                mainSTK.setImageResource(R.drawable.emoji11);
                break;
            case (R.id.sticker12):
                mainSTK.setEnabled(true);
                mainSTK.setImageResource(R.drawable.emoji12);
                break;
            case (R.id.sticker13):
                mainSTK.setEnabled(true);
                mainSTK.setImageResource(R.drawable.emoji13);
                break;
            default:
                break;
        }
    }

    public void choseFont(View v){
        Typeface font = null;
        switch (v.getId()){
            case R.id.arial:
                font = Typeface.createFromAsset(this.getAssets(),"arial.ttf");
                textView.setTypeface(font);
                break;
            case R.id.bauhs93:
                font = Typeface.createFromAsset(this.getAssets(),"BAUHS93.TTF");
                textView.setTypeface(font);
                break;
            case R.id.broadw:
                font = Typeface.createFromAsset(this.getAssets(),"BROADW.TTF");
                textView.setTypeface(font);
                break;
            case R.id.coopbl:
                font = Typeface.createFromAsset(this.getAssets(),"COOPBL.TTF");
                textView.setTypeface(font);
                break;
            case R.id.showg:
                font = Typeface.createFromAsset(this.getAssets(),"SHOWG.TTF");
                textView.setTypeface(font);
                break;
            case R.id.snap:
                font = Typeface.createFromAsset(this.getAssets(),"SNAP.TTF");
                textView.setTypeface(font);
                break;
            case R.id.stencil:
                font = Typeface.createFromAsset(this.getAssets(),"STENCIL.TTF");
                textView.setTypeface(font);
                break;
            default:
                break;

        }
    }

    public void choseColor(View v){

        switch (v.getId()){
            case R.id.m_ffffff:
                textView.setTextColor(Color.parseColor("#ffffff"));
                break;
            case R.id.m_00ffff:
                textView.setTextColor(Color.parseColor("#00ffff"));
                break;
            case R.id.m_ffff00:
                textView.setTextColor(Color.parseColor("#ffff00"));
                break;
            case R.id.m_ff00ff:
                textView.setTextColor(Color.parseColor("#ff00ff"));
                break;
            case R.id.m_800080:
                textView.setTextColor(Color.parseColor("#800080"));
                break;
            case R.id.m_808000:
                textView.setTextColor(Color.parseColor("#808000"));
                break;
            case R.id.m_008080:
                textView.setTextColor(Color.parseColor("#008080"));
                break;
            case R.id.m_0c0c0c:
                textView.setTextColor(Color.parseColor("#0c0c0c"));
                break;
            case R.id.m_ff0000:
                textView.setTextColor(Color.parseColor("#ff0000"));
                break;
            case R.id.m_00ff00:
                textView.setTextColor(Color.parseColor("#00ff00"));
                break;
            case R.id.m_0000ff:
                textView.setTextColor(Color.parseColor("#0000ff"));
                break;
            case R.id.m_808080:
                textView.setTextColor(Color.parseColor("#808080"));
                break;
            case R.id.m_800000:
                textView.setTextColor(Color.parseColor("#800000"));
                break;
            case R.id.m_008000:
                textView.setTextColor(Color.parseColor("#008000"));
                break;
            case R.id.m_000080:
                textView.setTextColor(Color.parseColor("#000080"));
                break;
            case R.id.m_000000:
                textView.setTextColor(Color.parseColor("#000000"));
                break;
            default:
                break;
        }
    }
    //endregion]
    //region [actionbar function]
    public void galleryPick(View v){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, GALERRY_REQUEST);
    }
    public void cameraPick(View v){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, CAMERA_REQUEST);

//        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }
    private void done(){
        main_edit.setDrawingCacheEnabled(true);
        temp = main_edit.getDrawingCache(true).copy(Bitmap.Config.ARGB_8888, false);
        Drawable d = new BitmapDrawable(getResources(), temp);
        main_edit.setBackground(d);
        mainPIC.setImageResource(R.drawable.emoji_empty);
        mainSTK.setImageResource(R.drawable.emoji_empty);
        textView.setText(null);
        main_edit.setDrawingCacheEnabled(false);
    }
    public void save(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Type name of the image");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //luu vao thu muc
                m_Text = input.getText().toString();
                OutputStream output;
                done();

                //crop bitmap
                BitmapDrawable drawable = (BitmapDrawable) mainFRM.getDrawable();
                frameBmp = drawable.getBitmap();
                bitmap = crop(frameBmp, temp);

                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsolutePath() + "/Famagic");
                dir.mkdir();
                File file = new File(dir, m_Text + ".png");
//                Toast.makeText(MainActivity.this, "SAVED", Toast.LENGTH_SHORT).show();
                try {
                    output = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                    output.flush();
                    output.close();
                    MakeSureFileWasCreatedThenMakeAvabile(file);
//                    Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
                    //reload app
                    finish();
                    startActivity(getIntent());
                } catch (FileNotFoundException e){
//                    Toast.makeText(this, "Picture cannot to gallery", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
//                    Toast.makeText(this, "Picture cannot to gallery", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void MakeSureFileWasCreatedThenMakeAvabile(File file) {
        MediaScannerConnection.scanFile(this,
                new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("ExternalStorage", "Scanned " + path + ":");
                        Log.e("ExternalStorage", "-> uri=" + uri);

                    }
                });

    }
    private Bitmap crop(Bitmap frBmp, Bitmap saveBmp){
        int wF = frBmp.getWidth();
        int hF = frBmp.getHeight();
        int wS = saveBmp.getWidth();
        int hS = saveBmp.getHeight();
        if(wF >= hF){
            float tyleF = hF * 1.000f / wF;
            int hTemp = (int)(tyleF * wS);
            Bitmap resizedbitmap = Bitmap.createBitmap(saveBmp, 0, (int)(hS-hTemp)/2, wS, hTemp);
            return resizedbitmap;
        } else {
            float tyleF = wF * 1.000f / hF;
            int wTemp = (int)(tyleF * hS);
            Bitmap resizedbitmap = Bitmap.createBitmap(saveBmp, (int)(wS-wTemp)/2, 0, wTemp, hS);
            return resizedbitmap;
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
            mainPIC.setImageBitmap(decodeSampledBitmapFromFile(file.getAbsolutePath(), 600, 400));
            mainPIC.setOnTouchListener(new MultiTouchListener());
            BitmapDrawable drawable = (BitmapDrawable) mainPIC.getDrawable();
            bitmap = drawable.getBitmap();
        }
        if(requestCode == GALERRY_REQUEST && resultCode == RESULT_OK){
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            // Get the cursor
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imgDecodableString = cursor.getString(columnIndex);
            cursor.close();
            // Set the Image in ImageView after decoding the String
            mainPIC.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
            mainPIC.setOnTouchListener(new MultiTouchListener());
            BitmapDrawable drawable = (BitmapDrawable) mainPIC.getDrawable();
            bitmap = drawable.getBitmap();
        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_SHORT).show();
        }
    }
    public void done1(View v){
        main_edit.setDrawingCacheEnabled(true);
        mainPIC.setVisibility(View.INVISIBLE);
        temp = main_edit.getDrawingCache(true).copy(Bitmap.Config.ARGB_8888, false);
        mainFRM.setImageBitmap(temp);
        mainPIC.setVisibility(View.VISIBLE);
        mainSTK.setImageResource(R.drawable.emoji_empty);
        textView.setText(null);
        main_edit.setDrawingCacheEnabled(false);
    }
    public void share(View v){
        try{
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
            startActivity(launchIntent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void reload(View v){
        finish();
        startActivity(getIntent());
    }
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // Calculate inSampleSize
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;
        if (height > reqHeight) {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;
        if (expectedWidth > reqWidth) {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }
        options.inSampleSize = inSampleSize;
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }
    //endregion
    //region [Effects Code]
    public void choseEffect(View v){
        switch (v.getId()){
            case R.id.autofix:
                mainPIC.setImageBitmap(bitmap);
                break;
            case R.id.bw:
                mainPIC.setImageBitmap(createBlackAndWhite(bitmap));
                break;
            case R.id.brightness:
                mainPIC.setImageBitmap(changeBitmapContrastBrightness(bitmap, 1, 20));
                break;
            case R.id.contrast:
                mainPIC.setImageBitmap(changeBitmapContrastBrightness(bitmap, 1.5f, 0));
                break;
            case R.id.grayscale:
                mainPIC.setImageBitmap(toGrayscale(bitmap));
                break;
            case R.id.saturate:
                mainPIC.setImageBitmap(changeBitmapColor(bitmap, Color.parseColor("#FF92D3FE")));
                break;
            default:
                break;
        }
    }
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness) {
        ColorMatrix cm = new ColorMatrix(new float[]{
                contrast, 0, 0, 0, brightness,
                0, contrast, 0, 0, brightness,
                0, 0, contrast, 0, brightness,
                0, 0, 0, 1, 0
        });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint();paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);
        return ret;
    }
    private Bitmap changeBitmapColor(Bitmap sourceBitmap, int color) {
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
                sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        p.setColorFilter(filter);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
        return resultBitmap;
    }
    public static Bitmap createBlackAndWhite(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);

                // use 128 as threshold, above -> white, below -> black
                if (gray > 128)
                    gray = 255;
                else
                    gray = 0;
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, gray, gray, gray));
            }
        }
        return bmOut;
    }
    //endregion

    //Hien thong bao khi an nut Back
    @Override
    public void onBackPressed() {
        if(frame_sv.getVisibility() == View.VISIBLE || filter_sv.getVisibility() == View.VISIBLE
                || text_sv.getVisibility() == View.VISIBLE || sticker_sv.getVisibility() == View.VISIBLE){
            sticker_sv.setVisibility(View.INVISIBLE);
            filter_sv.setVisibility(View.INVISIBLE);
            text_sv.setVisibility(View.INVISIBLE);
            frame_sv.setVisibility(View.INVISIBLE);
        }
        super.onBackPressed();
    }
}
