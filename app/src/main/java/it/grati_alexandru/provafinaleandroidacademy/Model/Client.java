package it.grati_alexandru.provafinaleandroidacademy.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by utente4.academy on 14/12/2017.
 */

public class Client extends User implements Serializable {
    public Client() {
        super();
    }

    public Client(String firstName, String lastName, String username, String password, List<Package> packageList) {
        super(firstName, lastName, username, password, packageList);
    }
}
