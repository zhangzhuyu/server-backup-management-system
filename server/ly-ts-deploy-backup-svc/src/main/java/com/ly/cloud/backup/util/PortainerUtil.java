package com.ly.cloud.backup.util;

import com.ly.cloud.portainer.PortainerConnection;
import com.ly.cloud.portainer.model.PortainerEndpoint;
import com.ly.cloud.portainer.model.PortainerEndpoints;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.swarm.Node;
import com.spotify.docker.client.messages.swarm.Swarm;

import java.util.List;

public class PortainerUtil {

    private String url = "http://192.168.35.153:9000/";

    public static void main(String... args) {
        PortainerUtil portainerUtil = new PortainerUtil();
        portainerUtil.get();
    }

    public void get() {
        try {
            PortainerConnection connection = PortainerConnection.connect(url, "admin", "37621040");
            PortainerEndpoints endpoints = connection.getEndpoints();
            System.out.println(endpoints.toString());

            PortainerEndpoint localEndpoint = connection.getEndpoint("primary");
            DockerClient client = localEndpoint.getDockerClient();
            List<Node> list = client.listNodes();
            System.out.println(list.toString());

            List<Container> containers = client.listContainers();
            for (Container container : containers) {
                System.out.println(container.toString());
            }

            Swarm swarm = localEndpoint.getDockerClient().inspectSwarm();
            System.out.println(swarm.id());

            System.out.println(connection.getUsers());

//            String[] command = {"sh", "-c", "ls"};
//            ExecCreation execCreation = client.execCreate(
//                    "cf060632b21b40a4da03a83e6724ef4447201451a65b4e246374332c71cfbb14", command, DockerClient.ExecCreateParam.attachStdout(),
//                    DockerClient.ExecCreateParam.attachStderr());
//            LogStream output = client.execStart(execCreation.id());
//            String execOutput = output.readFully();
//            System.out.println(execOutput);

            String id = connection.exec();
            System.out.println(id);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
