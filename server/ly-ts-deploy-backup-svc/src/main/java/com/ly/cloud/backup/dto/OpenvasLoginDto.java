package com.ly.cloud.backup.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.http.cookie.Cookie;

@XStreamAlias("envelope")
public class OpenvasLoginDto extends OpenvasBaseDto {
    private String massage;
    private Cookie sid;

    public Cookie getSid() {
        return sid;
    }

    public void setSid(Cookie sid) {
        this.sid = sid;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }


    @Override
    public String toString() {
        return "OpenvasLoginDto{" +
                "version='" + version + '\'' +
                ", vendor_version='" + vendor_version + '\'' +
                ", token='" + token + '\'' +
                ", message='" + massage + '\'' +
                ", sid='" + sid + '\'' +
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
