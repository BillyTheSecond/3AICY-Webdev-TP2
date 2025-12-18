
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ServeurRmi {
    try {
        LocateRegistry.createRegistry(1099);
        System.out.println("Annuaire créé");

        // ObjetDistant obj = new ObjectDistant()
        // Naming.bind("getData",obj);
        // System.out.println("Services enregistrés");



    } catch (RemoteException e1) {
        e1.printStackTrace();
    } catch (MalformedURLException e2) {
        e2.printStackTrace();
    } catch (AlreadyBoundException e3) {
        
        e3.printStackTrace();
    }
}


