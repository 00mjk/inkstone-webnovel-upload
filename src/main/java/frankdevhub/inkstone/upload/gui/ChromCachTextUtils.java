package nyoibo.inkstone.upload.gui;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

/**
 * <p>Title:WebLinkUtils.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>
 *
 * @author frankdevhub
 * @date:2019-05-24 02:26
 */

public class ChromCachTextUtils {
    private static Display display;
    private static Text textarea;

    public ChromCachTextUtils(Display display, Text textarea) {
        ChromCachTextUtils.display = display;
        ChromCachTextUtils.textarea = textarea;
    }

    public static void pushToChromCacheText(String message) {
        if (!StringUtils.isEmpty(message)) {
            display.syncExec(() -> textarea.append(message));
        }
    }
}
