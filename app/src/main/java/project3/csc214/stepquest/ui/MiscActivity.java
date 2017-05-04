package project3.csc214.stepquest.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import project3.csc214.stepquest.R;
import project3.csc214.stepquest.data.CameraActivity;
import project3.csc214.stepquest.data.VideoActivity;

public class MiscActivity extends AppCompatActivity {

    Button mPhoto, mVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misc);

        mPhoto = (Button)findViewById(R.id.aaah_takePhoto);
        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(CameraActivity.newInstance(MiscActivity.this));
            }
        });
        mVideo = (Button)findViewById(R.id.aaah_takeVideo);
        mVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(VideoActivity.newInstance(MiscActivity.this));
            }
        });
    }
}
