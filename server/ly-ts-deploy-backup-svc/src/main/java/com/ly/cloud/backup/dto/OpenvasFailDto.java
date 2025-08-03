package com.ly.cloud.backup.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("envelope")
public class OpenvasFailDto {
    @XStreamAlias("version")
    private String version;
    @XStreamAlias("vendor_version")
    private String vendor_version;
    @XStreamAlias("gsad_response")
    private OpenvasGsadResponseDto gsad_response;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVendor_version() {
        return vendor_version;
    }

    public void setVendor_version(String vendor_version) {
        this.vendor_version = vendor_version;
    }

    public OpenvasGsadResponseDto getGsad_response() {
        return gsad_response;
    }

    public void setGsad_response(OpenvasGsadResponseDto gsad_response) {
        this.gsad_response = gsad_response;
    }

    @Override
    public String toString() {
        return "OpenvasFailDto{" +
                "version='" + version + '\'' +
                ", vendor_version='" + vendor_version + '\'' +
                ", gsad_response=" + gsad_response +
                '}';
    }
}
