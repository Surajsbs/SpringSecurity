package com.bitwise.authorization.user.permission;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * @author surajs
 *
 */
@Component
public class SimplePermissionEvaluator implements PermissionEvaluator {

	@Autowired
	@Qualifier("jdbcTemplate")
	private JdbcTemplate template;

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		if (authentication == null) {
			System.out.println("authentication is null");
			return false;
		} else if (!(permission instanceof String)) {
			System.out.println("Invalid targetDomainObject");
			return false;
		} else
			return hasPrivilege(authentication, permission.toString().toUpperCase());
	}

	private boolean hasPrivilege(Authentication auth, String permission) {
		return retrivePermissions(auth).contains(permission.toUpperCase()) ? true : false;
	}

	private List<String> retrivePermissions(Authentication authentication) {
		String query = "select p.per_name, u.user_name from user_role ur "
				+ "inner join users u on ur.user_id = u.id inner join role role on role.id = ur.role_id "
				+ "inner join role_permission rp on role.id = rp.role_id "
				+ "inner join permission p on p.id = rp.per_id where u.user_name = ?";

		return template.query(query, new Object[] { authentication.getPrincipal().toString() },
				new RowMapper<String>() {

					@Override
					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString("per_name").toUpperCase();
					}
				});
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
			Object permission) {
		return false;
	}
}