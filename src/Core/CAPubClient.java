package Core;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * @author Ian Gortan
 */

import java.util.Queue;
import java.util.LinkedList;

// Simple client to test publishing to CAServer over RMI
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONTokener;
import org.json.JSONObject;
import org.json.simple.*;

class PubThread extends Thread {

    private static void connectClient(Integer publisherId, String message){
        Client client = ClientBuilder.newClient();
        String pwdPub = configuration.baseURL+"/publishContentResource/";
        Form form = new Form();
        form.param("id", publisherId.toString());
        form.param("message", "Message is "+ message);


        Response pub_res = client.target(pwdPub)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED),
                        Response.class);
    }

    //private String[] argument;
    private String topic;
    private int pos;
    private String host;

    PubThread(String host1, String topic1, int pos1)
    {
        topic = topic1;
        pos = pos1;
        host = host1;
    }

    public void run() {
        try {
          /*  try
            {sleep(5000);} catch(InterruptedException e){}
*/
            Client client = ClientBuilder.newClient();
            System.out.println("Publisher Client Starter");
            System.out.println("Connected to registry");
            System.out.println("Stub initialized");

            //argument[2] topic, argument[3] numbers
            String pwd = configuration.baseURL+"/registerPublisherResource/"+topic+"/"+pos;
            Response response = client.target(pwd).request().accept(MediaType.APPLICATION_JSON).get(Response.class);
            String jsonText = response.readEntity(String.class);
            //System.outpos.posprintln("Data received:" + jsonText);
            JSONTokener jsonTokener = new JSONTokener(jsonText);
            JSONObject studentJSONObject;
            //studentJSONObject = (JSONObject) jsonTokener.nextValue();
            //Integer intId = studentJSONObject.getInt("id");
            Integer TimesToSend = pos;
            //Long currentTimeStamp = System.currentTimeMillis();
            for (Integer i = 0; i < TimesToSend; i++) {
                connectClient(topic.hashCode(), "Message"+i.toString());

                //System.out.println("#" + i.toString() + " message sent");
            }
            //Long walltime = System.currentTimeMillis() - currentTimeStamp;
            System.out.println("Totally sent messages for " + pos + ": " + TimesToSend.toString());
           // System.out.println("Totally cost " + walltime.toString() + " ms");
            /*
topic[0] local host, topic[1] number of threads, topic[2] Topic, arg[3] number of post, topic[4[ topic...keeps going like that
 */
            /*Registry registry = LocateRegistry.getRegistry(host, 1099);
            BSDSPublishInterface CAServerStub = (BSDSPublishInterface) registry.lookup("CAServerPublisher");

            Integer countOfMessageSend = 0;
            int topic1 = CAServerStub.registerPublisher(topic);
            System.out.println("Pub id = " + topic);


            Long currentTimeStamp = System.currentTimeMillis();

            for (int nums = 0; nums < pos; nums++) {
                String message = Integer.toString(nums);
                CAServerStub.publishContent(topic1, message);
                countOfMessageSend++;
            }

            if (countOfMessageSend == pos) {
                Long walltime = System.currentTimeMillis() - currentTimeStamp;
                System.out.println("Total wall time to sent all message " + walltime.toString() + "ms.");
                System.out.println("Totall messages send: " + countOfMessageSend);
                System.out.println("Publish Client is working");
            } else System.out.println("Something is wrong");
            */
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}


public class CAPubClient {

    private CAPubClient() {
    }

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Not enough arguments, exit");
            return;
        }
        final Integer numOfThreads = Integer.parseInt(args[1]);

        int len = (args.length - 2) / 2;
        Long currentTimeStamp = System.currentTimeMillis();

        Queue<Thread> threadQueue = new LinkedList<>();
        for (int i = 0; i < len; i++) {
            for (int x = 0; x < numOfThreads; x++) {

                Thread t = new PubThread( args[i * 2 + 2], args[0], Integer.parseInt(args[i * 2 + 3]));
                threadQueue.add(t);
                t.start();
            }
        }


        for (int i = 0; i < numOfThreads * len; i++) {
            Thread t = threadQueue.poll();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Long walltime = System.currentTimeMillis() - currentTimeStamp;
        System.out.println("Totally cost " + walltime.toString() + " ms");
        System.out.println(numOfThreads.toString() + " Publish Client(s) have done their work");
    }
}