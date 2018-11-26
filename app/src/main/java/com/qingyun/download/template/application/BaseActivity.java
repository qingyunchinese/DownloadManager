package com.qingyun.download.template.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.MemoryCategory;
import com.qingyun.download.template.manager.PermissionManager;

/**
 * Created by qingyun1 on 16/11/22.
 */

public class BaseActivity extends AppCompatActivity
{

    private PermissionManager mPermissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ImageGlide.get(this).setMemoryCategory(MemoryCategory.HIGH);
        mPermissionManager = new PermissionManager();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    public void openActivity(Class<?> targetClass)
    {
        openActivity(targetClass, null);
    }

    public void openActivity(Class<?> targetClass, Bundle bundle)
    {
        Intent it = new Intent(this, targetClass);
        if (bundle != null)
        {
            it.putExtras(bundle);
        }
        startActivity(it);
    }

    public void requestAppPermissions(String[] requestPermissions, int requestCode)
    {
        mPermissionManager.requestPermissions(this, requestPermissions, requestCode);
    }

    protected boolean checkAppPermissions(String[] requestPermissions)
    {
        return mPermissionManager.checkPermissions(this, requestPermissions);
    }

    public void handleAppPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (mPermissionManager.handlePermissionsResult(requestCode, permissions, grantResults))
        {
            onRequestPermissionSuccess();
        }
        else
        {
            onRequestPermissionFailure();
        }
    }

    protected void onRequestPermissionSuccess()
    {

    }

    protected void onRequestPermissionFailure()
    {

    }
}
