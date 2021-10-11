package com.UniqueBulleteSolutions.whatsapp.utils;

public class PermissionsManager {
//    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
//
//    private void insertDummyContactWrapper() {
//        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_CONTACTS);
//        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[] {Manifest.permission.WRITE_CONTACTS},
//                    REQUEST_CODE_ASK_PERMISSIONS);
//            return;
//        }
//        insertDummyContact();
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_CODE_ASK_PERMISSIONS:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Permission Granted
//                    insertDummyContact();
//                } else {
//                    // Permission Denied
//                    Toast.makeText(MainActivity.this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT)
//                            .show();
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }
//
//    Asking for multiple permissions at a time
//    There is definitely some feature that requires more than one permission. You could request for multiple permissions at a time with same method as above. Anyway don't forget to check the 'Never ask again' case for every single permission as well.
//
//    Here is the revised code.
//
//    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
//
//    private void insertDummyContactWrapper() {
//        List<String> permissionsNeeded = new ArrayList<String>();
//
//        final List<String> permissionsList = new ArrayList<String>();
//        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
//            permissionsNeeded.add("GPS");
//        if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
//            permissionsNeeded.add("Read Contacts");
//        if (!addPermission(permissionsList, Manifest.permission.WRITE_CONTACTS))
//            permissionsNeeded.add("Write Contacts");
//
//        if (permissionsList.size() > 0) {
//            if (permissionsNeeded.size() > 0) {
//                // Need Rationale
//                String message = "You need to grant access to " + permissionsNeeded.get(0);
//                for (int i = 1; i < permissionsNeeded.size(); i++)
//                    message = message + ", " + permissionsNeeded.get(i);
//                showMessageOKCancel(message,
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
//                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
//                            }
//                        });
//                return;
//            }
//            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
//                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
//            return;
//        }
//
//        insertDummyContact();
//    }
//
//    private boolean addPermission(List<String> permissionsList, String permission) {
//        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
//            permissionsList.add(permission);
//            // Check for Rationale Option
//            if (!shouldShowRequestPermissionRationale(permission))
//                return false;
//        }
//        return true;
//    }
//    When every single permission got its grant result, the result will be sent to the same callback method, onRequestPermissionsResult. I use HashMap to make source code looks cleaner and more readable.
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
//            {
//                Map<String, Integer> perms = new HashMap<String, Integer>();
//                // Initial
//                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
//                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
//                perms.put(Manifest.permission.WRITE_CONTACTS, PackageManager.PERMISSION_GRANTED);
//                // Fill with results
//                for (int i = 0; i < permissions.length; i++)
//                    perms.put(permissions[i], grantResults[i]);
//                // Check for ACCESS_FINE_LOCATION
//                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                        && perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
//                        && perms.get(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//                    // All Permissions Granted
//                    insertDummyContact();
//                } else {
//                    // Permission Denied
//                    Toast.makeText(MainActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
//                            .show();
//                }
//            }
//            break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }
}
