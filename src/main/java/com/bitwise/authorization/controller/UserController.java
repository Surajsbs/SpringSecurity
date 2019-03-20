package com.bitwise.authorization.controller;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bitwise.authorization.vo.User;

/**
 * @author surajs
 *
 */
@RestController
@RequestMapping(value = "/data", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
	@Autowired
	@Qualifier("jdbcTemplate")
	private JdbcTemplate template;

	/* Get all users */
	@RequestMapping(value = "/user/getAll", method = RequestMethod.GET)
	@PostAuthorize("hasPermission(returnObject, 'read_all')")
	public ResponseEntity<Object> getAll() {
		return new ResponseEntity<>(
				template.query("select id, first_name, last_name, user_name from users", new RowMapper<User>() {

					@Override
					public User mapRow(ResultSet rs, int rowNum) throws SQLException {
						int id = rs.getInt("id");
						String fn = rs.getString("first_name");
						String ln = rs.getString("last_name");
						String un = rs.getString("user_name");
						return new User(id, fn, ln, un);
					}
				}), HttpStatus.OK);
	}

	/* Get single user */
	@RequestMapping(value = "/user/get/{id}", method = RequestMethod.GET)
	public ResponseEntity<String> get(@PathVariable Long id) {
		return new ResponseEntity<>("{\"message\":\"Success\",\"code\":" + id + "}", HttpStatus.OK);
	}

	/* Filter */
	@RequestMapping(value = "/user/filter", method = RequestMethod.GET)
	@PostAuthorize("hasPermission(returnObject, 'filterGet')")
	public ResponseEntity<String> get(@RequestParam String name) {
		return new ResponseEntity<>("{\"message\":\"Success\",\"code\":600000}" + name, HttpStatus.OK);
	}

	/* update user */
	@RequestMapping(value = "/user/update/{id}", method = RequestMethod.PUT)
	@PostAuthorize("hasPermission(returnObject, 'update')")
	public ResponseEntity<String> update(@PathVariable Long id) {
		return new ResponseEntity<>("{\"message\":\"Updated successfully\",\"code\":600000}", HttpStatus.OK);
	}

	/* Get delete single user */
	@RequestMapping(value = "/user/delete/{id}", method = RequestMethod.DELETE)
	@PostAuthorize("hasPermission(returnObject, 'delete')")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		return new ResponseEntity<>("{\"message\":\"Deleted successfully\",\"code\":600000}", HttpStatus.OK);
	}

	/* Invalidate single user */
	@RequestMapping(value = "/user/invalidate/{id}", method = RequestMethod.GET)
	public ResponseEntity<String> invalidateUser(@PathVariable Long id) {
		return new ResponseEntity<>("{\"message\":\"User invalidated successfully\",\"code\":600000}", HttpStatus.OK);
	}

	/* Create a user */
	@RequestMapping(value = "/user/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PostAuthorize("hasPermission(returnObject, 'create')")
	public ResponseEntity<Object> createUser(@RequestBody Object object) {
		return new ResponseEntity<>(object, HttpStatus.CREATED);
	}
}