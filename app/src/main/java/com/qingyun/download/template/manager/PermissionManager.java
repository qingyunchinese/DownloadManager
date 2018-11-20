package com.qingyun.download.template.manager;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionManager
{
    public PermissionManager()
    {

    }

    public boolean checkPermissions(Activity activity, String[] requestPermissions)
    {
        for (String permission : requestPermissions)
        {
            if (!checkSinglePermission(activity, permission))
            {
                return false;
            }
        }
        return true;
    }

    public boolean checkSinglePermission(Activity activity, String requestPermission)
    {
        try
        {
            return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(activity, requestPermission);
        }
        catch (Throwable e)
        {
            return false;
        }
    }


    public void requestPermissions(Activity activity, String[] requestPermissions, int requestCode)
    {
        ActivityCompat.requestPermissions(activity, requestPermissions, requestCode);
    }

    public void requestSinglePermission(Activity activity, String requestPermission, int requestCode)
    {
        String[] requestPermissionArray = {requestPermission};
        ActivityCompat.requestPermissions(activity, requestPermissionArray, requestCode);
    }

    public boolean handlePermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (verifyPermissions(permissions,grantResults))
        {
            return true;
        }
        return false;
    }

    public boolean verifyPermissions(String[] permissions, int[] grantResults)
    {
        if (grantResults == null || grantResults.length == 0)
        {
            return false;
        }

        boolean result = true;

        for (int grantResult : grantResults)
        {
            if (grantResult != PackageManager.PERMISSION_GRANTED)
            {
                result = false;
                break;
            }
        }
        return result;
    }
}
