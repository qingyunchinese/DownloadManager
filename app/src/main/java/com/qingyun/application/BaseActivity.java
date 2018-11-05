package com.qingyun.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.MemoryCategory;

/**
 * Created by qingyun1 on 16/11/22.
 */

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageGlide.get(this).setMemoryCategory(MemoryCategory.HIGH);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void openActivity(Class<?> targetClass) {
        openActivity(targetClass,null);
    }

    public void openActivity(Class<?> targetClass,Bundle bundle) {
        Intent it = new Intent(this,targetClass);
        if (bundle!= null) {
            it.putExtras(bundle);
        }
        startActivity(it);
    }

}
