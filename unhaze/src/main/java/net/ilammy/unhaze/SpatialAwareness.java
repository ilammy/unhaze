package net.ilammy.unhaze;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;

public class SpatialAwareness implements SensorEventListener {

    private static final int SAMPLES_PER_SECOND = 60;
    private static final int SAMPLING_PERIOD_US = 1_000_000 / SAMPLES_PER_SECOND;

    private SensorManager m_sensorManager;
    private Sensor m_rotationSensor;

    private GLSurfaceView m_surfaceView;
    private OverlayRenderer m_renderer;

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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        m_renderer.setRotationVector(sensorEvent.values);
        m_surfaceView.requestRender();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
}
