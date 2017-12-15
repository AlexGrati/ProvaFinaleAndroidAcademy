package it.grati_alexandru.provafinaleandroidacademy.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by utente4.academy on 14/12/2017.
 */

public class Package implements Serializable {
    private int id;
    private String clientUsername;
    private String courierUsername;
    private String clientName;
    private String warehouseAddress;
    private String clientAddress;
    private String size;
    private String status;
    private Date deliveryDate;

    public Package() {
        this.id = 0;
        this.status = null;
        this.clientUsername = null;
        this.courierUsername = null;
        this.clientName = null;
        this.warehouseAddress = null;
        this.clientAddress = null;
        this.size = null;
        this.deliveryDate = null;
    }

    public Package(int id, String clientUsername, String courierUsername, String clientName, String warehouseAddress, String clientAddress, String size, Date deliveryDate, String status) {
        this.id = id;
        this.clientUsername = clientUsername;
        this.courierUsername = courierUsername;
        this.clientName = clientName;
        this.warehouseAddress = warehouseAddress;
        this.clientAddress = clientAddress;
        this.size = size;
        this.deliveryDate = deliveryDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClientUsername() {
        return clientUsername;
    }

    public void setClientUsername(String clientUsername) {
        this.clientUsername = clientUsername;
    }

    public String getCourierUsername() {
        return courierUsername;
    }

    public void setCourierUsername(String courierUsername) {
        this.courierUsername = courierUsername;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getWarehouseAddress() {
        return warehouseAddress;
    }

    public void setWarehouseAddress(String warehouseAddress) {
        this.warehouseAddress = warehouseAddress;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
