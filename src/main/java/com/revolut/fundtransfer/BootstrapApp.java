package com.revolut.fundtransfer;

import com.revolut.fundtransfer.exception.FundTransferException;
import com.revolut.fundtransfer.helper.DbConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;

import static com.revolut.fundtransfer.helper.AppConfigHelper.ACH;

public class BootstrapApp {
    private static final Logger LOGGER = LogManager.getLogger(BootstrapApp.class);
    private static Server _jettyServer;

    public static void main(String[] args) {
       bootstrap(true);
    }

    public static void bootstrap(boolean jettyJoin) {
        LOGGER.info("bootstrapping service");
        try {
            DbConnectionFactory.getDbConnectionFactory(DbConnectionFactory.FACTORY_CODE_H2).createSchemaWithTestData();
            startServer(jettyJoin);
        } catch (FundTransferException ex) {
            LOGGER.error("error while bootstrapping service : ", ex);
        }
    }

    public static void startServer(boolean join) throws FundTransferException {
        LOGGER.info("starting jetty server to serve");
        final ResourceConfig application = new ResourceConfig()
                .packages("com.revolut.fundtransfer")
                .register(JacksonFeature.class);

        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath("/");

        ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(application));
        jerseyServlet.setInitOrder(0);
        contextHandler.addServlet(jerseyServlet, "/revolut/fundtransfer/*");
        jerseyServlet.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "com.revolut.fundtransfer");

        _jettyServer = new Server(Integer.parseInt(ACH.getPropValue("server.port")));
        _jettyServer.setHandler(contextHandler);

        try {
            _jettyServer.start();
            if(join) {
                _jettyServer.join();
            }
        } catch (Exception e) {
            throw new FundTransferException("error while starting up Jetty", e);
        } finally {
            if(join) {
                _jettyServer.destroy();
            }
        }
    }

    public static void stopServer() throws Exception {
        _jettyServer.stop();
    }
}
