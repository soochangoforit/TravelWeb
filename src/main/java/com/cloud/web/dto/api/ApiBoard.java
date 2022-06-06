package com.cloud.web.dto.api;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiBoard {

    public String baseurl="https://map.kakao.com/link/search/";
    private String uc_seq; //콘텐츠ID
    private String main_title; //콘텐츠명
    private String gugun_nm; //구군
    private String lat; //위도
    private String lng; //경도
    private String place; //여행지
    private String title;//제목
    private String subtitle;//부제목
    private String addr1;//주소
    private String cntct_tel;//연락처
    private String homepage_url;//홈페이지
    private String trfc_info;//교통정보
    private String usage_day;//운영일
    private String hldy_info;//휴무일
    private String usage_day_week_and_time;//운영 및 시간
    private String usage_amount;//운영 이용요금 시간
    private String middle_size_rm1;//편의시설
    private String main_img_normal;//이미지URL
    private String main_img_thumb;//썸네일이미지URL
    private String itemcntnts;//상세내용


    @Builder

    public ApiBoard(String uc_seq, String main_title, String gugun_nm,
                    String lat, String lng, String place, String title,
                    String subtitle, String addr1, String cntct_tel, String homepage_url,
                    String trfc_info, String usage_day, String hldy_info, String usage_day_week_and_time,
                    String usage_amount, String middle_size_rm1, String main_img_normal, String main_img_thumb,
                    String itemcntnts) {
        this.uc_seq = uc_seq;
        this.main_title = main_title;
        this.gugun_nm = gugun_nm;
        this.lat = lat;
        this.lng = lng;
        this.place = place;
        this.title = title;
        this.subtitle = subtitle;
        this.addr1 = addr1;
        this.cntct_tel = cntct_tel;
        this.homepage_url = homepage_url;
        this.trfc_info = trfc_info;
        this.usage_day = usage_day;
        this.hldy_info = hldy_info;
        this.usage_day_week_and_time = usage_day_week_and_time;
        this.usage_amount = usage_amount;
        this.middle_size_rm1 = middle_size_rm1;
        this.main_img_normal = main_img_normal;
        this.main_img_thumb = main_img_thumb;
        this.itemcntnts = itemcntnts;
    }
}
