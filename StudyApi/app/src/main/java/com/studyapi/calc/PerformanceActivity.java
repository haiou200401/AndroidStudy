package com.studyapi.calc;

import android.net.Uri;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.studyapi.BaseActivity;
import com.studyapi.R;

import java.util.List;

public class PerformanceActivity extends BaseActivity implements View.OnClickListener {
    final String TAG = "gqg:PerformanceActivity";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance);

        findViewById(R.id.id_performance_test_start).setOnClickListener(this);
        findViewById(R.id.id_performance_test_json).setOnClickListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void calcStart() {
        String str = "";
        for (int i = 0; i < 1000 * 1; i++) {
            str += String.valueOf(i);
        }
        Log.e(TAG, str);
        Log.e(TAG, "end out!");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.id_performance_test_start) {
            Debug.startMethodTracing("gqg_debug");
            calcStart();
            Debug.stopMethodTracing();
            //Debug.getLoadedClassCount();
        } else if (v.getId() == R.id.id_performance_test_json) {
            testJson();
        }
    }

/*
    {
        "succ_flag": "1",
            "orderList": [{
        "queue_cancel_flag": null,
                "myTicketList": [{
            "seat_no": "0062",
                    "sequence_no": "EA47753356"
        }],
        "order_date": "20161113140842"
    }],
        "statusCode": 200,
            "responseHeaders": {
        "Server": "Apache-Coyote/1.1",
                "ContentType": "text"
    },
        "responseTime": 635,
            "isSuccessful": true
    }
*/

    public class Ticket {
        String seat_no;
        String sequence_no;
    }

    public class Order {
        String queue_cancel_flag;
        List<Ticket> myTicketList;
        String order_date;
    }

    public class ResponseHeaders {
        String Server;
        String ContentType;
    }

    public class UnfinishOrder {
        String succ_flag2;
        List<Order> orderList;
        String statusCode;
        ResponseHeaders responseHeaders;
        int responseTime;
        boolean isSuccessful;
    }

    public static class OrderList {
        List<Order> orderList;
        String statusCode;
        ResponseHeaders responseHeaders;
        int responseTime;
        boolean isSuccessful;

        public static OrderList decode(String str) {
            Gson gson = new Gson();
            OrderList orderList = gson.fromJson(str, OrderList.class);
            return orderList;
        }
    }


    private void testJson() {
        //String jsString = "{\"succ_flag\":\"1\",\"orderList\":[{\"queue_cancel_flag\":null,\"myTicketList\":[{\"seat_no\":\"0062\",\"sequence_no\":\"EA47753356\"},{\"seat_no\":\"002222\",\"sequence_no\":\"EA2222\"}],\"order_date\":\"20161113140842\"}],\"statusCode\":200,\"responseHeaders\":{\"Server\":\"Apache-Coyote/1.1\",\"ContentType\":\"text\"},\"responseTime\":635,\"isSuccessful\":true}";
        String jsString = "{\"succ_flag\":\"1\",\"error_msg\":\"\",\"orderList\":[{\"queue_cancel_flag\":null,\"check608Code\":null,\"if_deliver\":\"N\",\"pay_flag\":\"Y\",\"tourFlag\":null,\"pay_resign_flag\":\"N\",\"check608Msg\":null,\"confirm_flag\":\"N\",\"print_eticket_flag\":\"N\",\"cancel_flag\":\"Y\",\"ticket_totalnum\":\"1\",\"myTicketList\":[{\"passenger_id_no\":\"132127194511110741\",\"ticket_status_name\":\"待支付\",\"train_date\":\"20161128\",\"seat_name\":\"062号\",\"to_station_telecode\":\"TAP\",\"coach_no\":\"11\",\"pay_limit_time\":\"20161113143842\",\"start_time\":\"1220\",\"confirm_flag\":\"N\",\"print_eticket_flag\":\"N\",\"passenger_name\":\"宋爱菊\",\"coach_name\":\"11\",\"cancel_flag\":\"Y\",\"passenger_id_type_name\":\"二代身份证\",\"limit_time\":\"20161113140842\",\"ticket_price\":\"6.00\",\"seat_type_code\":\"1\",\"return_flag\":\"N\",\"resign_flag\":\"4\",\"ticket_type_code\":\"1\",\"pay_limit_time_rel\":\"1419907\",\"batch_no\":\"1\",\"seat_flag\":\"0\",\"ticket_status_code\":\"i\",\"station_train_code\":\"2257\",\"reserve_time\":\"20161113140842\",\"pay_mode_code\":\"Y\",\"passenger_id_type_code\":\"1\",\"distance\":\"18\",\"arrive_time\":\"1241\",\"from_station_telecode\":\"BJP\",\"eticket_flag\":\"N\",\"flat_msg\":\"\",\"seat_no\":\"0062\",\"sequence_no\":\"EA47753356\"}],\"ticket_price_all\":\"6.00\",\"queue_message\":null,\"ticketInfo\":null,\"resign_flag\":\"4\",\"return_flag\":\"N\",\"order_flag\":\"2\",\"insure_query_no\":\"\",\"sequence_no\":\"EA47753356\",\"order_date\":\"20161113140842\"}],\"statusCode\":200,\"statusReason\":\"OK\",\"responseHeaders\":{\"Server\":\"Apache-Coyote/1.1\",\"Expires\":\"Thu, 01 Jan 1970 00:00:00 GMT\",\"Transfer-Encoding\":\"chunked\",\"X-Powered-By\":\"Servlet 2.5; JBoss-5.0/JBossWeb-2.1\",\"Pragma\":\"no-cache\",\"Date\":\"Sun, 13 Nov 2016 06:15:01 GMT\",\"Content-Type\":\"text/plain;charset=UTF-8\"},\"responseTime\":119,\"totalTime\":119,\"isSuccessful\":true}";

        Gson gson = new Gson();
        try {
            OrderList orderList = OrderList.decode(jsString);
            UnfinishOrder unfinishOrder = gson.fromJson(jsString, UnfinishOrder.class);
            Log.e(TAG, unfinishOrder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
