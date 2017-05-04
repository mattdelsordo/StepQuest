package project3.csc214.stepquest.data;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.UUID;

import project3.csc214.stepquest.R;

public class VideoActivity extends AppCompatActivity {
    private static final int REQUEST_VIDEO_CAPTURE = 7;
    private static final int REQUEST_CAMERA_PERMISSIONS = 5;
    private static final String TAG = "VideoActivity";
    public static final String ARG_URI = "arg_uri";
    private Uri mVideoUri;

    public static Intent newInstance(AppCompatActivity activity){
        Intent intent = new Intent(activity, VideoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        takeAVideo();
    }

    //handles code for taking a photo
    //TODO: make this actually work, goddammit
    public void takeAVideo(){
        //request permissions
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            //take photo
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            //intent.setAction();

            //make filename
            String filename = "IMG_" + UUID.randomUUID().toString() + ".jpg";
            //make file
            File picturesDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            File photoFile = new File(picturesDir, filename);

            mVideoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", photoFile);
            //Uri.fromFile(photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mVideoUri);

            Log.d(TAG, "photo location: " + photoFile.toString());

            if(intent.resolveActivity(getPackageManager()) != null){
                startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
            }

        }else{
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, REQUEST_CAMERA_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CAMERA_PERMISSIONS){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                takeAVideo();
            }else{
                //Toast.makeText(this, grantResults.length +  " " + grantResults[0],Toast.LENGTH_LONG).show();
                Toast.makeText(this, getString(R.string.camera_denied),Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_CANCELED);
                finish();
            }

        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult()");
        if(requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK){
//            Bundle extras = data.getExtras();
//            Bitmap bit = (Bitmap)extras.get("data");
//            mPhoto.setImageBitmap(bit);
            //mPhoto.setImageResource(R.mipmap.ic_photo_confirmed);
            Intent intent = new Intent();
            intent.putExtra(ARG_URI, mVideoUri.toString());
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}
