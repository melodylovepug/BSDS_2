/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Core;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author Ian Gortan
 * Simple client to test subscribing from CAServer over RMI
 */
public class CADiagnose {

    private CADiagnose() {}

    public static void main(String[] args) {
        //args[0] localhost
        //args[1] topic1
        //args[2] topic 2
        String host = (args.length < 1) ? null : args[0];
        try {
            Thread.sleep(7500);
            System.out.println ("Diagnose Client Starter");
            Registry registry = LocateRegistry.getRegistry(host);
            System.out.println ("Connected to registry");
            BSDSDiagnoseInterface CADiagnose = (BSDSDiagnoseInterface) registry.lookup("CAServerDiagnose");
            System.out.println ("Diagnose initialized");
            // add new topics
            while(true) {
                Thread.sleep(1000);
                int len = args.length;
                for (int i = 1; i < len; i++) {
                    String contentarray = args[i];
                    String allcontent = CADiagnose.getContentForEachTopic(contentarray);
                    System.out.println(allcontent);
                }

                System.out.println(" ... Looks like Diagnose Client is working too");
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
