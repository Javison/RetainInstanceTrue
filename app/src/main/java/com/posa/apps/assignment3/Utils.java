package com.posa.apps.assignment3;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by javigm on 4/11/15.
 */
public class Utils {




    public static String getUniqueFilename(final Uri filename) {
        return Base64.encodeToString((filename.toString()
                        + System.currentTimeMillis()
                        + Thread.currentThread().getName()).getBytes(),
                Base64.NO_WRAP);
    }

    /**
     * Copy the contents of the @a inputStream to the @a outputStream
     * in a manner that can be interrupted properly.
     *
     * @return true if copy completed without being interrupted, else false
     *
     * @throws IOException
     */
    public static boolean interruptibleCopy(InputStream inputStream, OutputStream outputStream) throws IOException {
        final byte[] buffer = new byte[1024];

        try {
            // Keep looping until the input stream is finished or the
            // thread is interrupted.
            for (int n; (n = inputStream.read(buffer)) >= 0; ) {
                if (Thread.interrupted()) {
                    return false;
                }
                // Write the bytes to the output stream.
                outputStream.write(buffer, 0, n);

            }
        } finally {
            // Flush the contents of the output stream.
            outputStream.flush();
        }
        return true;
    }


    public static String saveBitmapOnPngFile(Bitmap bitamp, String photoName){
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        File file = new File(path, photoName + ".png");
        File file2 = new File(path);
        String filePath = "";
        try {
            fOut = new FileOutputStream(file);
            if (!bitamp.compress(Bitmap.CompressFormat.PNG, 90, fOut)) {
                Log.e("MOOC", "error while saving bitmap " + path + photoName);
            } else {
                filePath = file.getAbsolutePath();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filePath;
    }

    public static String saveBitmapOnPngFile(Bitmap bitamp){
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        //File file = new File(path);
        String filePath = "";
        try {
            File file2 = File.createTempFile("BW-", ".png", new File(path));
            fOut = new FileOutputStream(file2);
            if (!bitamp.compress(Bitmap.CompressFormat.PNG, 90, fOut)) {
                Log.e("MOOC", "error while saving bitmap " + path);
            } else {
                filePath = file2.getAbsolutePath();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath;
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }


    /**
     * Download an image file from the URL provided by the user and
     * decode into a Bitmap.
     *
     * @param url
     *            The url where a bitmap image is located
     *
     * @return the image bitmap or null if there was an error
     */
    public static Bitmap downloadAndDecodeImage(String url) {
        try {
            // Check to see if this thread has been interrupted.
            if (Thread.interrupted()) {
                Alog.debug("Utils", "Thread.interrupted() 1");
                return null;
            }

            // Connect to a remote server, download the contents of
            // the image, and provide access to it via an Input
            // Stream.
            InputStream is = (InputStream) new URL(url).getContent();

            // Check to see if this thread has been interrupted.
            if (Thread.interrupted()) {
                Alog.debug("Utils", "Thread.interrupted() 2");
                return null;
            } else {
                // Decode an InputStream into a Bitmap.
                return BitmapFactory.decodeStream(is);
            }
        } catch (Exception e) {
            Alog.error("Utils", "Error downloading image");
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Apply a grayscale filter to the @a pathToImageFile and return a
     * Uri to the filtered image.
     */
    public static Uri grayScaleFilter(Context context, Uri pathToImageFile, Uri directoryPathname) {
        Bitmap originalImage = decodeImageFromPath(context, pathToImageFile);

        // Bail out if something is wrong with the image.
        if (originalImage == null)
            return null;

        Bitmap grayScaleImage = originalImage.copy(originalImage.getConfig(), true);

        boolean hasTransparent = grayScaleImage.hasAlpha();
        int width = grayScaleImage.getWidth();
        int height = grayScaleImage.getHeight();

        // A common pixel-by-pixel grayscale conversion algorithm
        // using values obtained from en.wikipedia.org/wiki/Grayscale.
        for (int i = 0; i < height; ++i) {
            // Break out if we've been interrupted.
            if (Thread.interrupted())
                return null;

            for (int j = 0; j < width; ++j) {
                // Check if the pixel is transparent in the original
                // by checking if the alpha is 0.
                if (hasTransparent && ((grayScaleImage.getPixel(j, i) & 0xff000000) >> 24) == 0)
                    continue;

                // Convert the pixel to grayscale.
                int pixel = grayScaleImage.getPixel(j, i);
                int grayScale = (int) (Color.red(pixel) * .299
                        + Color.green(pixel) * .587
                        + Color.blue(pixel) * .114);
                grayScaleImage.setPixel(j, i, Color.rgb(grayScale, grayScale, grayScale));
            }
        }

        // Create a filePath to a temporary file.
        File filePath = new File(openDirectory(directoryPathname),
                getUniqueFilename(Uri.parse(pathToImageFile.getLastPathSegment())));

        try {

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            grayScaleImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

            // Create a URI from the file.
            return Uri.fromFile(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a open File if @a directoryPath points to a valid
     * directory, else null.
     */
    public static File openDirectory(Uri directoryPathname) {
        File d = new File(directoryPathname.toString());
        if (!d.exists()
                && !d.mkdir())
            return null;
        else
            return d;
    }


    /**
     * Decode an image located at @a pathToImageFile and return a
     * Bitmap to the image.  This method scales the image to avoid
     * out-of-memory exceptions when decoding large images.
     */
    public static Bitmap decodeImageFromPath(Context context, Uri pathToImageFile) {
        ActivityManager mgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        mgr.getMemoryInfo(info);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        BitmapFactory.decodeFile(pathToImageFile.toString(), options);
        int ratio = (int) (4 * (long) options.outHeight * (long) options.outWidth * (long) 4 / (info.availMem + 1));

        options.inSampleSize = ratio;
        options.inJustDecodeBounds = false;

        try {
            return BitmapFactory.decodeFile(pathToImageFile.toString(), options);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
