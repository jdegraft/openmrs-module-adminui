/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.adminui.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.UserService;
import org.openmrs.module.adminui.AdminUiConstants;
import org.openmrs.module.providermanagement.Provider;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;

public class Account {
	
	private Person person;
	
	private ArrayList<User> userSet = new ArrayList<User>();
	
	private Set<Provider> providerSet;
	
	private boolean providerChecked = false;
	
	private boolean userChecked = false;
	
	private ArrayList<String> usernames;
	
	private ArrayList<String> passwords;
	
	private HashMap<User, String> confirmPasswords;
	
	private AccountService accountService;
	
	private UserService userService;
	
	private PersonService personService;
	
	private ProviderService providerService;
	
	private ProviderManagementService providerManagementService;
	
	public Account(Person person, AccountService accountService, UserService userService, ProviderService providerService,
	    ProviderManagementService providerManagementService, PersonService personService) {
		this.accountService = accountService;
		this.userService = userService;
		this.providerService = providerService;
		this.providerManagementService = providerManagementService;
		this.personService = personService;
		this.person = person;
	}
	
	public Person getPerson() {
		return person;
	}
	
	public boolean isUserLinked() {
		return userChecked;
	}
	
	public boolean isProviderLinked() {
		return providerChecked;
	}
	
	private void initializePersonNameIfNecessary() {
		if (person.getPersonName() == null) {
			person.addName(new PersonName());
		}
	}
	
	private User generateNewUser() {
		User user = new User();
		user.setPerson(person);
		return user;
	}
	
	private Provider generateNewProvider() {
		Provider newProvider = new Provider();
		newProvider.setPerson(person);
		return newProvider;
	}
	
	public void setGivenName(String givenName) {
		initializePersonNameIfNecessary();
		person.getPersonName().setGivenName(givenName);
	}
	
	public String getGivenName() {
		return person.getGivenName();
	}
	
	public void setFamilyName(String familyName) {
		initializePersonNameIfNecessary();
		person.getPersonName().setFamilyName(familyName);
	}
	
	public String getFamilyName() {
		return person.getFamilyName();
	}
	
	public void setGender(String gender) {
		person.setGender(gender);
	}
	
	public String getGender() {
		return person.getGender();
	}
	
	public void setUserEnabled(boolean userEnabled) {
		if (userEnabled)
			this.userChecked = true;
	}
	
	public boolean getUserEnabled() {
		return userChecked;
	}
	
	public void setProviderEnabled(boolean providerEnabled) {
		if (providerEnabled)
			this.providerChecked = true;
	}
	
	public boolean getProviderEnabled() {
		return providerChecked;
	}
	
	public void createRequiredUsers(int countUsers) {
		if (userChecked) {
			for (int i = 1; i <= countUsers; i++) {
				User user = generateNewUser();
				userSet.add(user);
			}
		}
	}
	
	public void setUsernames(ArrayList<String> username) {
		usernames = new ArrayList<String>(username);
		for (int i = 0; i < userSet.size(); i++) {
			User user = userSet.get(i);
			user.setUsername(username.get(i));
		}
	}
	
	public String getUsername(int i) {
		User user = userSet.get(i);
		return user != null ? user.getUsername() : null;
	}
	
	public void setPasswords(ArrayList<String> password) {
		this.passwords = new ArrayList<String>(password);
	}
	
	public String getPassword(int i) {
		User user = userSet.get(i);
		return user != null ? passwords.get(i) : null;
	}
	
	public void setConfirmPasswords(ArrayList<String> confirmPassword) {
		confirmPasswords = new HashMap<User, String>();
		for (int i = 0; i < userSet.size(); i++) {
			User user = userSet.get(i);
			confirmPasswords.put(user, confirmPassword.get(i));
		}
	}
	
	public String getConfirmPassword(int i) {
		User user = userSet.get(i);
		return user != null ? confirmPasswords.get(i) : null;
	}
	
	public void setPrivilegeLevels(ArrayList<String> privilegeLevel) {
		for (int i = 0; i < userSet.size(); i++) {
			User user = userSet.get(i);
			Role role = userService.getRole(privilegeLevel.get(i));
			user.addRole(role);
		}
	}
	
	public Role getPrivilegeLevel(int i) {
		User user = userSet.get(i);
		if (user != null && user.getRoles() != null) {
			for (Role role : user.getRoles()) {
				if (role.getRole().startsWith(AdminUiConstants.ROLE_PREFIX_PRIVILEGE_LEVEL)) {
					return role;
				}
			}
		}
		return null;
	}
	
	public void setCapabilities(ArrayList<String[]> roles) {
		for (int i = 0; i < userSet.size(); i++) {
			User user = userSet.get(i);
			String[] userRoles = roles.get(i);
			for (String roleName : userRoles) {
				Role role = userService.getRole(roleName);
				user.addRole(role);
			}
		}
	}
	
	public Set<Role> getCapabilities(int i) {
		
		User user = userSet.get(i);
		if (user == null) {
			return null;
		}
		
		Set<Role> capabilities = new HashSet<Role>();
		
		if (user.getRoles() != null) {
			for (Role role : user.getRoles()) {
				if (role.getRole().startsWith(AdminUiConstants.ROLE_PREFIX_CAPABILITY)) {
					capabilities.add(role);
				}
			}
		}
		return capabilities;
	}
	
	public User getUser(int userNo) {
		return userSet.get(userNo);
	}
	
	public ArrayList<User> getUsers() {
		return userSet;
	}
	
	public int getUsersCount() {
		return userSet.size();
	}
	
	public void setProviderRoles(List<ProviderRole> providerRoles) {
	}
	
	public int getProvidersCount() {
		return providerSet.size();
	}
	
	public Set<Provider> getProviders() {
		return providerSet;
	}
	
	public void save() {
		
		if (person != null) {
			personService.savePerson(person);
		}
		
		if (userChecked && userSet.size() > 0) {
			for (int i = 0; i < userSet.size(); i++) {
				User user = userSet.get(i);
				if (user != null) {
					boolean existingUser = (user.getUserId() != null);
					userService.saveUser(user, passwords.get(i));
					
					// the saveUser(user, password) method will *only* set a password for a new user, it won't change an existing one
					if (existingUser && StringUtils.isNotBlank(passwords.get(i))
					        && StringUtils.isNotBlank(confirmPasswords.get(i))) {
						userService.changePassword(user, passwords.get(i));
					}
				}
			}
		}
		
		if (providerChecked && !providerSet.isEmpty()) {
			for (Provider provider : providerSet) {
				providerService.saveProvider(provider);
			}
		}
		
	}
}
