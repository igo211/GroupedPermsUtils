package com.zero211.groupedpermsutils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by malloys on 8/8/2017.
 */

public class PermsGroup
{
    private static final String LOGTAG = PermsGroup.class.getSimpleName();

    private String name;
    private int requestCode;
    private String explaination;
    private String[] permissions;

    public static PermsGroup getByRequestCode(PermsGroup[] permsGroups, int requestCode)
    {
        for (PermsGroup tPermGroup : permsGroups)
        {
            if (tPermGroup.requestCode == requestCode)
            {
                return tPermGroup;
            }
        }

        return null;
    }

    public PermsGroup(String name, String explaination, int requestCode, String... permissions)
    {
        this.name = name;
        this.explaination = explaination;
        this.requestCode = requestCode;
        this.permissions = permissions;
    }


    public String getName()
    {
        return name;
    }

    public String getExplaination()
    {
        return explaination;
    }

    public int getRequestCode()
    {
        return requestCode;
    }

    public String[] getPermissions()
    {
        return permissions;
    }

    private String getPermissionsAsString()
    {
        StringBuilder sb = new StringBuilder();
        for (String tPermission : permissions)
        {
            if (sb.length() > 0)
            {
                sb.append(", ");
            }
            sb.append(tPermission);
        }
        return sb.toString();
    }


    private boolean isAllPermsGranted(Activity activity)
    {
        boolean allGood = true;

        for (String tDangerousPerm : this.permissions)
        {
            int tGrantResult = ActivityCompat.checkSelfPermission(activity, tDangerousPerm);
            if (tGrantResult != PackageManager.PERMISSION_GRANTED)
            {
                allGood = false;
            }
        }

        return allGood;
    }

    public boolean canDoActionRequiringPerms(Activity activity)
    {
        return this.canDoActionRequiringPerms(activity, true);
    }

    private boolean canDoActionRequiringPerms(Activity activity, boolean requestPerms)
    {
        Log.i(LOGTAG, "Checking permissions: " + this.getPermissionsAsString());

        // Verify that all required contact permissions have been granted.
        boolean allPermsGranted = isAllPermsGranted(activity);
        if (!allPermsGranted)
        {
            // Contacts permissions have not been granted.
            Log.i(LOGTAG, "Permissions have NOT been granted yet.");
            if (requestPerms)
            {
                Log.i(LOGTAG, "Requesting permissions.");
                requestPermissions(activity);
            }

            return false;
        }
        else
        {

            // Contact permissions have been granted. Show the contacts fragment.
            Log.i(LOGTAG, "Permissions have already been granted.");
            return true;
        }
    }

    private void requestPermissions(final Activity activity)
    {
        boolean shouldShowRationale = false;

        for (String tDangerousPerm : this.permissions)
        {
            boolean tShouldShow = ActivityCompat.shouldShowRequestPermissionRationale(activity, tDangerousPerm);
            if (tShouldShow)
            {
                shouldShowRationale = true;
                break;
            }
        }

        if (shouldShowRationale)
        {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i(LOGTAG, "Displaying contacts permission rationale to provide additional context.");

            AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setTitle("Permission explaination");
            alertDialog.setMessage("The following permissions are required to " + PermsGroup.this.explaination + "... ");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                            ActivityCompat.requestPermissions(activity, PermsGroup.this.permissions, PermsGroup.this.requestCode);
                        }
                    });
            alertDialog.show();
        }
        else
        {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(activity, this.permissions, this.requestCode);
        }


    }


}
