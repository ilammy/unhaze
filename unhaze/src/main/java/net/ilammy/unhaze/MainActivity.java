package net.ilammy.unhaze;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.Surface;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    private Camera m_camera;
    private CameraPreview m_preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_preview = new CameraPreview(this);

        ConstraintLayout layout = findViewById(R.id.constraintLayout);
        layout.addView(m_preview);
    }

    @Override
    protected void onStart() {
        super.onStart();

        m_camera = getCameraInstance();
        if (m_camera == null) {
            return;
        }
        fixCameraOrientation();

        m_preview.useCamera(m_camera);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Release the camera when we are no longer in the foreground.
        // This is important to let other applications use the camera.
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

    private void fixCameraOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int orientation = (info.orientation - degrees + 360) % 360;

        m_camera.setDisplayOrientation(orientation);
    }
}
