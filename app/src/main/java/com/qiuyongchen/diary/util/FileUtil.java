package com.qiuyongchen.diary.util;

/**
 * Created by qiuyongchen on 10/30/2015.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

public class FileUtil {
    private static int bufferd = 1024;

    private FileUtil() {
    }

	/*
     * <!-- 在SDCard中创建与删除文件权限 --> <uses-permission
	 * android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/> <!--
	 * 往SDCard写入数据权限 --> <uses-permission
	 * android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	 */

    // =================get SDCard information===================
    public static boolean isSdcardAvailable() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    public static long getSDAllSizeKB() {
        // get path of sdcard
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // get single block size(Byte)
        long blockSize = sf.getBlockSize();
        // 获取所有数据块数
        long allBlocks = sf.getBlockCount();
        // 返回SD卡大小
        return (allBlocks * blockSize) / 1024; // KB
    }

    /**
     * free size for normal application
     *
     * @return
     */
    public static long getSDAvalibleSizeKB() {
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        long blockSize = sf.getBlockSize();
        long avaliableSize = sf.getAvailableBlocks();
        return (avaliableSize * blockSize) / 1024;// KB
    }

    // =====================File Operation==========================
    public static boolean isFileExist(String directory) {
        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + directory);
        return file.exists();
    }

    public static boolean createFile(String directory) {
        if (isFileExist(directory)) {
            Log.i("isFileExist(" + directory + ")", "true");
        } else {
            Log.i("isFileExist(" + directory + ")", "false");
        }

        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + directory);
        if (!file.mkdirs()) {
            Log.i("file.mkdirs(" + directory + ")", "false");
            return false;
        }
        return true;

    }

    public static File writeToSDCardFile(String directory, String fileName,
                                         String content, boolean isAppend) {
        return writeToSDCardFile(directory, fileName, content, "", isAppend);
    }

    public static File createFileOnSDCard(String dir, String fileName)
            throws IOException {
        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + dir + File.separator
                + fileName);
        file.createNewFile();
        return file;
    }

    /**
     * @param directory (you don't need to begin with
     *                  Environment.getExternalStorageDirectory()+File.separator)
     * @param fileName
     * @param content
     * @param encoding  (UTF-8...)
     * @param isAppend  : Context.MODE_APPEND
     * @return
     */
    public static File writeToSDCardFile(String directory, String fileName,
                                         String content, String encoding, boolean isAppend) {
        File file = null;
        OutputStream output = null;

        try {
            file = createFileOnSDCard(directory, fileName);
            output = new FileOutputStream(file);
            byte[] b = content.getBytes();
            output.write(b);
            output.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null)
                    output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static String readFromSDCardFile(String directory, String fileName) {
        String content = ""; // 文件内容字符串
        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + directory + File.separator + fileName);

        try {
            InputStream inStream = new FileInputStream(file);
            if (inStream != null) {
                InputStreamReader inputReader = new InputStreamReader(inStream);
                BufferedReader buffReader = new BufferedReader(inputReader);

                String line;
                // 分行读取
                while ((line = buffReader.readLine()) != null) {
                    content += line + "\n";
                }
                inStream.close();
            }
        } catch (java.io.FileNotFoundException e) {
            Log.d("TestFile", "The File doesn't not exist.");
        } catch (IOException e) {
            Log.d("TestFile", e.getMessage());
        }

        return content;
    }

    public static Boolean comprobarSDCard(Context mContext) {
        String auxSDCardStatus = Environment.getExternalStorageState();

        if (auxSDCardStatus.equals(Environment.MEDIA_MOUNTED))
            return true;
        else if (auxSDCardStatus.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            Toast.makeText(
                    mContext,
                    "Warning, the SDCard it's only in read mode.\nthis does not result in malfunction"
                            + " of the read aplication", Toast.LENGTH_LONG)
                    .show();
            return true;
        } else if (auxSDCardStatus.equals(Environment.MEDIA_NOFS)) {
            Toast.makeText(
                    mContext,
                    "Error, the SDCard can be used, it has not a corret format or "
                            + "is not formated.", Toast.LENGTH_LONG)
                    .show();
            return false;
        } else if (auxSDCardStatus.equals(Environment.MEDIA_REMOVED)) {
            Toast.makeText(
                    mContext,
                    "Error, the SDCard is not found, to use the reader you need "
                            + "insert a SDCard on the device.",
                    Toast.LENGTH_LONG).show();
            return false;
        } else if (auxSDCardStatus.equals(Environment.MEDIA_SHARED)) {
            Toast.makeText(
                    mContext,
                    "Error, the SDCard is not mounted beacuse is using "
                            + "connected by USB. Plug out and try again.",
                    Toast.LENGTH_LONG).show();
            return false;
        } else if (auxSDCardStatus.equals(Environment.MEDIA_UNMOUNTABLE)) {
            Toast.makeText(
                    mContext,
                    "Error, the SDCard cant be mounted.\nThe may be happend when the SDCard is corrupted "
                            + "or crashed.", Toast.LENGTH_LONG).show();
            return false;
        } else if (auxSDCardStatus.equals(Environment.MEDIA_UNMOUNTED)) {
            Toast.makeText(
                    mContext,
                    "Error, the SDCArd is on the device but is not mounted."
                            + "Mount it before use the app.",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


}
