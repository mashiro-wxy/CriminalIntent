package wengxiaoyang.hziee.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;

import java.io.IOException;

public class PictureUtils {
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        //读入磁盘上图像的尺寸
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //弄清要减少多少
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            float heightScale = srcHeight / destHeight;
            float widthScale = srcWidth / destWidth;
            inSampleSize = Math.round(heightScale > widthScale ? heightScale : widthScale);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        //读入和创建常量bitmap
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(path, size.x, size.y);
    }

    public static Bitmap Toturn(Bitmap img, String Path) {
        int digree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(Path);
        } catch (IOException e) {
            e.printStackTrace(); exif = null;
        }
        if (exif != null) {
            // 读取图片中相机方向信息
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            //计算旋转角度
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90: digree = 90;  break;
                case ExifInterface.ORIENTATION_ROTATE_180: digree = 180; break;
                case ExifInterface.ORIENTATION_ROTATE_270: digree = 270; break;
                default: digree = 0; break;
            }
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(digree);
        return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);}

    }

