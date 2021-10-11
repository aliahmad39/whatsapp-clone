package com.UniqueBulleteSolutions.whatsapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;

public class Permissions {
    public boolean contactResult = false;

    public boolean ContactPermissions(Context context){
//        Dexter.withContext(context).
//                withPermissions(Manifest.permission.CAMERA,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .withListener(new MultiplePermissionsListener() {
//                    @Override
//                    public void onPermissionsChecked(MultiplePermissionsReport
//                                                             multiplePermissionsReport) {
//                        displaySong();
//                    }
//
//                    @Override
//                    public void    onPermissionRationaleShouldBeShown(List<PermissionRequest> list,
//                                                                      PermissionToken permissionToken) {
//
//                        permissionToken.continuePermissionRequest();
//
//                    }
//                }).check();

        Dexter.withActivity((Activity) context)
                .withPermission(Manifest.permission.READ_CONTACTS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        contactResult = true;
                        //      getContact();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        contactResult = false;
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
        return contactResult;
    }


}
