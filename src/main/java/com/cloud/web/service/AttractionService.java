package com.cloud.web.service;

import com.cloud.web.dto.api.ApiBoard;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@PropertySource("classpath:key.properties")
public class AttractionService {

    @Value(value = "${key.path}")
    String serviceKey;

    public static ArrayList<ApiBoard> apiBoards = new ArrayList<>();

    public static Set<String> set = new HashSet<>();

    public static ArrayList<String> keys = new ArrayList<>();

    public static Map<String, List<ApiBoard>> map = new HashMap<>();



    public Map<String, List<ApiBoard>> callApiWithJson(String numOfRows, String pageNo ) {

        StringBuffer result = new StringBuffer();

        try {
            String apiUrl = "http://apis.data.go.kr/6260000/AttractionService/getAttractionKr?" +
                    "serviceKey=" + serviceKey +
                    "&numOfRows=" + numOfRows +
                    "&pageNo=" + pageNo;

            URL url = new URL(apiUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            BufferedInputStream bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream, "UTF-8"));

            String returnLine;
            while((returnLine = bufferedReader.readLine()) != null) {
                result.append(returnLine);
            }

            JSONObject jsonObject = XML.toJSONObject(result.toString());

            //jsonObject를 우리가 사용할 특정한 dto로 받아주자.
            JSONObject response = jsonObject.getJSONObject("response");
            JSONObject body =  response.getJSONObject("body");
            JSONObject items =  body.getJSONObject("items");
            JSONArray itemsArray = items.getJSONArray("item");

            //JSONArray to Dto Array
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject obj = itemsArray.getJSONObject(i);

                set.add(obj.getString("GUGUN_NM")); // HastSet에 각각의 게시글을 구분할 수 있는 key값을 Set형태로 받고 있다.

                String uc_seq = String.valueOf(obj.getInt("UC_SEQ"));
                String main_title = obj.getString("MAIN_TITLE");
                String gugun_nm = obj.getString("GUGUN_NM");
                String lat = String.valueOf(obj.getDouble("LAT"));
                String lng = String.valueOf(obj.getDouble("LNG"));
                String place = obj.getString("PLACE");
                String title = obj.getString("TITLE");
                String subtitle = obj.getString("SUBTITLE");
                String addr1 = obj.getString("ADDR1");
                String cntct_tel = obj.getString("CNTCT_TEL");
                String homepage_url = obj.getString("HOMEPAGE_URL");
                String trfc_info = obj.getString("TRFC_INFO");
                String usage_day = obj.getString("USAGE_DAY");
                String hldy_info = obj.getString("HLDY_INFO");
                String usage_day_week_and_time = obj.getString("USAGE_DAY_WEEK_AND_TIME");
                String usage_amount = obj.getString("USAGE_AMOUNT");
                String middle_size_rm1 = obj.getString("MIDDLE_SIZE_RM1");
                String main_img_normal = obj.getString("MAIN_IMG_NORMAL");
                String main_img_thumb = obj.getString("MAIN_IMG_THUMB");
                String itemcntnts = obj.getString("ITEMCNTNTS");

                ApiBoard apiBoard = ApiBoard.builder().uc_seq(uc_seq)
                        .main_title(main_title)
                        .gugun_nm(gugun_nm)
                        .lat(lat)
                        .lng(lng)
                        .place(place)
                        .title(title)
                        .subtitle(subtitle)
                        .addr1(addr1)
                        .cntct_tel(cntct_tel)
                        .homepage_url(homepage_url)
                        .trfc_info(trfc_info)
                        .usage_day(usage_day)
                        .hldy_info(hldy_info)
                        .usage_day_week_and_time(usage_day_week_and_time)
                        .usage_amount(usage_amount)
                        .middle_size_rm1(middle_size_rm1)
                        .main_img_normal(main_img_normal)
                        .main_img_thumb(main_img_thumb)
                        .itemcntnts(itemcntnts).build();

                apiBoards.add(apiBoard);
            } // set에 key 값들이 저장되어 있고 , apiBoards List에는 모든 게시글의 정보가 객체로 하나씩 담겨 있다.

            Object[] list = set.toArray();
            for(Object a : list){
                String s = String.valueOf(a);
                keys.add(s);
            } // set에 있는 key값들을 하나의 List 형태로 변환했다.

            for (String key : keys) {
                List<ApiBoard> collect = apiBoards.stream().filter(e -> e.getGugun_nm().equals(key)).collect(Collectors.toList());
                map.put(key, collect);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }






    public ApiBoard callDetailPage(String key ) {

        ApiBoard apiBoard = null;
        try{
            apiBoard = apiBoards.stream().filter(e -> e.getUc_seq().equals(key)).findFirst().orElse(null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return apiBoard;
    }

















}
