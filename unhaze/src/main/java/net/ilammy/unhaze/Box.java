package net.ilammy.unhaze;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Box {

    private static final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";
    private static final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    private static float boxCoordinates[] = {
            // top face
            +0.5f, +0.5f, +0.5f, // I
            -0.5f, +0.5f, +0.5f, // II
            -0.5f, -0.5f, +0.5f, // III
            +0.5f, -0.5f, +0.5f, // IV
            // bottom face
            +0.5f, +0.5f, -0.5f, // I
            -0.5f, +0.5f, -0.5f, // II
            -0.5f, -0.5f, -0.5f, // III
            +0.5f, -0.5f, -0.5f, // IV
    };
    private static short drawOrder[][] = {
            {0, 1, 3, 1, 2, 3}, // top face
            {0, 4, 1, 4, 5, 1}, // northern face
            {0, 3, 7, 7, 4, 0}, // eastern face
            {2, 1, 6, 1, 5, 6}, // western face
            {3, 2, 6, 6, 7, 3}, // southern face
            {5, 4, 7, 7, 6, 5}, // bottom face
    };
    private static float colors[] = {
            1.00f, 1.00f, 1.00f, 1.0f, // top face
            0.00f, 0.00f, 1.00f, 1.0f, // northern face
            0.65f, 0.65f, 0.35f, 1.0f, // eastern face
            0.00f, 1.00f, 0.00f, 1.0f, // western face
            1.00f, 0.00f, 0.00f, 1.0f, // southern face
            0.00f, 0.00f, 0.00f, 1.0f, // bottom face
    };
    private FloatBuffer m_vertices;
    private ShortBuffer m_drawLists[];
    private int m_program;

    public Box() {
        ByteBuffer bbVertices = ByteBuffer.allocateDirect(boxCoordinates.length * 4);
        bbVertices.order(ByteOrder.nativeOrder());
        m_vertices = bbVertices.asFloatBuffer();
        m_vertices.put(boxCoordinates);
        m_vertices.position(0);

        m_drawLists = new ShortBuffer[drawOrder.length];
        for (int i = 0; i < drawOrder.length; i++) {
            short[] faceOrder = drawOrder[i];

            ByteBuffer bbDrawList = ByteBuffer.allocateDirect(faceOrder.length * 2);
            bbDrawList.order(ByteOrder.nativeOrder());

            ShortBuffer drawList = bbDrawList.asShortBuffer();
            drawList.put(faceOrder);
            drawList.position(0);

            m_drawLists[i] = drawList;
        }

        int vertexShader = OverlayRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = OverlayRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        m_program = GLES20.glCreateProgram();
        GLES20.glAttachShader(m_program, vertexShader);
        GLES20.glAttachShader(m_program, fragmentShader);
        GLES20.glLinkProgram(m_program);
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(m_program);

        int vPosition = GLES20.glGetAttribLocation(m_program, "vPosition");
        int vColor = GLES20.glGetUniformLocation(m_program, "vColor");
        int uMVPMatrix = GLES20.glGetUniformLocation(m_program, "uMVPMatrix");

        // Prepare position data.
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 3 * 4, m_vertices);

        // Set view-projection matrix.
        GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, mvpMatrix, 0);

        // Draw box faces.
        for (int i = 0; i < m_drawLists.length; i++) {
            // Set face color.
            int colorOffset = i * 4;
            GLES20.glUniform4fv(vColor, 1, colors, colorOffset);

            // Draw a face (as two triangles).
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 2 * 3, GLES20.GL_UNSIGNED_SHORT, m_drawLists[i]);
        }

        // Unbind the vertex array.
        GLES20.glDisableVertexAttribArray(vPosition);
    }
}
