package net.ilammy.unhaze;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;

import java.io.IOException;

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

        // Release the camera for other activities while we're in background.
        if (m_camera != null) {
            m_camera.unlock();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reconnect to the camera when we are moved into foreground.
        if (m_camera != null) {
            try {
                m_camera.reconnect();
            } catch (IOException e) {
                Log.e(TAG, "failed to reconnect to the camera: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Release the camere when we are destroyed. This is important.
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
