package com.dreamlink.communication.api;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

public class FileUtil {

	/**
	 * convert bytes to KB,MB
	 * @param fileSize
	 * @return like:3.45MB
	 */
	public static String getFormatFileSize(long fileSize){
		if (fileSize > 1024 * 1024) {
			Double dsize = (double) (fileSize / (1024 * 1024));
			return new DecimalFormat("#.00").format(dsize) + "MB";
		}else if (fileSize > 1024) {
			Double dsize = (double)fileSize / (1024);
			return new DecimalFormat("#.00").format(dsize) + "KB";
		}else {
			return String.valueOf((int)fileSize) + " Bytes";
		}
	}
	
	/**
	 * bytes tp chars
	 * @param bytes
	 * @return
	 */
	public static char[] getChars(byte[] bytes) {
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array();
}

}
