package com.ly.cloud.portainer.model;

import com.ly.cloud.portainer.PortainerConnection;
import com.ly.cloud.portainer.model.rest.RawEndpoint;

import java.util.LinkedList;
import java.util.List;

public class PortainerEndpoints {

    List<PortainerEndpoint> endpoints;
    PortainerConnection connection;

    public PortainerEndpoints(PortainerConnection connection, List<RawEndpoint> list) {
        endpoints = new LinkedList<>();
        for (RawEndpoint rawEndpoint : list) {
            endpoints.add(new PortainerEndpoint(connection, rawEndpoint));
        }
    }

    public PortainerEndpoint getEndpointByName(String name) {

        for (PortainerEndpoint endpoint : endpoints) {
            if (endpoint.getName().equals(name))
                return endpoint;
        }
        return null;
    }

    public List<PortainerEndpoint> getRawEndpoints() {
        return endpoints;
    }

    @Override
    public String toString() {
        return endpoints.toString();
    }
}
