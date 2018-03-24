package net.ilammy.unhaze;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;

public class SpatialAwareness implements SensorEventListener {

    private static final int SAMPLES_PER_SECOND = 120;
    private static final int SAMPLING_PERIOD_US = 1_000_000 / SAMPLES_PER_SECOND;
    // (0.0, 1.0) range where 0.0 is no updates at all, 1.0 is no smoothing at all.
    private static final float SMOOTH_FACTOR = 0.75f;
    private SensorManager m_sensorManager;
    private Sensor m_rotationSensor;
    private GLSurfaceView m_surfaceView;
    private OverlayRenderer m_renderer;
    private float[] m_rotationVector;

    public SpatialAwareness(Activity activity) {
        m_sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        m_rotationSensor = m_sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    public void setSurfaceView(GLSurfaceView view) {
        m_surfaceView = view;
    }

    public void setRenderer(OverlayRenderer renderer) {
        m_renderer = renderer;
    }

    public void startTracking() {
        m_sensorManager.registerListener(this, m_rotationSensor, SAMPLING_PERIOD_US);
    }

    public void stopTracking() {
        m_sensorManager.unregisterListener(this);
    }

    private void updateRotationVector(float[] newValues) {
        // Smooth the readings as the rotation sensor is kinda noisy.
        if (m_rotationVector == null) {
            m_rotationVector = newValues;
        } else {
            for (int i = 0; i < m_rotationVector.length; i++) {
                m_rotationVector[i] += SMOOTH_FACTOR * (newValues[i] - m_rotationVector[i]);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        updateRotationVector(sensorEvent.values);

        m_renderer.setRotationVector(m_rotationVector);
        m_surfaceView.requestRender();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
}
