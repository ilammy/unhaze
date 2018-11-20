package net.ilammy.unhaze;

import android.app.Activity;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;

import net.ilammy.unhaze.astro.Star;
import net.ilammy.unhaze.astro.Stars;

public class MainActivity extends Activity {

    private OverlaySurfaceView m_overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_overlay = new OverlaySurfaceView(this);

        ConstraintLayout layout = findViewById(R.id.constraintLayout);
        layout.addView(m_overlay);

        Stars.loadStars(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        m_overlay.onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();

        m_overlay.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        m_overlay.onPause();
    }
}
