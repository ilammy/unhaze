package net.ilammy.unhaze.render;

import android.content.Context;
import android.opengl.GLES20;

import net.ilammy.unhaze.R;
import net.ilammy.unhaze.astro.Star;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

public class StarModel {
    // We render the stars in a dome with its center in origin and given radius.
    // The observer should be in the center or they'd see that heavens are fake.
    private static final float STAR_DOME_RADIUS = 100.0f;
    private static final float STAR_QUAD_RADIUS = 0.25f;

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
    private FloatBuffer vertexList;
    private ShortBuffer drawList;
    private int starCount;

    private final int program;

    public StarModel(Context context) {
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
        return shader;
    }

    public void draw(float[] vpMatrix) {
        GLES20.glUseProgram(this.program);

        int vPosition = GLES20.glGetAttribLocation(this.program, "vPosition");
        int umViewProjection = GLES20.glGetUniformLocation(this.program, "umViewProjection");
        int vColor = GLES20.glGetUniformLocation(this.program, "vColor");

        int vertexCount = 3 * 2 * this.starCount;

        // Prepare position data.
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexList);

        // Set view-projection matrix.
        GLES20.glUniformMatrix4fv(umViewProjection, 1, false, vpMatrix, 0);

        // Set color.
        float[] white = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        GLES20.glUniform4fv(vColor, 1, white, 0);

        // Draw a shitload of triangles.
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, vertexCount, GLES20.GL_UNSIGNED_SHORT, drawList);

        // Unbind the vertex array.
        GLES20.glDisableVertexAttribArray(vPosition);
    }

    public void loadStars(List<Star> stars) {
        this.vertexList = allocateVertexList(stars.size());
        this.drawList = allocateDrawList(stars.size());

        for (Star star : stars) {
            pushStar(star);
        }

        this.vertexList.position(0);
        this.drawList.position(0);

        this.starCount = stars.size();
    }

    private FloatBuffer allocateVertexList(int starCount) {
        int capacity = vertexCapacity(starCount);

        // Reuse existing buffer if possible.
        if (this.vertexList != null && this.vertexList.capacity() >= capacity) {
            this.vertexList.clear();
            return this.vertexList;
        }

        ByteBuffer bbVertexList = ByteBuffer.allocateDirect(capacity);
        bbVertexList.order(ByteOrder.nativeOrder());
        return bbVertexList.asFloatBuffer();
    }

    private static int vertexCapacity(int starCount) {
        // We need 4 points to describe a quad, each point requires 3 coordinates,
        // and each coordinate takes 4 bytes (float).
        return starCount * 4 * 3 * 4;
    }

    private ShortBuffer allocateDrawList(int starCount) {
        int capacity = drawListCapacity(starCount);

        // Reuse existing buffer if possible.
        if (this.drawList != null && this.drawList.capacity() >= capacity) {
            this.drawList.clear();
            return this.drawList;
        }

        ByteBuffer bbVertexList = ByteBuffer.allocateDirect(capacity);
        bbVertexList.order(ByteOrder.nativeOrder());
        return bbVertexList.asShortBuffer();
    }

    private static int drawListCapacity(int starCount) {
        // We need to tell OpenGL the order in which to draw, that's 2 triangles,
        // with 3 points in each, and every point index requires 2 bytes (short).
        // Mind the wind, it must be counterclockwise.
        return starCount * 2 * 3 * 2;
    }

    private void pushStar(Star star) {
        int offset = this.vertexList.position();

        float[] vertices = new float[] {
                +STAR_QUAD_RADIUS, +STAR_QUAD_RADIUS, 0.0f,
                -STAR_QUAD_RADIUS, +STAR_QUAD_RADIUS, 0.0f,
                -STAR_QUAD_RADIUS, -STAR_QUAD_RADIUS, 0.0f,
                +STAR_QUAD_RADIUS, -STAR_QUAD_RADIUS, 0.0f,
        };

        repositionStar(star, vertices);

        this.vertexList.put(vertices);

        short[] drawOrder = new short[] {
                (short) (offset + 0), (short) (offset + 2), (short) (offset + 1),
                (short) (offset + 0), (short) (offset + 3), (short) (offset + 2),
        };
        this.drawList.put(drawOrder);
    }

    private static void repositionStar(Star star, float[] vertices) {
        // TODO: use correct horizontal coordinates instead of equatorial
        double theta = Math.PI / 2 - star.declination;
        double phi = star.rightAscension;

        // The order is important here. First we rotate the quad around Y axis, then around Z axis,
        // and only then move it into correct position. Rotations could be interchanged, but the
        // translation should happen after that.
        rotateAltitude(vertices, theta);
        rotateAzimuth(vertices, phi);
        translatePosition(vertices, theta, phi);
    }

    private static void rotateAltitude(float[] vertices, double theta) {
        for (int i = 0; i < vertices.length / 3; i++) {
            float x = vertices[(3 * i) + 0];
            float z = vertices[(3 * i) + 2];
            vertices[(3 * i) + 0] = (float) (Math.sin(theta) * z + Math.cos(theta) * x);
            vertices[(3 * i) + 2] = (float) (Math.cos(theta) * z - Math.sin(theta) * x);
        }
    }

    private static void rotateAzimuth(float[] vertices, double phi) {
        for (int i = 0; i < vertices.length / 3; i++) {
            float x = vertices[(3 * i) + 0];
            float y = vertices[(3 * i) + 1];
            vertices[(3 * i) + 0] = (float) (Math.cos(phi) * x - Math.sin(phi) * y);
            vertices[(3 * i) + 1] = (float) (Math.sin(phi) * x + Math.cos(phi) * y);
        }
    }

    private static void translatePosition(float[] vertices, double theta, double phi) {
        float x = (float) (STAR_DOME_RADIUS * Math.sin(theta) * Math.cos(phi));
        float y = (float) (STAR_DOME_RADIUS * Math.sin(theta) * Math.sin(phi));
        float z = (float) (STAR_DOME_RADIUS * Math.cos(theta));

        for (int i = 0; i < vertices.length / 3; i++) {
            vertices[(3 * i) + 0] += x;
            vertices[(3 * i) + 1] += y;
            vertices[(3 * i) + 2] += z;
        }
    }
}
