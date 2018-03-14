package net.ilammy.unhaze;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private Camera m_camera;

    public CameraPreview(Context context) {
        super(context);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // Required for Android pre-3.0:
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void useCamera(Camera camera) {
        m_camera = camera;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            m_camera.setPreviewDisplay(surfaceHolder);
            m_camera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "failed to set camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        // Currently we ignore any change events.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        m_camera.stopPreview();
    }
}
