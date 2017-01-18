package com.asiainfo.wheelsurf.wheelsurfexample.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.asiainfo.wheelsurf.wheelsurfexample.R;
import com.asiainfo.wheelsurf.wheelsurfexample.view.LuckyPan;

public class WheelSurfActivity extends Activity {

    private LuckyPan mLuckyPan;
    private ImageView mStartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel_surf);

        mLuckyPan = (LuckyPan) findViewById(R.id.luck_Pan);
        mStartBtn = (ImageView) findViewById(R.id.start_btn);

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLuckyPan.isStart()) {
                    Log.e("WheelSurfActivity", "转转盘可以旋转啦,(=@__@=)哪里？...");

                    mLuckyPan.luckyStart();
                    mStartBtn.setImageResource(R.drawable.pan_stop);

                } else {

                    if (!mLuckyPan.isShouldEnd()) {

                        mLuckyPan.luckyEnd();
                        mStartBtn.setImageResource(R.drawable.pan_start);
                    }
                }
            }
        });

    }
}
