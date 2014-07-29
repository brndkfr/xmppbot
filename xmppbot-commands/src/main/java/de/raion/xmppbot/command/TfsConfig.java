package de.raion.xmppbot.command;
/*
 * #%L
 * XmppBot Commands
 * %%
 * Copyright (C) 2012 - 2013 Bernd Kiefer <b.kiefer@raion.de>
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.net.URI;
import java.net.URISyntaxException;

@SuppressWarnings("unused")
public class TfsConfig {
    private String tfsHome;
    private String regex;
    private String user;
    private String password;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getTfsHome() {
        return tfsHome;
    }

    public void setTfsHome(String tfsHome) {
        this.tfsHome = tfsHome;
    }

    public URI getIssueURI(String issue) throws URISyntaxException {
        return new URI(tfsHome + "_api/_wit/workitems?_v=5&ids=" + issue);
    }

    public URI getIssueBrowseURI(String issue) throws URISyntaxException {
        return new URI(tfsHome + "_workitems/edit/" + issue);
    }
}
