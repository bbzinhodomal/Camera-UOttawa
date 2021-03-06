package nigelhenshaw.com.cameraintenttutorial;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.SynchronousQueue;


public class CamaraIntentActivity extends Activity {

    private static final int ACTIVITY_START_CAMERA_APP = 0;
    private ImageView mPhotoCapturedImageView;
    private String mImageFileLocation = "";
    private String GALLERY_LOCATION = "image gallery";
    private File mGalleryFolder;
    private String option;
    private Spinner spinner;
    private RecyclerView mRecyclerView;
    private TextView textview;
    private String strMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara_intent);

        createImageGallery();

        Log.d("aaa","xxx");


        ArrayAdapter<CharSequence> adapter;
        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.search_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camara_intent, menu);
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

    public void takePhoto(View view) throws IOException {
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
           photoFile = createImageFile();

        } catch (IOException e) {

            e.printStackTrace();
        }
        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        startActivityForResult(callCameraApplicationIntent, ACTIVITY_START_CAMERA_APP);
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK) {
            setContentView(R.layout.waiting);
            textview = (TextView) findViewById(R.id.editText);

            try {
                UploadAndMove();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void UploadAndMove() throws IOException {
        if(mImageFileLocation != ""){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {

                        Person p = new Person();
                        option = spinner.getSelectedItem().toString();
                        String name = p.getName(mImageFileLocation);

                        if(!name.isEmpty() && name.charAt(0) != '@') {
                            String url = p.getUrlApi(option);
                            goToUrl(url);
                        }else{
                            strMessage = "erro";
                            if(name.equals("@1")){
                                strMessage = "Cant find face";
                            }
                            else if(name.equals("@2")){
                                strMessage = "Cant find someone with that face";
                            }
                            runOnUiThread(new Runnable(){
                                @Override
                                public void run() {
                                    textview.setText(strMessage);
                                }
                            });
                        }

                    } catch (IOException e) {
                        //TODO: better error handling when image uploading fails
                        e.printStackTrace();
                    }
                }
            };

            new Thread(runnable).start();

        }else{
            Log.e("a","erro");
        }
    }

    private void goToUrl(String url){
        Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( url ) );
        startActivity( browse );
    }

    private void createImageGallery() {
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        mGalleryFolder = new File(storageDirectory, GALLERY_LOCATION);
        if(!mGalleryFolder.exists()) {
            mGalleryFolder.mkdirs();
        }

    }

    File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";

        File image = File.createTempFile(imageFileName,".jpg", mGalleryFolder);
        mImageFileLocation = image.getAbsolutePath();

        return image;

    }

    void setReducedImageSize() {
        int targetImageViewWidth = mPhotoCapturedImageView.getWidth();
        int targetImageViewHeight = mPhotoCapturedImageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
        int cameraImageWidth = bmOptions.outWidth;
        int cameraImageHeight = bmOptions.outHeight;

        int scaleFactor = Math.min(cameraImageWidth/targetImageViewWidth, cameraImageHeight/targetImageViewHeight);
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;

        Bitmap photoReducedSizeBitmp = BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
        mPhotoCapturedImageView.setImageBitmap(photoReducedSizeBitmp);


    }
}
