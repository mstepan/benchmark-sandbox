import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
-XX:+UnlockCommercialFeatures -XX:+FlightRecorder -XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints
 */
public class Jetty {

    public static void main(String[] args) throws Exception {
        Server server = new Server(7070);
        server.setHandler(new HelloHandler());
        server.start();
        server.join();
    }

    private static class HelloHandler extends DefaultHandler {
        @Override
        public void handle(String s, Request req, HttpServletRequest httpReq,
                           HttpServletResponse httpResp) throws IOException, ServletException {

            req.setHandled(true);

            httpResp.setContentType("text/html");
            httpResp.setCharacterEncoding("UTF-8");
            httpResp.setStatus(HttpServletResponse.SC_OK);

            httpResp.getWriter().println("<h1>Hello World</h1>");
        }
    }


}
