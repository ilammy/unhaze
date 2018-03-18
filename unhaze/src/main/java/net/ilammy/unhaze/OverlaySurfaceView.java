package net.ilammy.unhaze;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;

public class OverlaySurfaceView extends GLSurfaceView {

    private final OverlayRenderer m_renderer;

    public OverlaySurfaceView(Context context) {
        super(context);

        // Use OpenGL ES 2.0.
        setEGLContextClientVersion(2);

        // Ensure we're drawn on top and support transparency.
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.RGBA_8888);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        // Set renderer and make sure the view is updated on demand.
        m_renderer = new OverlayRenderer();
        setRenderer(m_renderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }
}
