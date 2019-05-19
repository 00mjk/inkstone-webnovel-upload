package nyoibo.inkstone.upload.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.thymeleaf.util.StringUtils;
import nyoibo.inkstone.upload.data.logging.Logger;
import nyoibo.inkstone.upload.data.logging.LoggerFactory;
import nyoibo.inkstone.upload.message.MessageMethod;

/**
 * <p>Title:InkstoneRawHeaderUtils.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-19 17:35
 */

public class InkstoneRawHeaderUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InkstoneRawHeaderUtils.class);
	public static String getRawCNHeaderToEN(String header) throws Exception {
		String convert = null;
		String regexCN = "第([\\s\\S]*?)章";
		String regexNum = "\\d+(\\.\\d+){0,1}";
		Matcher matcher = Pattern.compile(regexCN).matcher(header);
		if (matcher.find()) {
			convert = matcher.group(1).trim();
			LOGGER.begin().headerAction(MessageMethod.EVENT).info(String.format("Catch raw header key:[%s]", convert));

			int number = StringNumberUtils.numberCN2Arab(convert);
			convert = Integer.toString(number);

		} else {
			matcher = Pattern.compile(regexNum).matcher(header);
			if (matcher.find()) {
				convert = matcher.group();
				LOGGER.begin().headerAction(MessageMethod.EVENT)
						.info(String.format("Catch raw header key:[%s]", convert));
			}
		}

		if (StringUtils.isEmpty(convert)) {
			throw new Exception(String.format("Cannot recognize the raw header in format：[%s] if need help, "
					+ "please contact support for this bug.", header));
		}

		return convert;
	}

	public static String getRawExelChap(String header) {
		String convert = null;
		convert = header.toLowerCase();
		
		
		return convert;
	}

	public static void main(String[] args) throws Exception {
         String s = "a123";
         System.out.println(s.toLowerCase());
	}

}
