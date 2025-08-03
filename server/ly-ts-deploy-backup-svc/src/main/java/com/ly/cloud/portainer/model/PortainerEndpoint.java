package com.ly.cloud.portainer.model;

import com.ly.cloud.portainer.PortainerConnection;
import com.ly.cloud.portainer.PortainerDockerClient;
import com.ly.cloud.portainer.model.rest.RawEndpoint;
import com.spotify.docker.client.DockerClient;

import java.net.URISyntaxException;

public class PortainerEndpoint extends RawEndpoint {

    RawEndpoint endpoint;
    PortainerConnection connection;
    PortainerDockerClient dockerClient;

    public PortainerEndpoint(PortainerConnection connection, RawEndpoint rawEndpoint) {
        this.endpoint = rawEndpoint;
        this.connection = connection;
    }

    public long getId() {
        return endpoint.getId();
    }

    public String getName() {
        return endpoint.getName();
    }

    @Override
    public String toString() {
        return "PortainerEndpoint(id: " + getId() + " name: " + getName() + ")";
    }

    public synchronized DockerClient getDockerClient() {
        if (dockerClient == null) {
            try {
                dockerClient = new PortainerDockerClient(connection, this);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return dockerClient;
    }

}
