package org.ws;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import Core.CAServer;
import org.json.*;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;

// Plain old Java Object it does not extend as class or implements
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation.
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML.

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello

@Path("/ws")
//@Produces(MediaType.TEXT_HTML)
//@Consumes(MediaType.TEXT_HTML)
public class ServerResource {
    private static CAServer caserver = new CAServer();

    //import from Example
    // This method is called if TEXT_PLAIN is request
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayPlainTextHello() {
        return "Hello Jersey";
    }

    // This method is called if XML is request
    @GET
    @Produces(MediaType.TEXT_XML)
    public String sayXMLHello() {
        return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey" + "</hello>";
    }

    // This method is called if HTML is request
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String sayHtmlHello() {
        return "<html> " + "<title>" + "Hello Jersey" + "</title>"
                + "<body><h1>" + "Hello Jersey "  + "</body></h1>" + "</html> ";
    }

    @GET
    @Path("/registerPublisherResource/{topic}")
    //@Produces(MediaType.TEXT_HTML)
    //change to post
    public Response registerPublisherResource(@PathParam("topic") final String topic) {
        int id = caserver.registerPublisher(topic);

        JSONStringer json = new JSONStringer();
        try {
            json.object().key("pubtopic").value(id).endObject();
        } catch (Exception e) {}

        System.out.println(json.toString());

        return Response.ok(json.toString()).build();
    }

    @POST
    @Path("/publishContentResource/")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response publishContentResoure(@FormParam("id")  String publisherId,
                                          @FormParam("message") String message)
    {
        caserver.publishContent(Integer.parseInt(publisherId), message);
        caserver.countWordUpdate(message);
        System.out.println("Content published");
        return Response.ok(200).build();
    }


    @GET
    @Path("/count/{topn}")
    public Response countWord(@PathParam("topn") final Integer topn ) {
            String topword;
        topword = caserver.topN(topn);
        return Response.ok(topword).build();}



    @GET
    @Path("/registersubscriberResource/{topic}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerSubscriberResourcce(@PathParam("topic") final String topic){
        Integer tp = caserver.registerSubscriber(topic);
        JSONStringer json = new JSONStringer();
        try{
        json.object().key("id").value(tp).endObject();}
        catch (Exception e){}
        System.out.println(json.toString());
        return Response.ok(json.toString()).build();
    }
    @GET
    @Path("/getlatestcontentResource{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLatestContentResource(@PathParam("id") final Integer subscriberID){
        String res = caserver.getLatestContent(subscriberID);

        JSONStringer json = new JSONStringer();
        try{
        json.object().key("message").value(res).endObject();} catch (Exception e){}
        System.out.println(json.toString());
        return Response.ok(json.toString()).build();
    }


}