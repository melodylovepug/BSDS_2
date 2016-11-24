/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Core;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.Queue;

import org.json.JSONTokener;
import org.json.JSONObject;


/**
 * @author Ian Gortan
 *         Simple client to test subscribing from CAServer over RMI
 */
public class  CASubClient {
    private CASubClient() {}

    private static String getLatestContent(Integer subscriberId) {
        String message = "";
        try {
            Client client = ClientBuilder.newClient();
            String dst_sub = configuration.baseURL + "/getlatestcontentResource/" + subscriberId.toString();
            Response sub_res = client.target(dst_sub)
                    .request()
                    .accept(MediaType.APPLICATION_JSON)
                    .get(Response.class);

            String jsonMessage = sub_res.readEntity(String.class);
            JSONTokener jsonMessageTokener = new JSONTokener(jsonMessage);
            JSONObject jsonMessageObject;

            jsonMessageObject = (JSONObject) jsonMessageTokener.nextValue();

            message = jsonMessageObject.getString("message");

        }
        catch (Exception e) {}
        return message;

    }
/*
    private String topic;
    private int num;
    private String host;
    SubThread(String host, String topic, int num) {
        this.host = host;
        this.topic = topic;
        this.num = num;
    }


    public void run(){

        try {
            System.out.println("Subscriber Client Starter");
            Registry registry = LocateRegistry.getRegistry(host);
            System.out.println("Connected to registry");
            BSDSSubscribeInterface CAServerStub = (BSDSSubscribeInterface) registry.lookup("CAServerSubscriber");
            System.out.println("Stub initialized");
*/
/*
args[0] local host, args[1] Topic, arg[2] number of message want, args[3] topic...keeps going like that
 */

    /*

                    CAServerStub.registerSubscriber(topic);
                    Integer countOfMessageRecieved = 0;

                    long requestNew = 100;

                    Long currentTimeStamp = System.currentTimeMillis();
                    //60 seconds
                int hashtopic = topic.hashCode();

                    for (int i = 0; i < num; i++) {
                        String messagereturn = CAServerStub.getLatestContent(hashtopic);

                        while ( messagereturn.equals("No message for this topic ") && (requestNew < 60000)) {
                            System.out.println("Sleep Time：" + requestNew);
                            requestNew *= 2;
                            Thread.sleep(requestNew);
                            messagereturn = CAServerStub.getLatestContent(hashtopic);
                        }
                        if (requestNew >= 60000)
                            break;
                        //System.out.println("Message return #:" + countOfMessageRecieved);
                        countOfMessageRecieved++;
                        requestNew = 100;
                    }


                    if (countOfMessageRecieved != num)
                        System.out.println("Failed to request all content, part of missing");

                        Long walltime = System.currentTimeMillis() - currentTimeStamp;
                        System.out.println("Recieving message on topic : " +  topic);
                        System.out.println("Total wall time to receive all message " + walltime.toString() + "ms.");
                        System.out.println("Total message received for this topic: " + countOfMessageRecieved);
                        System.out.println(" ... Looks like Subscribe Client is working too");




            }catch (Exception e) {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
            }
        }}
    */
    public static void main(String[] args) {


        String host = (args.length < 1) ? null : args[0];
        try {
            System.out.println("Subscriber Client Starter");
            Client client = ClientBuilder.newClient();
            System.out.println("Connected to registry");
            System.out.println("Stub initialized");
            //fix to recursion version for both sub and pub
            Integer len = (args.length - 1) / 2;

            Long currentTimeStamp = System.currentTimeMillis();
            for (int i = 0; i < len; i++) {
                //args[0], args[i * 2 + 1], Integer.parseInt(args[i * 2 + 2])
                String pwd = configuration.baseURL + "/registersubscriberResource/" + args[i * 2 + 1];
                Response res = client.target(pwd).request().accept(MediaType.APPLICATION_JSON).get(Response.class);

                String jsonText = res.readEntity(String.class);

                JSONTokener jsonTokener = new JSONTokener(jsonText);
                JSONObject studentJSONObject;
                //studentJSONObject = (JSONObject) jsonTokener.nextValue();
                //Integer id = studentJSONObject.getInt("id");
                Integer topichash = args[i * 2 + 1].hashCode();
                System.out.println("Sub id = " + Integer.toString(topichash));
                long requestNew = 100;
                Integer countOfMessageRecieved = 0;

                for (int j = 0; j < Integer.parseInt(args[i * 2 + 2]); j++) {
                    String messagereturn = getLatestContent(topichash);

                    while (messagereturn.equals("No message for this topic ") && (requestNew < 60000)) {
                        System.out.println("Sleep Time：" + requestNew);
                        requestNew *= 2;
                        Thread.sleep(requestNew);
                        messagereturn = getLatestContent(topichash);
                    }
                    if (requestNew >= 60000)
                        break;
                    //System.out.println("Message return #:" + countOfMessageRecieved);
                    countOfMessageRecieved++;
                    requestNew = 100;
                }
                if (countOfMessageRecieved != Integer.parseInt(args[2]))
                    System.out.println("Failed to request all content, part of missing");
                System.out.println("Message return #:" + countOfMessageRecieved);
                System.out.println("Recieving message on topic : " + args[i * 2 + 1]);
                System.out.println("Total message received for this topic: " + countOfMessageRecieved.toString());
                System.out.println(" ... Looks like Subscribe Client is working too");
            }
                Long walltime = System.currentTimeMillis() - currentTimeStamp;
                System.out.println("Total wall time to receive all message " + walltime.toString() + "ms.");

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

    }


        /*
        if (args.length < 3) {
            System.out.println("Not enough args");
            return;
        }
         Queue<Thread> threadQueue = new LinkedList<>();
        Integer len = (args.length - 1) / 2;


        for (int i = 0; i < len; i++) {
            Thread t = new SubThread(args[0], args[i * 2 + 1], Integer.parseInt(args[i * 2 + 2]));
            threadQueue.add(t);
            t.start();

        }


        for (int i = 0; i < len; i++) {
            Thread t = threadQueue.poll();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(len.toString() + " Publish Sub(s) have done their work");
    }
    */


}


