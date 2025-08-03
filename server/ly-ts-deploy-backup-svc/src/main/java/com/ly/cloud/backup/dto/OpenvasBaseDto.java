package com.ly.cloud.backup.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class OpenvasBaseDto {
    @XStreamAlias("version")
    protected String version;
    @XStreamAlias("vendor_version")
    protected String vendor_version;
    @XStreamAlias("token")
    protected String token;
//    private String tokena;
    @XStreamAlias("time")
    protected String time;
    @XStreamAlias("timezone")
    protected String timezone;
    @XStreamAlias("login")
    protected String login;
    @XStreamAlias("session")
    protected String session;
    @XStreamAlias("role")
    protected String role;
    @XStreamAlias("i18n")
    protected String i18n;
    @XStreamAlias("client_addressa")
    protected String client_address;
    @XStreamAlias("backend_operation")
    protected String backend_operation;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getI18n() {
        return i18n;
    }

    public void setI18n(String i18n) {
        this.i18n = i18n;
    }

    public String getVendor_version() {
        return vendor_version;
    }

    public void setVendor_version(String vendor_version) {
        this.vendor_version = vendor_version;
    }

    public String getClient_address() {
        return client_address;
    }

    public void setClient_address(String client_address) {
        this.client_address = client_address;
    }

    public String getBackend_operation() {
        return backend_operation;
    }

    public void setBackend_operation(String backend_operation) {
        this.backend_operation = backend_operation;
    }

    @Override
    public String toString() {
        return "OpenvasLoginDto{" +
                "version='" + version + '\'' +
                ", vendor_version='" + vendor_version + '\'' +
                ", token='" + token + '\'' +
//                ", tokena='" + tokena + '\'' +
                ", time='" + time + '\'' +
                ", timezone='" + timezone + '\'' +
                ", login='" + login + '\'' +
                ", session='" + session + '\'' +
                ", role='" + role + '\'' +
                ", i18n='" + i18n + '\'' +
                ", client_address='" + client_address + '\'' +
                ", backend_operation='" + backend_operation + '\'' +
                '}';
    }
}
