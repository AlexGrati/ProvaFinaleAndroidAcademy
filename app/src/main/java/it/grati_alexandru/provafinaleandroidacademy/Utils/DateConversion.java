package it.grati_alexandru.provafinaleandroidacademy.Utils;

import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by utente4.academy on 14/12/2017.
 */

public class DateConversion {


    public static String formatDateToString(Date date){
        Format format2 = new SimpleDateFormat("HH:mm dd/MM/yy", Locale.ITALY);
        return format2.format(date);
    }

    public static Date formatStringToDate(String dateString){
        if(dateString.charAt(0) == '"' && dateString.charAt(dateString.length()-1) == '"'){
            dateString = dateString.substring(1,dateString.length()-1);
        }
        SimpleDateFormat format = new SimpleDateFormat("HH:mm dd/MM/yy", Locale.ITALY);
        return  format.parse(dateString, new ParsePosition(0));
    }
}
