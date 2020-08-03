package application.utility;

import application.utility.strings.ServiceNameUtil;

public class ServiceUtil {

	public static boolean isAssistanceService(String service) {
		return service.equals(ServiceNameUtil.ASSISTANCE_SERVICE);
	}
}
