package org.ws;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dongxu on 11/21/16.
 */


public class Application extends javax.ws.rs.core.Application{
    public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(ServerResource.class);
        return s;
    }
}
