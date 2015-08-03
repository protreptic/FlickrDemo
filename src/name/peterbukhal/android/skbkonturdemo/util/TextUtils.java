package name.peterbukhal.android.skbkonturdemo.util;

public class TextUtils {
	public static String formatPhotoCount(Integer count) {
		String result = null;
		
		if (count >= 1000000) {
			result = String.format("%.2fM", (count / 1000000f));
		} if (count >= 1000) {
			result = String.format("%.2fK", (count / 1000f));
		} else {
			result = String.valueOf(count);
		}
		
		return result;
	}
}
