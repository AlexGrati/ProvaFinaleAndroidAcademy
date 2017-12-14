package it.grati_alexandru.provafinaleandroidacademy.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by utente4.academy on 14/12/2017.
 */

public class Courier extends User implements Serializable {
    public Courier() {
        super();
    }

    public Courier(String firstName, String lastName, String username, String password, List<Package> packageList) {
        super(firstName, lastName, username, password, packageList);
    }
}
