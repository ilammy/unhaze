package net.ilammy.unhaze.render;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import net.ilammy.unhaze.R;
import net.ilammy.unhaze.astro.Star;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.List;

public class StarModel {
    // We render the stars in a dome with its center in origin and given radius.
    // The observer should be in the center or they'd see that heavens are fake.
    private static final float STAR_DOME_RADIUS = 100.0f;
    private static final float STAR_QUAD_RADIUS = 1.0f;

    // Every star is unique and still the same. In our world every star is a square (aka quad)
    // which is constructed from two triangles:
    //
    //   1---0
    //   |  /|
    //   | / |
    //   |/  |
    //   2---3
    //
    // Each star's model is the same so we can share the vertex data between them. We only need
    // to modify the model-view matrix so that a star is drawn at its designated place and the
    // quad is facing the camera.
    private final FloatBuffer vertexList;
    private final ShortBuffer drawList;
    private final int program;

    public StarModel(Context context) {
        // We need 4 points to describe a quad, each point requires 3 coordinates,
        // and each coordinate takes 4 bytes (float).
        ByteBuffer bbVertexList = ByteBuffer.allocateDirect(4 * 3 * 4);
        bbVertexList.order(ByteOrder.nativeOrder());
        this.vertexList = bbVertexList.asFloatBuffer();
        this.vertexList.put(new float[] {
                +STAR_QUAD_RADIUS, +STAR_QUAD_RADIUS, 0,
                -STAR_QUAD_RADIUS, +STAR_QUAD_RADIUS, 0,
                -STAR_QUAD_RADIUS, -STAR_QUAD_RADIUS, 0,
                +STAR_QUAD_RADIUS, -STAR_QUAD_RADIUS, 0,
        });
        this.vertexList.position(0);

        // We need to tell OpenGL the order in which to draw, that's 2 triangles,
        // with 3 points in each, and every point index requires 2 bytes (short).
        // Mind the wind, it must be counterclockwise.
        ByteBuffer bbDrawList = ByteBuffer.allocateDirect(2 * 3 * 2);
        bbDrawList.order(ByteOrder.nativeOrder());
        this.drawList = bbDrawList.asShortBuffer();
        this.drawList.put(new short[] {0, 1, 2, 0, 2, 3});
        this.drawList.position(0);

        String fragmentShaderCode = context.getString(R.string.starFragmentShader);
        String vertexShaderCode = context.getString(R.string.starVertexShader);

        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);

        this.program = GLES20.glCreateProgram();
        GLES20.glAttachShader(this.program, fragmentShader);
        GLES20.glAttachShader(this.program, vertexShader);
        GLES20.glLinkProgram(this.program);
    }

    private static int loadShader(int type, String code) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);

        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            String error = GLES20.glGetShaderInfoLog(shader);
            throw new RuntimeException("failed to compile shader: " + error);
        }

        return shader;
    }

    public void draw(List<Star> stars, float[] vMatrix, float[] pMatrix) {
        GLES20.glUseProgram(this.program);

        int vPosition = GLES20.glGetAttribLocation(this.program, "vPosition");
        int umBillboard = GLES20.glGetUniformLocation(this.program, "umBillboard");
        int umModel = GLES20.glGetUniformLocation(this.program, "umModel");
        int umView = GLES20.glGetUniformLocation(this.program, "umView");
        int umProjection = GLES20.glGetUniformLocation(this.program, "umProjection");
        int vColor = GLES20.glGetUniformLocation(this.program, "vColor");

        // Compute the billboard matrix which is inverse of view's rotation.
        // It should negate the camera rotation and make the quads face it.
        float[] billboard = new float[16];
        float[] temp = vMatrix.clone();
        temp[3] = 0.0f;
        temp[7] = 0.0f;
        temp[11] = 0.0f;
        temp[12] = 0.0f;
        temp[13] = 0.0f;
        temp[14] = 0.0f;
        temp[15] = 1.0f;
        Matrix.invertM(billboard, 0, temp, 0);

        // Prepare position data.
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexList);

        // Set fixed billboard, view and projection matrices.
        GLES20.glUniformMatrix4fv(umBillboard, 1, false, billboard, 0);
        GLES20.glUniformMatrix4fv(umView, 1, false, vMatrix, 0);
        GLES20.glUniformMatrix4fv(umProjection, 1, false, pMatrix, 0);

        // Set color.
        float[] white = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        GLES20.glUniform4fv(vColor, 1, white, 0);

        float[] mMatrix = new float[16];
        for (Star star : stars) {
            // TODO: precompute all matrices instead of doing this every frame
            repositionStar(star, mMatrix);

            // Set dynamic model matrix.
            GLES20.glUniformMatrix4fv(umModel, 1, false, mMatrix, 0);

            // Draw a star quad (2 triangles).
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 2 * 3, GLES20.GL_UNSIGNED_SHORT, drawList);
        }

        // Unbind the vertex array.
        GLES20.glDisableVertexAttribArray(vPosition);
    }

    private static void repositionStar(Star star, float[] mMatrix) {
        // TODO: use correct horizontal coordinates
        double theta = Math.PI / 2 - star.declination;
        double phi = star.rightAscension;
        float x = (float) (STAR_DOME_RADIUS * Math.sin(theta) * Math.cos(phi));
        float y = (float) (STAR_DOME_RADIUS * Math.sin(theta) * Math.sin(phi));
        float z = (float) (STAR_DOME_RADIUS * Math.cos(theta));

        Matrix.setIdentityM(mMatrix, 0);
        Matrix.translateM(mMatrix, 0, x, y, z);
    }
}
