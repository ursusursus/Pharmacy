package utils;

import java.util.List;

public class Security {

	public static boolean isAuthenticated(String sessionId) {
		return (sessionId != null);
	}
	
	public static boolean isAuthorized(List<String> authorizedRoles, String userRole) {
		for (String authorizedRole : authorizedRoles) {
			if(userRole.equals(authorizedRole)) {
				return true;
			}
		}
		return false;
	}
}
