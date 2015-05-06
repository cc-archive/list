/* The MIT License (MIT)

Copyright (c) 2014 Treehouse Island, Inc.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.creativecommons.thelist.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileHelper {
	
	public static final String TAG = FileHelper.class.getSimpleName();
	
	public static final int SHORT_SIDE_TARGET = 1280;
	
	public static String getByteArrayFromFile(Context context, Uri uri) {
		byte[] fileBytes;
        String fileString = null;
        InputStream inStream = null;
        ByteArrayOutputStream outStream = null;
        
        if (uri.getScheme().equals("content")) {
        	try {
        		inStream = context.getContentResolver().openInputStream(uri);
        		outStream = new ByteArrayOutputStream();
            
        		byte[] bytesFromFile = new byte[1024*1024]; // buffer size (1 MB)
        		int bytesRead = inStream.read(bytesFromFile);
        		while (bytesRead != -1) {
        			outStream.write(bytesFromFile, 0, bytesRead);
        			bytesRead = inStream.read(bytesFromFile);
        		}
            
        		fileBytes = outStream.toByteArray();
                fileString = new String(Base64.encode(fileBytes, Base64.DEFAULT));
        	}
	        catch (IOException e) {
	        	Log.e(TAG, e.getMessage());
	        }
	        finally {
	        	try {
	        		inStream.close();
	        		outStream.close();
	        	}
	        	catch (IOException e) { /*( Intentionally blank */ }
	        }
        }
        else {
        	try {
	        	File file = new File(uri.getPath());
	        	FileInputStream fileInput = new FileInputStream(file);
	        	fileBytes = IOUtils.toByteArray(fileInput);
                fileString = new String(Base64.encode(fileBytes, Base64.DEFAULT));
        	}
        	catch (IOException e) {
        		Log.e(TAG, e.getMessage());
        	}
       	}
        
        return fileString;
	}

    //Not used reduceImageForUpload
	public static byte[] reduceImageForUpload(byte[] imageData) {
		Bitmap bitmap = org.creativecommons.thelist.utils.ImageResizer.resizeImageMaintainAspectRatio(imageData, SHORT_SIDE_TARGET);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
		byte[] reducedData = outputStream.toByteArray();
		try {
			outputStream.close();
		}
		catch (IOException e) {
			// Intentionally blank
		}
		
		return reducedData;
	}

//    public static String getFileName(Context context, Uri uri){
//        getFileType(context, uri);
//
//        Cursor returnCursor =
//                context.getContentResolver().query(uri, null, null, null, null);
//        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//        returnCursor.moveToFirst();
//
//        String fileName = returnCursor.getString(nameIndex);
//
//        Log.v(TAG,"> getFileName: " + returnCursor.getString(nameIndex));
//        return fileName;
//
//    }

//    public static String getFileType(Context context, Uri uri){
//        String mimeType = context.getContentResolver().getType(uri);
//
//        Log.v(TAG,"> getFileType: " + mimeType);
//        return mimeType;
//
//    }


//	public static String getFileName(Context context, Uri uri, String fileType) {
//		String fileName = "uploaded_file.";
//
//		if (fileType.equals(PhotoConstants.MEDIA_TYPE_IMAGE)) {
//			fileName += "png";
//		}
//		else {
//			// For video, we want to get the actual file extension
//			if (uri.getScheme().equals("content")) {
//				// do it using the mime type
//				String mimeType = context.getContentResolver().getType(uri);
//				int slashIndex = mimeType.indexOf("/");
//				String fileExtension = mimeType.substring(slashIndex + 1);
//				fileName += fileExtension;
//			}
//			else {
//				fileName = uri.getLastPathSegment();
//			}
//		}
//
//		return fileName;
//	}


    public static long getFileSize(Uri uri){
        File photo = new File(uri.getPath());
        long size = photo.length()/(1024*1024);
        return size;
    }

    public static String createUploadPhotoObject(Context context, Uri uri) {
        //Convert photo file to Base64 encoded string
        String fileString = getByteArrayFromFile(context, uri);

        return fileString;
    }


    public static Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
} //FileHelper