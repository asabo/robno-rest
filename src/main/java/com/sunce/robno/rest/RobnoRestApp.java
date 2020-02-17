package com.sunce.robno.rest;

import com.ansa.util.beans.PostavkeBeanOpce;
import com.sunce.robno.rest.manager.Konstante;
import com.sunce.robno.rest.resource.RobnoRestResourceConfig;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.servlet.ServletContainer;

public class RobnoRestApp {
    
	private static com.ansa.dao.DAOFactory factory; 
	private static Map<UUID, Integer> loggedUsers = new HashMap<UUID, Integer>();
	   
    private Server server;
    
    public void setup() {
        //SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
       server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);
        
        HandlerCollection handlers = new HandlerCollection();
        
        //here we will hold start page for our web service, should it have to serve
        //some public pages (index.html with some basic info for beggining)
        WebAppContext webapp1 = new WebAppContext();
        webapp1.setResourceBase("src/main/webapp");
        webapp1.setContextPath("/html");
        handlers.addHandler(webapp1);

        //rest services will live under '/' and all classes serving them will 
        // be in 'resource' package holding 'v1' prefix with them so that we can build 'v2' later
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        handler.setContextPath("/robno-rest");
        
        handler.addServlet(new ServletHolder(
                        new ServletContainer(
                                new RobnoRestResourceConfig())
                ), "/*");

        handler.addFilter(AuthFilter.class, "/v1/*", EnumSet.of(DispatcherType.REQUEST));
        
        handlers.addHandler(handler);
        server.setHandler(handlers);
       
    }
    
    public void start() throws Exception {
        server.start();
        server.dump(System.err);
        System.out.println("Server started...");
        server.join();
    }
    
    public static void main(String args[]) throws Exception {
        RobnoRestApp theServer = new RobnoRestApp();
        theServer.setup();
        theServer.start();
    }
    
    @Resource
    public final static com.ansa.dao.DAOFactory getDAOFactory()
    {
     if (factory!=null)
             return factory;
     
     factory = com.ansa.dao.DAOFactory.getInstance();
     String sep=System.getProperty( "file.separator" );
     PostavkeBeanOpce post=new PostavkeBeanOpce();
    
     String server=post.getDbServer();
     if (server==null) 
    	 server="127.0.0.1";
     
     System.out.println("Spajanje na bazu: "+server);

     factory.connectToDB("com.mysql.cj.jdbc.Driver",
                       "jdbc:mysql://", server, Konstante.BAZA_PODATAKA + Konstante.BAZA_DEF_ENCODING,
                       Konstante.BAZA_USERNAME,
                       Konstante.BAZA_PASSWORD,
                       Konstante.MIN_DB_VEZA_POOL,
                       Konstante.MAX_DB_VEZA_POOL,
                       vratiKonfiguracijskiDirektorijKorisnika() + sep + Konstante.DB_LOG_FILE, 1.0d);

    return factory;
    }//getDAOFactory
    
    public final static Integer getLoggedUserIdForUUID(UUID uuid) {
    	return loggedUsers.get(uuid);
    }
    
    public final static UUID logUser(Integer userId) {
    	if (userId<1) return null; 
    	
    	UUID uuid = UUID.randomUUID();
    	loggedUsers.put(uuid, userId);
    	return uuid;
    }

    public final static String vratiKonfiguracijskiDirektorijKorisnika()
    {
      String uHome=System.getProperty("user.home");
      String sep=System.getProperty("file.separator");
      String dir=uHome+sep+".sunce";

      return dir;
    }//vratiKonfiguracijskiDirektorijKorisnika

}