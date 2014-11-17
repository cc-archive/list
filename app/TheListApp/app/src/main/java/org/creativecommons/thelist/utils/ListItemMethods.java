package org.creativecommons.thelist.utils;

/**
 * Created by damaris on 2014-11-16.
 */
public class ListItemMethods {
//
//    protected Context mContext;
//
//
//    public ListItemMethods(Context mContext) {
//        this.mContext = mContext;
//    }
//
//    public static void showListItemDialog() {
//
//        //DIALOG FOR LIST ITEM ACTION
//        DialogInterface.OnClickListener mDialogListener =
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch(which) {
//                            case 0:
//                                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                mMediaUri = getOutputMediaFileUri(PhotoConstants.MEDIA_TYPE_IMAGE);
//                                if (mMediaUri == null) {
//                                    // display an error
//                                    Toast.makeText(MainActivity.this, R.string.error_external_storage,
//                                            Toast.LENGTH_LONG).show();
//                                }
//                                else {
//                                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
//                                    startActivityForResult(takePhotoIntent, PhotoConstants.TAKE_PHOTO_REQUEST);
//                                }
//                                break;
//                            case 1: // Choose picture
//                                Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                                choosePhotoIntent.setType("image/*");
//                                startActivityForResult(choosePhotoIntent,PhotoConstants.PICK_PHOTO_REQUEST);
//                                break;
//                            case 2: // Save Item to My List
//                                //TODO: POST Data to save list item
//                                break;
//                        }
//                    }
//                    private Uri getOutputMediaFileUri(int mediaType) {
//                        // To be safe, you should check that the SDCard is mounted
//                        // using Environment.getExternalStorageState() before doing this.
//                        if (isExternalStorageAvailable()) {
//                            // get the URI
//
//                            // 1. Get the external storage directory
//                            String appName = mContext.getString(R.string.app_name);
//                            File mediaStorageDir = new File(
//                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                                    appName);
//
//                            // 2. Create our subdirectory
//                            if (! mediaStorageDir.exists()) {
//                                if (! mediaStorageDir.mkdirs()) {
//                                    Log.e(TAG, "Failed to create directory.");
//                                    return null;
//                                }
//                            }
//
//                            // 3. Create a file name
//                            // 4. Create the file
//                            File mediaFile;
//                            Date now = new Date();
//                            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);
//
//                            String path = mediaStorageDir.getPath() + File.separator;
//                            if (mediaType == PhotoConstants.MEDIA_TYPE_IMAGE) {
//                                mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
//                            }
//                            else {
//                                return null;
//                            }
//
//                            Log.d(TAG, "File: " + Uri.fromFile(mediaFile));
//
//                            // 5. Return the file's URI
//                            return Uri.fromFile(mediaFile);
//                        }
//                        else {
//                            return null;
//                        }
//                    }
//                    //Check if external storage is available
//                    private boolean isExternalStorageAvailable() {
//                        String state = Environment.getExternalStorageState();
//
//                        if (state.equals(Environment.MEDIA_MOUNTED)) {
//                            return true;
//                        }
//                        else {
//                            return false;
//                        }
//                    }
//                };
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setItems(R.array.listItem_choices, mDialogListener);
//        AlertDialog dialog = builder.create();
//        dialog.show();
//
//    }
}
