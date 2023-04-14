package fr.insset.ccm.m1.sag.travelogue.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class PermissionsHelper {

    public static boolean hasPermission(Context context, String permission){
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasPermissions(Context context, String[] permissions) {
        if (permissions == null) {
            return false;
        }
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void requestPermissions(Activity activity, String[] permissions, int requestCode){
        String[] permissionArray = getNonGrantedPermissions(activity, permissions);
        if(permissionArray != null){
            ActivityCompat.requestPermissions(activity, permissionArray, requestCode);
        }
    }

    public static void requestPermissions(Fragment fragment, String[] permissions, int requestCode){
        String[] permissionArray = getNonGrantedPermissions(fragment.getContext(), permissions);
        if(permissionArray != null){
            ActivityCompat.requestPermissions(fragment.getActivity(), permissionArray, requestCode);
        }
    }

    private static String[] getNonGrantedPermissions(Context context, String[] permissions) {
        ArrayList<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        if (permissionList.size() > 0) {
            String[] permissionArray = new String[permissionList.size()];
            permissionList.toArray(permissionArray);
            return permissionArray;
        }
        return null;
    }

}