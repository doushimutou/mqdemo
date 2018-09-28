package com.ayt.mqdemo;/**
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @author ayt
 * @date 2018/8/316:05
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author ayt  on 20180803
 */
public class Test {


    public static void main(String[] args){




        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
        System.out.println(zero);
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(dateFormat.format(zero));
        test1(5);

    }


    public static void  test1(Integer i){


        for (int j = 0; j < 5; j++) {
            i=j;
            String mab="{\n" +
                    "    \"productCode\": \"NORMAL_PACKAGE\",\n"+
                    "    \"mailno\""+"："+"\""+i+"\"";
            System.out.println(mab);
        }


    }

}




