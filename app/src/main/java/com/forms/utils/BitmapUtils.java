package com.forms.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * description： bitmap工具
 * <p/>
 * Created by TIAN FENG on 2017年9月5日 QQ：27674569 Email: 27674569@qq.com
 * Version：1.0
 */
public class BitmapUtils {
	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * byte[] 转file
	 */
	public static File bytes2File(byte[] buf, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists() && dir.isDirectory()) {
				dir.mkdirs();
			}
			file = new File(filePath + File.separator + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(buf);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return file;
	}

	/**
	 * 旋转图片
	 * 
	 * @param angle
	 * @param path
	 * @return
	 */
	public static Bitmap rotaingImage(int angle, String path) {
		// 将图片压缩一下
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = 2;
		Bitmap bitmap = BitmapFactory.decodeFile(
				Environment.getExternalStorageDirectory() + File.separator + "image" + File.separator + "temp",
				bitmapOptions);
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * bitmap 转file
	 * 
	 * @param bitmap
	 * @param path
	 * @return
	 */
	public static File bitmap2File(Bitmap bitmap, String path) {
		File file = new File(path);// 将要保存图片的路径
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * 圆角图
	 */
	public static Bitmap getCircleBitmap(Bitmap bitmap) {
		try {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			final RectF rectF = new RectF(rect);
			canvas.drawRoundRect(rectF, 8, 8, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);
			return output;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return bitmap;
	}

	/**
	 * 通过uri获取图片并进行压缩
	 * 
	 * @param uri
	 */
	public static Bitmap getBitmapFormUri(Context context, Uri uri, int maxWidth, int maxHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 只读取图片尺寸
		resolveUri(context, uri, options);
		// 计算实际缩放比例
		int scale = 1;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			if ((options.outWidth / scale > maxWidth && options.outWidth / scale > maxWidth * 1.4)
					|| (options.outHeight / scale > maxHeight && options.outHeight / scale > maxHeight * 1.4)) {
				scale++;
			} else {
				break;
			}
		}
		options.inSampleSize = scale;
		options.inJustDecodeBounds = false;// 读取图片内容
		options.inPreferredConfig = Config.RGB_565; // 根据情况进行修改
		Bitmap bitmap = null;
		try {
			bitmap = resolveUriForBitmap(context, uri, options);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	private static void resolveUri(Context context, Uri uri, BitmapFactory.Options options) {
		if (uri == null) {
			return;
		}
		String scheme = uri.getScheme();
		if (ContentResolver.SCHEME_CONTENT.equals(scheme) || ContentResolver.SCHEME_FILE.equals(scheme)) {
			InputStream stream = null;
			try {
				stream = context.getContentResolver().openInputStream(uri);
				BitmapFactory.decodeStream(stream, null, options);
			} catch (Exception e) {
				Log.w("resolveUri", "未打开: " + uri, e);
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException e) {
						Log.w("resolveUri", "未关闭: " + uri, e);
					}
				}
			}
		} else if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
			Log.w("resolveUri", "未关闭: " + uri);
		} else {
			Log.w("resolveUri", "未关闭: " + uri);
		}
	}

	private static Bitmap resolveUriForBitmap(Context context, Uri uri, BitmapFactory.Options options) {
		if (uri == null) {
			return null;
		}

		Bitmap bitmap = null;
		String scheme = uri.getScheme();
		if (ContentResolver.SCHEME_CONTENT.equals(scheme) || ContentResolver.SCHEME_FILE.equals(scheme)) {
			InputStream stream = null;
			try {
				stream = context.getContentResolver().openInputStream(uri);
				bitmap = BitmapFactory.decodeStream(stream, null, options);
			} catch (Exception e) {
				Log.w("resolveUriForBitmap", "Unable to open content: " + uri, e);
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException e) {
						Log.w("resolveUriForBitmap", "Unable to close content: " + uri, e);
					}
				}
			}
		} else if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
			Log.w("resolveUriForBitmap", "Unable to close content: " + uri);
		} else {
			Log.w("resolveUriForBitmap", "Unable to close content: " + uri);
		}

		return bitmap;
	}

	/**
	 * 质量压缩方法
	 * 
	 * @param image
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 50) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			if (options < 0) {
				break;
			}
			// 第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差 ，第三个参数：保存压缩后的数据的流
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 根据uri获得path
	 */
	public static String getPathByUri(final Context context, final Uri uri) {
		if (null == uri)
			return null;
		final String scheme = uri.getScheme();
		String data = null;
		if (scheme == null)
			data = uri.getPath();
		else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
			data = uri.getPath();
		} else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
			Cursor cursor = context.getContentResolver().query(uri, new String[] { ImageColumns.DATA }, null, null,
					null);
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					int index = cursor.getColumnIndex(ImageColumns.DATA);
					if (index > -1) {
						data = cursor.getString(index);
					}
				}
				cursor.close();
			}
		}
		return data;
	}
}
