package cn.dxjia.ffmpeg.library;

import android.util.Log;

/**
 * 警告：该文件不可修改，包括包名和类名
 */
public class FFmpegNativeHelper {
	public FFmpegNativeHelper() {
	}
	
	static {
		System.loadLibrary("avutil-54");
		System.loadLibrary("swresample-1");
		System.loadLibrary("avcodec-56");
		System.loadLibrary("avformat-56");
		System.loadLibrary("swscale-3");
		System.loadLibrary("avfilter-5");
		System.loadLibrary("avdevice-56");
		System.loadLibrary("ffmpegjni");
	}

	public static String runCommand(String command) {
		if(command == null || command.length() == 0) {
            return "Command can`t be empty.";
        }
		String[] args = command.split(" ");
		for(int i = 0; i < args.length; i++) {
			Log.d("ffmpeg-jni", args[i]);
		}
		try {
			return ffmpeg_run(args);
		} catch (Exception e) {
			throw e;
		}
	}

	private static native int ffmpeg_init();
	private static native int ffmpeg_uninit();
	private static native String ffmpeg_run(String[] args);
}
