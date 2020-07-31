package com.example.solveit;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.List;

public class AppCategoryHelper {
    public static List<ResolveInfo> getPackagesOfDialerApps(Context context){

        List<ResolveInfo> packages = new ArrayList<>();

        // Declare action which target application listen to initiate phone call
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        // Query for all those applications
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
        // Read package name of all those applications
        for(ResolveInfo resolveInfo : resolveInfos){
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            packages.add(resolveInfo);
        }

        return packages;
    }
}
