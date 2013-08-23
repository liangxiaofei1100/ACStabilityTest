package com.dreamlink.communication.api;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

/**
 * This class is used for write log to file. It is useful for debug. Log file is
 * located at /data/data/com.dreamlink.communication/files/</br>
 * 
 * Usage:</br>
 * 
 * 1. {@link #open()}, Before write, we need to open the file to prepare
 * writing.</br>
 * 
 * 2. {@link #writeLog(String)}, Write log to file.</br>
 * 
 * 3. {@link #close()}, After finish all writing, do not forget to close the
 * file.;
 * 
 */
public class LogFile {
	private File mFile;
	private FileWriter mWriter;

	/**
	 * Create log file use the file name.
	 * 
	 * @param context
	 * @param fileName
	 *            file name.
	 */
	public LogFile(Context context, String fileName) {
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String filePath = path + "/" + fileName;
		// String filePath = context.getFilesDir().getAbsolutePath() + "/"
		// + fileName;
		mFile = new File(filePath);
		if (!mFile.exists()) {
			try {
				mFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Before write, we need to open the file to prepare writing.
	 */
	public void open() {
		try {
			mWriter = new FileWriter(mFile);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write log to file. </br>
	 * 
	 * Tips: add \n to make log more readable.
	 * 
	 * @param log
	 */
	public void writeLog(String log) {
		try {
			mWriter.write(log);
			mWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeLog(byte[] logs) {
		char[] log = FileUtil.getChars(logs);
		try {
			mWriter.write(log);
			mWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * After finish all writing, do not forget to close the file.
	 */
	public void close() {
		try {
			mWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
