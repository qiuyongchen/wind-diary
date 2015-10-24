package com.qiuyongchen.diary.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.os.Environment;
import android.util.Log;

/**
 * help to create file, create directory.
 *
 * @author redleaf
 */
public class FileHelper {
    private String sdCardRoot;

    public FileHelper() {
        super();
        this.sdCardRoot = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
    }

    /**
     * ��SD���ϴ���Ŀ¼
     *
     * @param dir Ŀ¼��
     * @return
     */
    public File createDirOnSDCard(String dir) {
        File dirFile = new File(sdCardRoot + File.separator + dir
                + File.separator);
        Log.i("createDirOnSDCard", sdCardRoot + File.separator + dir
                + File.separator);
        dirFile.mkdirs();
        return dirFile;
    }

    /**
     * ��SD��Ŀ¼�����ļ�
     *
     * @param fileName
     * @param dir
     * @return
     * @throws IOException
     */
    public File createFileOnSDCard(String fileName, String dir)
            throws IOException {
        File file = new File(sdCardRoot + File.separator + dir + File.separator
                + fileName);
        Log.e("createFileOnSDCard", sdCardRoot + File.separator + dir
                + File.separator + fileName);
        file.createNewFile();
        return file;
    }

    /**
     * �ж�SD�����ļ��Ƿ����
     *
     * @param fileName
     * @param dir
     * @return
     */
    public boolean isFileExist(String fileName, String dir) {
        File file = new File(sdCardRoot + File.separator + dir + File.separator
                + fileName);
        return file.exists();
    }

    public File writeToFileOnSDCard(String fileName, String dir, String content) {
        File file = null;
        OutputStream output = null;

        try {
            file = createFileOnSDCard(fileName, dir);
            output = new FileOutputStream(file);
            byte[] b = content.getBytes();
            output.write(b);
            output.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * read file from SD card.
     *
     * @param fileName
     * @param dir
     * @return
     */
    public String readFromFileOnSDCard(String fileName, String dir) {
        String content = ""; // 文件内容字符串
        File file = new File(sdCardRoot + File.separator + dir + File.separator
                + fileName);
        try {
            InputStream instream = new FileInputStream(file);
            if (instream != null) {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                // 分行读取
                while ((line = buffreader.readLine()) != null) {
                    content += line + "\n";
                }
                instream.close();
            }
        } catch (FileNotFoundException e) {
            Log.d("TestFile", "The File doesn't not exist.");
        } catch (IOException e) {
            Log.d("TestFile", e.getMessage());
        }

        return content;
    }
}
