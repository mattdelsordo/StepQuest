package project3.csc214.stepquest.data;

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


/**
 * This Activity exists to take photos
 */

public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 6;
    private static final int REQUEST_CAMERA_PERMISSIONS = 5;
    private static final String TAG = "CameraActivity";
    public static final String ARG_URI = "arg_uri";
    private Uri mPhotoUri;

    public static Intent newInstance(AppCompatActivity activity){
        Intent intent = new Intent(activity, CameraActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        takeAPhoto();
    }

    //handles code for taking a photo
    //TODO: make this actually work, goddammit
    public void takeAPhoto(){
        //request permissions
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            //take photo
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //intent.setAction();

            //make filename
            String filename = "IMG_" + UUID.randomUUID().toString() + ".jpg";
            //make file
            File picturesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File photoFile = new File(picturesDir, filename);

            mPhotoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", photoFile);
            //Uri.fromFile(photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);

            Log.d(TAG, "photo location: " + photoFile.toString());

            if(intent.resolveActivity(getPackageManager()) != null){
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }

        }else{
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CAMERA_PERMISSIONS){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                takeAPhoto();
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
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
//            Bundle extras = data.getExtras();
//            Bitmap bit = (Bitmap)extras.get("data");
//            mPhoto.setImageBitmap(bit);
            //mPhoto.setImageResource(R.mipmap.ic_photo_confirmed);
            Intent intent = new Intent();
            intent.putExtra(ARG_URI, mPhotoUri.toString());
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}
