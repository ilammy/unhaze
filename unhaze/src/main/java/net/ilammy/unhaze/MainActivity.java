package net.ilammy.unhaze;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    private Camera m_camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_camera = getCameraInstance();
        if (m_camera == null) {
            return;
        }

        // Make sure the image on the preview is the same as seen by the user's eyes.
        m_camera.setDisplayOrientation(90);

        CameraPreview preview = new CameraPreview(this, m_camera);
        ConstraintLayout layout = findViewById(R.id.constraintLayout);
        layout.addView(preview);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (m_camera != null) {
            m_camera.release();
        }
    }

    private Camera getCameraInstance() {
        try {
            return Camera.open();
        } catch (Exception e) {
            Log.e(TAG, "camera not available: " + e.getMessage());
            return null;
        }
    }
}
