package mypackage;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.JetService;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sink;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.map.IMap;
import com.hazelcast.sql.SqlResult;
import com.hazelcast.sql.SqlRow;
import hazelcast.rebrick.LegoPart;
import hazelcast.rebrick.LegoSet;
import hazelcast.rebrick.MyFunction;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@WebServlet(name = "mypackage.HelloServlet", urlPatterns = "/search")
public class HelloServlet extends HttpServlet {

    HazelcastInstance client;

    {
        client = HazelcastClient.newHazelcastClient();
        client.getMap("resultMap").destroy();
    }

    //this method is called when post requests are sent to /hello
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("a POST request was sent to /hello");

    }

    //this method is called when get requests are sent to /hello
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("a GET request was sent to /hello");
        String setnum1 = request.getParameter("setnum1");
        String setnum2 = request.getParameter("setnum2");
        String setnum3 = request.getParameter("setnum3");

        response.setContentType("text/html");
        List<String> setNumbers = Arrays.asList(setnum1, setnum2, setnum3);

        JetService jetService = client.getJet();

        IMap<String, LegoSet> sets = client.getMap("sets");
        HashSet<LegoPart> allParts = new HashSet<>();

        for (String setNumber : setNumbers) {
            LegoSet set = sets.get(setNumber);
            allParts.addAll(set.getParts());
        }

        Pipeline pipeline = Pipeline.create();
        pipeline.readFrom(Sources.map("sets")).
                map(new MyFunction(allParts)).
                writeTo((Sink) Sinks.map("resultMap"));
        jetService.newLightJob(pipeline).join();

        SqlResult sqlRows = client.getSql().execute("SELECT * FROM resultMap ORDER BY percentage DESC LIMIT 50");

        header(response);

        response.getWriter().println("<ul>");
        for (SqlRow sqlRow : sqlRows) {
            response.getWriter().println("<li>" + sqlRow.getObject("legoSet") + " " + sqlRow.getObject("percentage") + "</li>");
        }
        response.getWriter().println("</ul>");

        end(response);
    }


    private static void end(HttpServletResponse response) throws IOException {
        response.getWriter().println("</body></html>\n");
    }

    private static void header(HttpServletResponse response) throws IOException {
        response.getWriter().println("<html>\n" +
                "<head>\n" +
                "<title>Rebrickable</title>\n" +
                "</head>\n" +
                "<body bgcolor=white>\n" +
                "\n" +
                "<header>\n" +
                "<div class=\"container\">\n" +
                "<img src=\"https://rebrickable.com/static/img/title.png?1550975899.0560193\" class=\"hidden-xs hidden-sm\" alt=\"Rebrickable - Build with LEGO\" height=\"60px\">\n" +
                "<br>\n" +
                "<img src=\"https://rebrickable.com/static/img/photos/build4.jpg\" class=\"hidden-xs hidden-sm\" alt=\"Rebrickable - Build with LEGO\">\n" +
                "</header>" +
                "\n" +
                "\n" +
                "<h1>Build</h1>\n" +
                "Enter the Set Number for 1-3 sets, to be used as source parts in the Build calculations. Note that these sets are not added to your LEGO collection.\n" +
                "<br>\n" +
                "<br>\n" +
                "<form action=\"search\">\n" +
                "    <input type=\"text\" name=\"setnum1\"><br>\n" +
                "    <input type=\"text\" name=\"setnum2\"><br>\n" +
                "    <input type=\"text\" name=\"setnum3\"><br>\n" +
                "    <input type=\"submit\">\n" +
                "</form>");
        response.getWriter().println("<br>");
        response.getWriter().println("<br>");
    }

}
