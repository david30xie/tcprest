package io.tcprest.test.smoke;

import io.tcprest.client.TcpRestClientFactory;
import io.tcprest.server.SingleThreadTcpRestServer;
import io.tcprest.test.HelloWorld;
import io.tcprest.test.HelloWorldResource;

/**
 * @author Weinan Li
 * @date 08 06 2012
 */
public class ConcurrentTest extends TcpClientFactorySmokeTest {

    private TcpRestClientFactory factory;

    public void multipleClientsTest() {
        tcpRestServer.addResource(HelloWorldResource.class);

        factory =
                new TcpRestClientFactory(HelloWorld.class, "localhost",
                        ((SingleThreadTcpRestServer) tcpRestServer).getServerSocket().getLocalPort());

        for (int i = 0; i < 100; i++) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        HelloWorld client = (HelloWorld) factory.getInstance();
                        client.helloWorld();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();

        }
    }
}