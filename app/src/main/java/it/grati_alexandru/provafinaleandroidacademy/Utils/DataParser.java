package it.grati_alexandru.provafinaleandroidacademy.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.grati_alexandru.provafinaleandroidacademy.Model.Courier;
import it.grati_alexandru.provafinaleandroidacademy.Model.Package;

/**
 * Created by utente4.academy on 15/12/2017.
 */

public class DataParser {

    public static List<Integer> getPackagesId(String packageJSON){
        List<Integer> packageIdList = new ArrayList<>();
        try{
            JSONObject packageObject = new JSONObject(packageJSON);
            Iterator iterator = packageObject.keys();
            while(iterator.hasNext()){
                String key = (String) iterator.next();
                if(!key.equals("LastModDate")) {
                    int id = Integer.parseInt(parseString(key));
                    packageIdList.add(id);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return packageIdList;
    }

    public static List<Courier> cerateCourierList(String jsonResponse){
        List<Courier> courierList = new ArrayList<>();
            try{
                JSONObject courierObject = new JSONObject(jsonResponse);
                Iterator iterator = courierObject.keys();
                while (iterator.hasNext()){
                    String key = (String) iterator.next();
                    JSONObject courier = courierObject.getJSONObject(key);
                    Iterator courierIterator = courier.keys();
                    Courier c = new Courier();
                    while (courierIterator.hasNext()){
                        String data = (String) courierIterator.next();
                        String val = courier.getString(data);

                        switch (data){
                            case "FirstName":
                                c.setFirstName(val);
                                break;
                            case  "LastName":
                                c.setLastName(val);
                                break;
                        }

                        if(c.getUsername() == null){
                            c.setUsername(key);
                        }
                    }
                    courierList.add(c);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        return courierList;
    }

    public static List<Package> createPackageListFromId(String jsonResponse, List<Integer> idList){
        List<Package> packageList = new ArrayList<>();
        try{
            JSONObject packageObject = new JSONObject(jsonResponse);
            Iterator iterator = packageObject.keys();
            while(iterator.hasNext()){
                String key = (String) iterator.next();
                key = parseString(key);
                int id = Integer.parseInt(key);
                if(isContained(id,idList)) {
                    JSONObject pack = packageObject.getJSONObject(key);
                    Iterator packageIterator = pack.keys();
                    Package p = new Package();
                    while (packageIterator.hasNext()) {
                        String data = (String) packageIterator.next();
                        String val = pack.getString(data);
                        switch (data) {
                            case "ClientName":
                                p.setClientName(val);
                                break;
                            case "ClientUsername":
                                p.setClientUsername(val);
                                break;
                            case "DeliveryAddress":
                                p.setClientAddress(val);
                                break;
                            case "DeliveryDate":
                                p.setDeliveryDate(DateConversion.formatStringToDate(val));
                                break;
                            case "Size":
                                p.setSize(val);
                                break;
                            case "Status":
                                p.setStatus(val);
                                break;
                            case "WarehouseAddress":
                                p.setWarehouseAddress(val);
                                break;
                        }
                        if (p.getId() == 0) {

                            p.setId(id);
                        }
                    }
                    packageList.add(p);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return  packageList;
    }

    public static boolean isContained(int id, List<Integer> integerList){
        for (Integer i : integerList){
            if(id == i){
                return true;
            }
        }
        return false;
    }

    public static String parseString(String string){
        int length = string.length();
        if(string.charAt(0) == '"' && string.charAt(length-1) == '"'){
            return string.substring(1,length-1);
        }
        return string;
    }
}
