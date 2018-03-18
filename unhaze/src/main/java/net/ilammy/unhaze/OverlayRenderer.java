package net.ilammy.unhaze;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OverlayRenderer implements GLSurfaceView.Renderer {

    private Box m_box;

    private float[] m_mvpMatrix = new float[4 * 4];
    private float[] m_projectionMatrix = new float[4 * 4];
    private float[] m_viewMatrix = new float[4 * 4];

    public static int loadShader(int type, String code) {
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);

        return shader;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        m_box = new Box();

        // Set fully transparent background.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float aspectRatio = (float) width / height;

        Matrix.frustumM(m_projectionMatrix, 0, -aspectRatio, aspectRatio, -1, 1, 3, 9);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        Matrix.setLookAtM(m_viewMatrix, 0, 0, 0, -3, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

        Matrix.multiplyMM(m_mvpMatrix, 0, m_projectionMatrix, 0, m_viewMatrix, 0);

        m_box.draw(m_mvpMatrix);
    }
}
