package net.ilammy.unhaze;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;

import net.ilammy.unhaze.render.StarModel;

public class OverlaySurfaceView extends GLSurfaceView {

    private final OverlayRenderer m_renderer;

    private SpatialAwareness m_spatialAwareness;

    public OverlaySurfaceView(Activity activity) {
        super(activity);

        // Use OpenGL ES 2.0.
        setEGLContextClientVersion(2);

        // Ensure we're drawn on top and support transparency.
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.RGBA_8888);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        // Set renderer and make sure the view is updated on demand.
        m_renderer = new OverlayRenderer(activity);
        setRenderer(m_renderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);

        // Prepare to track device movements and update the scene accordingly.
        m_spatialAwareness = new SpatialAwareness(activity);
        m_spatialAwareness.setRenderer(m_renderer);
        m_spatialAwareness.setSurfaceView(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        m_spatialAwareness.startTracking();
    }

    @Override
    public void onPause() {
        super.onPause();

        m_spatialAwareness.stopTracking();
    }
}
