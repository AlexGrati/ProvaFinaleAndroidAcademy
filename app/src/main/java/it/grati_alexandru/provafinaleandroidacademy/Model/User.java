package it.grati_alexandru.provafinaleandroidacademy.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by utente4.academy on 14/12/2017.
 */

public class User implements Serializable {
    public String firstName;
    public String lastName;
    public String username;
    public String password;
    public List<Package> packageList;

    public User(){
        this.firstName = null;
        this.lastName = null;
        this.username = null;
        this.password = null;
        this.packageList = new ArrayList<>();
    }

    public User(String firstName, String lastName, String username, String password,List<Package> packageList) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.packageList = packageList;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Package> getPackageList() {
        return packageList;
    }

    public void setPackageList(List<Package> packageList) {
        this.packageList = packageList;
    }

    public void addPackageToList(Package pack){
        getPackageList().add(pack);
    }

    public Package findPackageById(int id){
        List<Package> tempPackageList = getPackageList();
        for(Package p : tempPackageList){
            if(p.getId() == id)
                return p;
        }
        return  null;
    }
    public void modifyPackStatus(Package p){
        Package pack = findPackageById(p.getId());
        pack.setStatus(p.getStatus());
    }
}
