package net.ilammy.unhaze.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import net.ilammy.unhaze.R;
import net.ilammy.unhaze.astro.Star;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.List;

public class StarModel {
    // We render the stars in a dome with its center in origin and given radius.
    // The observer should be in the center or they'd see that heavens are fake.
    private static final float STAR_DOME_RADIUS = 100.0f;
    private static final float STAR_QUAD_RADIUS_6 = 0.1f;
    private static final float STAR_QUAD_RADIUS_1 = 0.5f;

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
    private FloatBuffer colorList;
    private FloatBuffer vertexList;
    private FloatBuffer textureList;
    private ShortBuffer drawList;
    private int starCount;

    private final int starTexture;
    private final int program;

    public StarModel(Context context) {
        this.starTexture = loadTexture(context, R.drawable.star_texture);

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
        checkShader(shader);
        return shader;
    }

    private static void checkShader(int shader) {
        int[] status = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);

        if (status[0] == 0) {
            String error = GLES20.glGetShaderInfoLog(shader);
            GLES20.glDeleteShader(shader);
            throw new RuntimeException("failed to compile shader: " + error);
        }
    }

    private static int loadTexture(Context context, int resourceId) {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        readTexture(texture, context, resourceId);
        checkTexture(texture);
        return texture[0];
    }

    private static void readTexture(int[] texture, Context context, int resourceId) {
        // Don't prescale anything, please.
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);

        // Set scale filtering for nicer interpolation.
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // The bitmap has been copied to video memory. Free up conventional memory right now.
        bitmap.recycle();
    }

    private static void checkTexture(int[] texture) {
        if (texture[0] == 0) {
            GLES20.glDeleteTextures(1, texture, 0);
            throw new RuntimeException("failed to load texture");
        }
    }

    public void draw(float[] vpMatrix) {
        GLES20.glUseProgram(this.program);

        int umViewProjection = GLES20.glGetUniformLocation(this.program, "umViewProjection");
        int utTexture = GLES20.glGetUniformLocation(this.program, "utTexture");

        int avPosition = GLES20.glGetAttribLocation(this.program, "avPosition");
        int avTexture = GLES20.glGetAttribLocation(this.program, "avTexture");
        int avColor = GLES20.glGetAttribLocation(this.program, "avColor");

        int vertexCount = 3 * 2 * this.starCount;

        // Set fixed view-projection matrix.
        GLES20.glUniformMatrix4fv(umViewProjection, 1, false, vpMatrix, 0);

        // Bind the star texture.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.starTexture);
        GLES20.glUniform1i(utTexture, 0);

        // Bind per-vertex attribute arrays.
        GLES20.glEnableVertexAttribArray(avPosition);
        GLES20.glEnableVertexAttribArray(avTexture);
        GLES20.glEnableVertexAttribArray(avColor);

        // Set per-vertex attributes.
        GLES20.glVertexAttribPointer(avPosition, 3, GLES20.GL_FLOAT, false, 3 * 4, this.vertexList);
        GLES20.glVertexAttribPointer(avTexture, 2, GLES20.GL_FLOAT, false, 2 * 4, this.textureList);
        GLES20.glVertexAttribPointer(avColor, 3, GLES20.GL_FLOAT, false, 3 * 4, this.colorList);

        // Draw a shitload of triangles.
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, vertexCount, GLES20.GL_UNSIGNED_SHORT, drawList);

        // Unbind the attribute arrays.
        GLES20.glDisableVertexAttribArray(avPosition);
        GLES20.glDisableVertexAttribArray(avTexture);
        GLES20.glDisableVertexAttribArray(avColor);
    }

    public void loadStars(List<Star> stars) {
        this.colorList = allocate(colorCapacity(stars.size())).asFloatBuffer();
        this.vertexList = allocate(vertexCapacity(stars.size())).asFloatBuffer();
        this.textureList = allocate(textureCapacity(stars.size())).asFloatBuffer();
        this.drawList = allocate(drawListCapacity(stars.size())).asShortBuffer();

        for (int i = 0; i < stars.size(); i++) {
            pushStar(stars.get(i), i);
        }

        this.colorList.position(0);
        this.vertexList.position(0);
        this.textureList.position(0);
        this.drawList.position(0);

        this.starCount = stars.size();
    }

    private static ByteBuffer allocate(int capacity) {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }

    private static int colorCapacity(int starCount) {
        // We need 4 points to describe a quad, each point requires 3 color components,
        // and each component takes 4 bytes (float).
        return starCount * 4 * 3 * 4;
    }

    private static int vertexCapacity(int starCount) {
        // We need 4 points to describe a quad, each point requires 3 coordinates,
        // and each coordinate takes 4 bytes (float).
        return starCount * 4 * 3 * 4;
    }

    private static int textureCapacity(int starCount) {
        // We need 4 points to describe a quad, each point requires 2 texture coordinates,
        // and each coordinate takes 4 bytes (float).
        return starCount * 4 * 2 * 4;
    }

    private static int drawListCapacity(int starCount) {
        // We need to tell OpenGL the order in which to draw, that's 2 triangles,
        // with 3 points in each, and every point index requires 2 bytes (short).
        // Mind the wind, it must be counterclockwise.
        return starCount * 2 * 3 * 2;
    }

    private void pushStar(Star star, int starIndex) {
        pushStarVertexData(star);
        pushStarTextureData();
        pushStarDrawOrder(starIndex);
        pushStarColorData(star);
    }

    private void pushStarVertexData(Star star) {
        float radius = radiusForMagnitude(star.magnitude);
        float[] vertices = new float[] {
                +radius, +radius, 0.0f,
                -radius, +radius, 0.0f,
                -radius, -radius, 0.0f,
                +radius, -radius, 0.0f,
        };
        repositionStar(star, vertices);
        this.vertexList.put(vertices);
    }

    // The coordinates are rotated by 90 degrees counterclockwise to keep the image upright.
    private static final float[] STAR_TEXTURE_COORDS = new float[] {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    private void pushStarTextureData() {
        this.textureList.put(STAR_TEXTURE_COORDS);
    }

    private void pushStarDrawOrder(int starIndex) {
        int offset = starIndex * 4;
        short[] drawOrder = new short[] {
                (short) (offset + 0), (short) (offset + 2), (short) (offset + 1),
                (short) (offset + 0), (short) (offset + 3), (short) (offset + 2),
        };
        this.drawList.put(drawOrder);
    }

    private static final HashMap<String, float[]> SPECTRAL_COLORS = new HashMap<>();
    static {
        SPECTRAL_COLORS.put("O", rgb2openglColor("9BB0FF")); // blue supergiants
        SPECTRAL_COLORS.put("B", rgb2openglColor("AABFFF")); // blue giants
        SPECTRAL_COLORS.put("A", rgb2openglColor("CAD7FF")); // blue main-sequence stars
        SPECTRAL_COLORS.put("F", rgb2openglColor("F8F7FF")); // white main-sequence stars
        SPECTRAL_COLORS.put("G", rgb2openglColor("FFF4EA")); // yellow main-sequence stars
        SPECTRAL_COLORS.put("K", rgb2openglColor("FFDAB5")); // orange main-sequence stars
        SPECTRAL_COLORS.put("M", rgb2openglColor("FFCC6F")); // red giants
        // TODO: extended star colors
        SPECTRAL_COLORS.put("W", rgb2openglColor("FFFFFF")); // Wolf-Rayet stars
        SPECTRAL_COLORS.put("S", rgb2openglColor("FFFFFF")); // S-type stars ;)
        SPECTRAL_COLORS.put("C", rgb2openglColor("FF8080")); // carbon giants
        SPECTRAL_COLORS.put("N", rgb2openglColor("FFFFFF")); // carbon giants (too)
        SPECTRAL_COLORS.put("R", rgb2openglColor("FFFFFF")); // carbon giants (too)
        SPECTRAL_COLORS.put("D", rgb2openglColor("FFFFFF")); // white dwarfs (degenerates)
        SPECTRAL_COLORS.put("L", rgb2openglColor("FFFFFF")); // red dwarfs
        SPECTRAL_COLORS.put("T", rgb2openglColor("FFFFFF")); // methane dwarfs
        SPECTRAL_COLORS.put("Y", rgb2openglColor("FFFFFF")); // brown dwarfs
    }

    private static float[] rgb2openglColor(String hex) {
        float r = ((float) Integer.parseInt(hex.substring(0, 2), 16)) / 256.0f;
        float g = ((float) Integer.parseInt(hex.substring(2, 4), 16)) / 256.0f;
        float b = ((float) Integer.parseInt(hex.substring(4, 6), 16)) / 256.0f;
        return new float[] { r, g, b};
    }

    private static float[] colorForSpectralClass(String spectralClass) {
        // Dunno, let's default to the Sun's class.
        if (spectralClass == null || spectralClass.isEmpty()) {
            spectralClass = "G";
        }
        // Skip class modifiers if any (like in "dG0").
        int offset = 0;
        for (; offset < spectralClass.length(); offset++) {
            if (Character.isUpperCase(spectralClass.charAt(offset))) {
                break;
            }
        }
        // Only the class itself has meaningful impact on the visible color.
        // Subdivisions are not that important.
        spectralClass = spectralClass.substring(offset, offset + 1);

        return SPECTRAL_COLORS.get(spectralClass);
    }

    private void pushStarColorData(Star star) {
        float[] color = colorForSpectralClass(star.spectralClass);
        for (int i = 0; i < 4; i++) {
            this.colorList.put(color);
        }
    }

    private static float radiusForMagnitude(double magnitude) {
        // Keep it simple. Assign STAR_QUAD_RADIUS_1 to stars with m = 1.0 and
        // STAR_QUAD_RADIUS_6 to m = 6.0, then interpolate linearly between those.
        return (float) ((6.0 - magnitude) / (6.0 - 1.0) * (STAR_QUAD_RADIUS_1 - STAR_QUAD_RADIUS_6)
                + STAR_QUAD_RADIUS_6);
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
