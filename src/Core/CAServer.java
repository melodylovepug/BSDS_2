/**
 * Created by Ian Gortan on 9/19/2016.
 */
package Core;


import com.sun.org.apache.regexp.internal.RE;

import java.sql.*;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import java.lang.String;

//Skeleton of CAServer supporting both BSDS interfaces

public class CAServer implements BSDSPublishInterface, BSDSSubscribeInterface, BSDSDiagnoseInterface {

    //Database connection
    private static Connection connection;
    private static Statement statement;
    private static HashSet<String> ignoreset = new HashSet<>();


    public CAServer() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:/Users/Yuga/Desktop/new/BSDS2new/cw.db");
            statement = connection.createStatement();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        ignoreset.add("of");
        ignoreset.add("this");
        ignoreset.add("and");
        ignoreset.add("a");
        ignoreset.add("the");
        ignoreset.add("is");

    }

    //static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer,String>> CPMap = new ConcurrentHashMap<>();
    //static  ConcurrentHashMap<Integer, String> innerMap = new ConcurrentHashMap<>();

    //count how many times of message left for each topic
    //key is OveralliD, val is sub/pub
    static ConcurrentHashMap<Integer, Integer> bigmap = new ConcurrentHashMap<>();
    static ConcurrentHashMap<Integer, String> smallmao = new ConcurrentHashMap<>();
    static ConcurrentHashMap<Integer, Integer> countForEachTopic = new ConcurrentHashMap<>();
    //count howmany times of live left for each message
    static ConcurrentHashMap<Integer, Integer> deathOfMessage = new ConcurrentHashMap<>();
    // link each Id to the overallid of a message， subscriber as key, Overallid as value
    static ConcurrentHashMap<Integer, Integer> topicwithOverallId = new ConcurrentHashMap<>();
    // use to find the last pos of each topic (only work if only sub is consumer one topic)
    //sub as key, biggestid as value.
    static ConcurrentHashMap<Integer, Integer> topicBiggestId = new ConcurrentHashMap<>();
    //record numbers of subscriber for each topic
    static ConcurrentHashMap<Integer, Integer> topicSubscriberCount = new ConcurrentHashMap<>();
    //overallid with count of subscriber
    static ConcurrentHashMap<Integer, Integer> idwithCount = new ConcurrentHashMap<>();
    static Integer OverallId = 0;

/* TTL
    public class timestamp_Id{
        public int id;
        public long TimeStamp;
        public int sub_pub_id;
        timestamp_Id(int iid, long Ts, int spid)
        {
            id = iid;
            TimeStamp = Ts;
            sub_pub_id = spid;
        }

    }
    public static  class Comp_timer implements Comparator<timestamp_Id>
    {
        public int compare(timestamp_Id id1, timestamp_Id id2)
        {
            return id2.TimeStamp - id1.TimeStamp >=0 ? -1 : 1;
        }

    }
    //queue to store all the messages with all topics
    static public PriorityBlockingQueue<timestamp_Id> timerqueue = new PriorityBlockingQueue<>(1,new Comp_timer());
*/

    public int registerPublisher(String topic) {

        System.out.println(
                "Publisher: " +
                        topic
        );
        int id = topic.hashCode();
        return id;
    }

    // publishes a message to the server

    public void publishContent(int publisherID, String message) {
        // add new topic basic implementation

        synchronized (this) {
            OverallId++;
            smallmao.put(OverallId, message);
            bigmap.put(OverallId, publisherID);
        }
        //record the biggest id
        if (topicBiggestId.containsKey(publisherID)) {
            if (OverallId > topicBiggestId.get(publisherID)) {
                topicBiggestId.replace(publisherID, OverallId);
            }
        } else {
            topicBiggestId.put(publisherID, OverallId);
        }


        //record the smallest id
        if (!topicwithOverallId.containsKey(publisherID))
            topicwithOverallId.put(publisherID, OverallId);

        // increase the count of each topic
        if (!countForEachTopic.containsKey(publisherID)) {
            countForEachTopic.put(publisherID, 0);
        }
        synchronized (this) {
            int oldcount = countForEachTopic.get(publisherID);
            oldcount++;
            countForEachTopic.replace(publisherID, oldcount);
            //System.out.println("Overall id :" + OverallId);
        }

        // System.out.println(bigmap);
        //Timestamp
         /*
            long cur_timestamp = System.currentTimeMillis();
            timestamp_Id time = new timestamp_Id(OverallId, cur_timestamp+TimeToLive, publisherID);
            timerqueue.add(time);
            */
        System.out.println("new message is added to queue");

    }


    public int registerSubscriber(String topic) {
        System.out.println("Topic is  " + topic);
        int id = topic.hashCode();
         /*
         if(topicSubscriberCount.containsKey(id))
         {
             synchronized (this) {
                 Integer count = topicSubscriberCount.get(id);
                 count++;
                 topicSubscriberCount.replace(id, count);
             }
         }
         else*/
        topicSubscriberCount.put(id, 1);
        return id;
    }


    public String getLatestContent(int subscriberID) {

        //System.out.println("Getting content for topic :" +subscriberID);
        //System.out.println("Topic with Overall Id" + topicwithOverallId);
        String messgaereturn;
        messgaereturn = "";
        int lastpos;

        if (countForEachTopic.containsKey(subscriberID) && (countForEachTopic.get(subscriberID) > 0)) {
            //find the last pos of the same topic and loop to find the next id with the same topic

            lastpos = topicwithOverallId.get(subscriberID);
            // System.out.println("Big map" + bigmap);
            messgaereturn = smallmao.get(lastpos);
            System.out.println("Delivered last message");


            //remove if idwithCount.val = topicsubscriberCount.val

            idwithCount.put(subscriberID, 1);
            System.out.println("why messed up ");
            //System.out.println(topicSubscriberCount);
            //System.out.println(subscriberID);
            if (topicSubscriberCount.containsKey(subscriberID)) {
                Integer count1 = idwithCount.get(subscriberID);

                Integer count2 = topicSubscriberCount.get(subscriberID);
                System.out.println(count1 + "count" + count2);
                if (count1 == count2) {
                    //remove when message is delivered to all subscriber
                    System.out.println("Starts delete if count is a match");
                    //deduct the count for each topic
                    synchronized (this) {
                        Integer cur_count = countForEachTopic.get(subscriberID);
                        cur_count--;
                        countForEachTopic.replace(subscriberID, cur_count);
                    }
                    //remove the message from overall so cant be found
                    bigmap.remove(lastpos);
                    smallmao.remove(lastpos);
                    // System.out.println(CPMap);
                    // System.out.println("topic with overall ID:" + topicwithOverallId);
                    // System.out.println("Count for topic:" + countForEachTopic);

                }
            } else {
                messgaereturn = "You are not subscribed for this topic";
            }

            synchronized (this) {
                lastpos++;
                while (!bigmap.containsKey(lastpos) || (bigmap.get(lastpos) != subscriberID) && (lastpos < topicBiggestId.get(subscriberID))) {
                    lastpos++;
                    //System.out.println(lastpos);
                    //System.out.println(topicBiggestId);
                }
            }
            topicwithOverallId.replace(subscriberID, lastpos);
        } else {
            messgaereturn = "No message for this topic ";
        }
        return messgaereturn;
    }


    public String getContentForEachTopic(String allcontectstringtopic) {
        StringBuilder stringBuilder = new StringBuilder();

        int hashtmp = allcontectstringtopic.hashCode();
        Integer count = countForEachTopic.get(hashtmp);
        //rebuild the string.

        stringBuilder.append(allcontectstringtopic);
        stringBuilder.append(":");
        stringBuilder.append(Integer.toString(count));
        stringBuilder.append(",");

        String finalString = stringBuilder.toString();

        return finalString;

    }

    public String topN(Integer topn) {
        String topnresult = "";
        String sqlquery = "SELECT * FROM countword ORDER BY eachcount DESC LIMIT " + topn.toString();
        try {
            ResultSet resultset = statement.executeQuery(sqlquery);

            while (resultset.next()) {
                topnresult += "<Word:>"+resultset.getString("word") + ": " +
                        ((Integer) resultset.getInt("eachcount")).toString();
            }
        } catch (SQLException e) {
        }
        return topnresult;
    }

    public void countWordUpdate(String message) {
        // string : " my name is" each word seperate by " " will be take as next.
        StringTokenizer piece = new StringTokenizer(message, " ");
        ConcurrentHashMap<String, Integer> wordMap = new ConcurrentHashMap<>();
        while (piece.hasMoreTokens()) {
            String word = piece.nextToken();
            if (!ignoreset.contains(word)) {
                if (wordMap.containsKey(word)) {
                    wordMap.replace(word, wordMap.get(word) + 1);
                } else {
                    wordMap.put(word, 1);
                }
            }
        }
        //after count each word in a single piece of message time update database

        try {
            for (ConcurrentHashMap.Entry<String, Integer> now : wordMap.entrySet()) {
                String select = "SELECT * FROM countword WHERE word=\'"
                        + now.getKey() + "\'";
                synchronized (this) {
                ResultSet resultSet = statement.executeQuery(select);
                // if the word already in the database
                    if (resultSet.next()) {
                        Integer count = resultSet.getInt("eachcount");
                        Integer totalcount = resultSet.getInt("total");
                        String sqlquery = "UPDATE countword SET eachcount=\'" +
                                ((Integer) (count + now.getValue())).toString() +
                                "\', total=\'" + ((Integer) (totalcount + 1)).toString()
                                + "\' WHERE word=\'" + now.getKey() + "\'";
                        statement.executeUpdate(sqlquery);
                    } else {
                        String sqlquery = "INSERT INTO countword (word, eachcount, total)" +
                                " VALUES(\'" + now.getKey() + "\', " + now.getValue().toString() +
                                ", 1)";
                        statement.executeUpdate(sqlquery);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }


    //remove for Time-out
    /*
    public boolean remove_everything(timestamp_Id cur) {

                Integer cur_id = cur.id;
                Integer cur_spid = cur.sub_pub_id;
                Integer cur_count = countForEachTopic.get(cur_spid);
                cur_count--;
                //remove from the big map;
                bigmap.remove(cur_id);
                smallmao.remove(cur_id);

                //deduct the count for each topic
                countForEachTopic.replace(cur_spid,cur_count);
                //remove the message from overall



        return true;
    }
*/


/*
    public static void main(String args[]) {

        try {
            CAServer objPub = new CAServer();
            CAServer objSub = new CAServer();
            CAServer objDiag = new CAServer();
            System.out.println("Server Initializing");

            //Thread remover = new Thread(new TimeOutRemover(objPub));
            BSDSPublishInterface pStub = (BSDSPublishInterface) UnicastRemoteObject.exportObject(objPub, 0);
            BSDSSubscribeInterface sStub = (BSDSSubscribeInterface) UnicastRemoteObject.exportObject(objSub, 0);
            BSDSDiagnoseInterface  diag = (BSDSDiagnoseInterface) UnicastRemoteObject.exportObject(objDiag, 0);
            //很多要加东西的低昂
            System.out.println("stubs created ....");
            // Bind the remote object's stub in the local host registry
            LocateRegistry.createRegistry(1099);
                
            Registry registry = LocateRegistry.getRegistry();  
            System.out.println("Ref to Registry ok");  
            try {
                registry.bind("CAServerPublisher", pStub);
                registry.bind("CAServerSubscriber", sStub);
                registry.bind("CAServerDiagnose", diag);
            } catch(Exception e) {
                System.out.println("Caught already bound exception, probably safe to continue in dev mode" + e.toString());
            }
            //
            //remover.start();
            System.err.println("CAServer ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
    */
}
