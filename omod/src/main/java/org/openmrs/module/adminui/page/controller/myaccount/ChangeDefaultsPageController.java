/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.adminui.page.controller.myaccount;

import java.util.Locale;
import java.util.Set;
import java.util.HashSet;

import java.lang.System;

import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.uicommons.UiCommonsConstants;
import org.openmrs.module.uicommons.util.InfoErrorMessageUtil;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class ChangeDefaultsPageController {

    public void get(PageModel pageModel) {
        User user = Context.getAuthenticatedUser();
        Map<String, String> props = user.getUserProperties();
        UserDefaults userDefaults = new UserDefaults();
        userDefaults.setDefaultLocale(props.get(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCALE));
        userDefaults.setDisplayProficientLocales(
        		Context.getAdministrationService().getPresentationLocales(),
        		props.get(OpenmrsConstants.USER_PROPERTY_PROFICIENT_LOCALES)
        );
    	pageModel.addAttribute("userDefaults", userDefaults);
    }

    public String post(PageModel pageModel, 
                       @RequestParam(required = false, value = "defaultLocale") String defaultLocale,
                       @RequestParam(required = false, value = "proficientLocalesList") String[] proficientLocalesList,
                       @SpringBean("userService") UserService userService,
                       HttpServletRequest request) {

        //TODO do some validation
        try {
            User user = Context.getAuthenticatedUser();
            
            UserDefaults userDefaults = new UserDefaults();
        	userDefaults.setDefaultLocale(defaultLocale);
        	userDefaults.setDisplayProficientLocalesList(
        			Context.getAdministrationService().getPresentationLocales(),
        			proficientLocalesList
        	);
        	Map<String, String> props = user.getUserProperties();
            props.put(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCALE, userDefaults.getDefaultLocale());
            props.put(OpenmrsConstants.USER_PROPERTY_PROFICIENT_LOCALES, userDefaults.getProficientLocales());
            user.setUserProperties(props);
            userService.saveUser(user, null);
            InfoErrorMessageUtil.flashInfoMessage(request.getSession(), "adminui.account.defaults.success");
        } catch (Exception ex) {
            request.getSession().setAttribute(
                    UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, "adminui.account.defaults.fail");
            return "account/changeDefaults";
        }
        return "myaccount/myAccount";
    }

    public class LocaleOption {

        private Locale locale;
        private boolean selected;
        private String name;
        private String language;

        public LocaleOption() {
        }

        public LocaleOption(Locale locale, boolean selected, String name, String language) {
            this.locale = locale;
            this.selected = selected;
            this.name = name;
            this.language = language;
        }

        public Locale getLocale() {
            return locale;
        }

        public void setLocale(Locale locale) {
            this.locale = locale;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean getSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }
    
    public class UserDefaults {

        private String defaultLocale;
        private Set<LocaleOption> proficientLocalesSet;
        private Set<Locale> displayLocales;

        public UserDefaults() {
        }
        
        public String getDefaultLocale() {
            return defaultLocale;
        }

        public void setDefaultLocale(String defaultLocale) {
            this.defaultLocale = defaultLocale;
        }

        public void setProficientLocales(String proficientLocales) {
        	setProficientLocalesToSet(proficientLocales);
        }

        public String getProficientLocales() {
        	return getProficientLocalesFromSet();
        }

        public void setDisplayProficientLocales(Set<Locale> displayLocales, String proficientLocales) {
            setDisplayLocales(displayLocales);
            setProficientLocales(proficientLocales);
        }

        public void setDisplayProficientLocalesList(Set<Locale> displayLocales, String[] proficientLocalesList) {
            setDisplayLocales(displayLocales);
            setProficientLocalesList(proficientLocalesList);
        }

        public Set<LocaleOption> getProficientLocalesSet() {
            return proficientLocalesSet;
        }

        public void setProficientLocalesSet(Set<LocaleOption> proficientLocalesSet) {
            this.proficientLocalesSet = proficientLocalesSet;
            if (displayLocales != null)
            {
	            String tmpProficientLocales = getProficientLocales();
	            this.proficientLocalesSet = new HashSet<LocaleOption>();
                for (Locale loc : displayLocales)
                {
                	LocaleOption locOption = new LocaleOption();
                	locOption.locale = loc;
                	locOption.language = loc.getDisplayName();
                	locOption.name = loc.toString();
                	this.proficientLocalesSet.add(locOption);
                }
	            setProficientLocales(tmpProficientLocales);
            }
        }

        public Set<Locale> getDisplayLocales() {            
            return displayLocales;
        }

        public void setDisplayLocales(Set<Locale> displayLocales) {
            this.displayLocales = displayLocales;
            if (displayLocales != null)
            {
	            String tmpProficientLocales = getProficientLocales();
	            proficientLocalesSet = new HashSet<LocaleOption>();
                for (Locale loc : displayLocales)
                {
                	LocaleOption locOption = new LocaleOption();
                	locOption.locale = loc;
                	locOption.language = loc.getDisplayName();
                	locOption.name = loc.toString();
                	proficientLocalesSet.add(locOption);
                }
	            setProficientLocales(tmpProficientLocales);
        	}	
        }
        
        private String[] getProficientLocalesList()
        {
        	if (proficientLocalesSet == null)
        		return null;
        	String[] tmpProfLocList = new String[proficientLocalesSet.size()];
        	int idx = 0;
            for (LocaleOption locOption : this.proficientLocalesSet)
            {
            	tmpProfLocList[idx] = locOption.getName();
            }
        	return tmpProfLocList;
        }
        
        private void setProficientLocalesList(String[] proficientLocalesList)
        {
        	if (proficientLocalesList == null)
        		return;
        	clearProficientLocalesSet();
            for (String loc : proficientLocalesList)
            {
            	if (loc == "false")
            		continue;
                for (LocaleOption locOption : this.proficientLocalesSet)
                {
                	if (locOption.getName().compareTo(loc) == 0)
                	{
                   		locOption.selected = true;
                	}
                 }
            }
        }
        
        private String getProficientLocalesFromSet() {
        	if (proficientLocalesSet == null) 
        		return "";
        	StringBuilder retProficientLocales = new StringBuilder();
            for (LocaleOption locOption : proficientLocalesSet)
            {
            	if (locOption.selected)
            	{
                	if (retProficientLocales.length() > 0)
                		retProficientLocales = retProficientLocales.append(",");
                	retProficientLocales = retProficientLocales.append(locOption.getName());
            	}
            }
            return retProficientLocales.toString();
        }
        
        private void setProficientLocalesToSet(String proficientLocales)
        {
        	if (proficientLocales == null || proficientLocales == "")
        	{
        		return;        		
        	}
        	if (proficientLocalesSet == null) 
        	{
        		return;        		
        	}
        	clearProficientLocalesSet();
            String [] locs = proficientLocales.split(",");
            for (String loc : locs)
            {
                for (LocaleOption locOption : proficientLocalesSet)
                {
                	if (locOption.getName().compareTo(loc) == 0)
                	{
                		locOption.selected = true;
                	}
                }
            }
        }
        
        private void clearProficientLocalesSet()
        {
        	if (proficientLocalesSet != null) 
        	{
                for (LocaleOption locOption : proficientLocalesSet)
                {
                	locOption.selected = false;
                }
        	}
        }
    }
}
