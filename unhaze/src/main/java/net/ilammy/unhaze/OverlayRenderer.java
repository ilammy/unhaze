package net.ilammy.unhaze;

import android.content.Context;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import net.ilammy.unhaze.astro.Stars;
import net.ilammy.unhaze.render.StarModel;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OverlayRenderer implements GLSurfaceView.Renderer {
    private Context context;
    private StarModel stars;
    private float[] m_projectionMatrix = new float[16];
    private float[] m_rotationMatrix = new float[16];
    private float[] m_vpMatrix = new float[16];

    public OverlayRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Prepare the model once we have OpenGL ES context ready.
        this.stars = new StarModel(this.context);

        // TODO: reload on every time change to recompute horizontal coordinates
        this.stars.loadStars(Stars.knownStars());

        Matrix.setIdentityM(m_rotationMatrix, 0);

        // Set fully transparent background.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Enable face culling to not draw backfaces.
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float aspectRatio = (float) width / height;
        float zoom = 50.0f;

        Matrix.frustumM(m_projectionMatrix, 0,
                -aspectRatio * zoom, aspectRatio * zoom,
                -1 * zoom, 1 * zoom,
                80, 240);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        this.stars.draw(m_vpMatrix);
    }

    public void setRotationVector(float[] rotationVector) {
        SensorManager.getRotationMatrixFromVector(m_rotationMatrix, rotationVector);
        Matrix.multiplyMM(m_vpMatrix, 0, m_projectionMatrix, 0, m_rotationMatrix, 0);
    }
}
