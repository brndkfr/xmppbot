package de.raion.xmppbot.command;


import java.net.URI;
import java.net.URISyntaxException;

public class CrucibleConfig {

    private String scheme = "http";

    private String domain = "nb-jira.europe.corp.microsoft.com";

    private String searchPath = "/crucible/rest/quicknav/1.0/search";

    private String authenticationUser;

    private String authenticationPassword;

    private String matchingPattern = ".*([0-9a-fA-F]{40}).*|.*([0-9a-fA-F]{6}).*|.*(SR-\\d+).*";


    public String getMatchingPattern() {
        return matchingPattern;
    }

    public void setMatchingPattern(String matchingPattern) {
        this.matchingPattern = matchingPattern;
    }

    public String getCrucibleDomain() {
        return domain;
    }

    public void setCrucibleDomain(String crucibleDomain) {
        this.domain = crucibleDomain;
    }

    public String getSearchPath() {
        return searchPath;
    }

    public void setSearchPath(String searchPath) {
        this.searchPath = searchPath;
    }

    public String getAuthenticationUser() {
        return authenticationUser;
    }

    public void setAuthenticationUser(String authenticationUser) {
        this.authenticationUser = authenticationUser;
    }

    public String getAuthenticationPassword() {
        return authenticationPassword;
    }

    public void setAuthenticationPassword(String authenticationPassword) {
        this.authenticationPassword = authenticationPassword;
    }


    public URI getQueryPath() throws URISyntaxException {
        return new URI(scheme, domain, searchPath, null);
    }

    public String link(String href){
        return new StringBuffer(scheme).append("://").append(domain)
                    .append("/").append(href).toString();
    }
}
