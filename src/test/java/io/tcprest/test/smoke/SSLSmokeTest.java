package io.tcprest.test.smoke;

import io.tcprest.client.TcpRestClientFactory;
import io.tcprest.server.SSLParam;
import io.tcprest.server.SingleThreadTcpRestServer;
import io.tcprest.server.TcpRestServer;
import io.tcprest.test.HelloWorld;
import io.tcprest.test.HelloWorldResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * @author Weinan Li
 * @created_at 08 25 2012
 */
public class SSLSmokeTest {
    protected TcpRestServer tcpRestServer;

    @Before
    public void startTcpRestServer() throws Exception {
        SSLParam serverSSLParam = new SSLParam();
        serverSSLParam.setTrustStorePath("/Users/weli/projs/tcprest/src/main/resources/server_ks");
        serverSSLParam.setKeyStorePath("/Users/weli/projs/tcprest/src/main/resources/server_ks");
        serverSSLParam.setKeyStoreKeyPass("123123");
        serverSSLParam.setNeedClientAuth(true);

        tcpRestServer = new SingleThreadTcpRestServer(Math.abs(new Random().nextInt()) % 10000 + 8000, serverSSLParam);
        tcpRestServer.up();
    }

    @After
    public void stopTcpRestServer() throws Exception {
        tcpRestServer.down();
    }

    @Test
    public void testTwoWayHandShake() {

        tcpRestServer.addResource(HelloWorldResource.class);

        SSLParam clientSSLParam = new SSLParam();
        clientSSLParam.setTrustStorePath("/Users/weli/projs/tcprest/src/main/resources/client_ks");
        clientSSLParam.setKeyStorePath("/Users/weli/projs/tcprest/src/main/resources/client_ks");
        clientSSLParam.setKeyStoreKeyPass("456456");
        clientSSLParam.setNeedClientAuth(true);

        TcpRestClientFactory factory =
                new TcpRestClientFactory(HelloWorld.class, "localhost", tcpRestServer.getServerPort(), null, clientSSLParam);

        HelloWorld client = (HelloWorld) factory.getInstance();

        assertEquals("Hello, World", client.sayHelloTo("World"));

    }
}
