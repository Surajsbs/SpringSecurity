package com.bitwise.authorization.user.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bitwise.authorization.vo.User;
import com.bitwise.authorization.vo.UserRole;

/**
 * @author surajs
 *
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private JdbcTemplate template;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = template.query(
				"select id, first_name, last_name, user_name, password from users where user_name = ?",
				new Object[] { username }, new ResultSetExtractor<User>() {

					@Override
					public User extractData(ResultSet rs) throws SQLException, DataAccessException {
						User users = null;
						if (rs.next()) {
							int id = rs.getInt("id");
							String fn = rs.getString("first_name");
							String ln = rs.getString("last_name");
							String un = rs.getString("user_name");
							String pwd = rs.getString("password");
							users = new User(id, fn, ln, un, pwd);
						}
						return users;
					}
				});

		if (user != null) {
			System.out.println("User : '" + username + "' is found successfully");
			user.setGrantedAuthorities(getGrantedAuthorities(username));
			return new org.springframework.security.core.userdetails.User(user.getUser_name(), user.getPassword(), true,
					true, true, true, user.getGrantedAuthorities());
		}
		System.out.println("User : '" + username + "' is not found");
		throw new UsernameNotFoundException(username);
	}

	private List<GrantedAuthority> getGrantedAuthorities(String username) {
		List<UserRole> roles = template.query(
				"select role.id, role.role_name from users u inner join user_role ur on ur.user_id = u.id "
						+ "inner join role role on role.id = ur.role_id where u.user_name = ?",
				new Object[] { username }, new RowMapper<UserRole>() {

					@Override
					public UserRole mapRow(ResultSet rs, int rowNum) throws SQLException {
						return new UserRole(rs.getString("role_name"), rs.getInt("id"));
					}
				});

		List<GrantedAuthority> grantedAuthorities = new ArrayList<>(roles.size());
		for (UserRole role : roles) {
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRole()));
		}
		System.out.println("Role are found for user : " + username + " as '" + roles + "'");
		return grantedAuthorities;
	}
}
