
/**
 *
 * @author Ian Gortan
 */

package Core;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Ian Gortan on 9/19/2016.
 */
public interface BSDSSubscribeInterface extends Remote {
    // registers a new subscriber and resturns a subscriber id
    int registerSubscriber (String topic) throws RemoteException;

    // gets next outstanding message for a subscription
    String getLatestContent(int subscriberID) throws RemoteException;
}