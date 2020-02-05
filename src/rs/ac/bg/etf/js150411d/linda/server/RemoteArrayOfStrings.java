package rs.ac.bg.etf.js150411d.linda.server;

import java.rmi.RemoteException;

public class RemoteArrayOfStrings implements RemoteObject {
    private String [] data;

    public RemoteArrayOfStrings () {
        super();
    }
   public RemoteArrayOfStrings (String [] data) {
        super();
        this.data = data;
    }
    @Override
    public String[] getData() throws RemoteException {
        return data;
    }

    @Override
    public void setData(String[] data) throws RemoteException {
        this.data = data;
    }
}
