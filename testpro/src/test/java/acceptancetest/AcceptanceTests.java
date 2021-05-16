package acceptancetest;

import com.iiichz.microservicedevtest.devboot.ServerStart;
import com.iiichz.microservicedevtest.jettymock.JettyMockServer;

import com.intuit.karate.KarateOptions;
import com.intuit.karate.junit4.Karate;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.core.env.AbstractEnvironment;

/**
 *
 * 测试框架启动类，所有测试启动类均需调用里面的init方法
 *
 * @author g00563351
 * @since Agile Controller V300R003C10, 2020-11-28
 *
 */
@RunWith(Karate.class)
@KarateOptions(tags = {"~@ignore"})
public class AcceptanceTests {

    private static ServerStart server;
    private static JettyMockServer mockServer = new JettyMockServer();

    @BeforeClass
    public static void acceptanceTestsSetup() throws Exception {
        mockServer.startMockServer(32018);
        startServer();
    }

    private static void startServer() throws Exception {
        if (server == null) { // keep spring boot side alive for all tests including package 'mock'
            server = new ServerStart();            System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "at");
            server.start(new String[] {}, false);
        }
        System.setProperty("demo.server.port", server.getPort() + "");
    }
}