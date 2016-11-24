package Core;
import java.rmi.Remote;
import java.rmi.RemoteException;



public interface BSDSDiagnoseInterface extends Remote{


    //Get all the content for each topic
    String getContentForEachTopic (String collectionofcontent) throws RemoteException;
}
