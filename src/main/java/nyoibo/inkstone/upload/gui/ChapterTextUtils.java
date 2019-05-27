package nyoibo.inkstone.upload.gui;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class ChapterTextUtils {
	private static Display display;
	private static Text textarea;

	public ChapterTextUtils(Display display, Text textarea) {
		this.display = display;
		this.textarea = textarea;
	}

	public static void pushToChapterText(String message) {
		if (!StringUtils.isEmpty(message)) {
			display.syncExec(new Runnable() {
				@Override
				public void run() {
					textarea.append(message);
				}
			});
		}
	}
}
