/*
 * Copyright (C) 2003-2014 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.extension.importUsersFromCSV;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MedAmine.
 */
public class UserBean implements Serializable {


    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String groups;
    private String spaces;

    private Map<String, String> additionalInformations = new HashMap<>();

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public String getSpaces() {
        return spaces;
    }

    public void setSpaces(String spaces) {
        this.spaces = spaces;
    }

    public Map<String, String> getAdditionalInformations() {
        return additionalInformations;
    }

    public void setAdditionalInformations(Map<String, String> additionalInformations) {
        this.additionalInformations = additionalInformations;
    }
}
