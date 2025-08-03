package com.ly.cloud.portainer.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExecParams {

    @JsonProperty("AttachStderr")
    private Boolean AttachStderr;

    @JsonProperty("AttachStdin")
    private Boolean AttachStdin;

    @JsonProperty("AttachStdout")
    private Boolean AttachStdout;

    @JsonProperty("Tty")
    private Boolean Tty;

    @JsonProperty("Cmd")
    private List<String> Cmd;

    @JsonProperty("id")
    private String id;

    @JsonProperty("Id")
    private String websocketId;

    public ExecParams() {
    }

    public ExecParams(String id) {
        this.AttachStderr = true;
        this.AttachStdin = true;
        this.AttachStdout = true;
        this.Cmd = new ArrayList<>();
        this.Cmd.add("bash");
        this.Tty = true;
        this.id = id;
    }
}
