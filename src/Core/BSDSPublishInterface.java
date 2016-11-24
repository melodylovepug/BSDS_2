
/**
 * Created by Ian Gortan on 9/20/2016.
 */

package Core;
import java.rmi.Remote;
import java.rmi.RemoteException;



public interface BSDSPublishInterface extends Remote{
    // returns unique publisherID. Each publisher publishes messages on a single topic
    int registerPublisher(String topic) throws RemoteException;

    // publishes a message to the server
    void publishContent (int publisherID,
                            String message
                         ) throws RemoteException;
}
