package com.airdnd.back;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import service.AirdndUserResInfoService;
import vo.AirdndHomePictureVO;
import vo.AirdndHomeVO;
import vo.AirdndHostVO;
import vo.AirdndUseRuleVO;
import vo.AirdndUserResInfoVO;
import vo.AirdndUserVO;

@Controller
public class UserResInfoController {
   @Autowired
   AirdndUserResInfoService airdndUserResInfoService;
   
   @Autowired
   HttpServletRequest request;
   
   @RequestMapping(value = "/trips/v1", produces = "application/json;charset=utf8")
   @ResponseBody
   public String user_res_info_list(Model model, @RequestParam(value="tab", defaultValue="")String tab) {
      //Login cookie
      HttpSession session = request.getSession();
      Cookie[] cookies = request.getCookies();
      String sessionKey = "";
      int signInIdx = 1; //temp
      String signInEmail = "";
      String signInName = "";
      
      if(cookies == null) {
         System.out.println("not cookies");
      }else {
         for(Cookie cookie : cookies) {
            if("AirdndSES".equals(cookie.getName())) {
               sessionKey = cookie.getValue();
               AirdndUserVO signInVO = (AirdndUserVO)session.getAttribute(sessionKey);
               signInIdx = signInVO.getUser_idx();
               signInEmail = signInVO.getEmail();
               signInName = signInVO.getLast_name() + signInVO.getFirst_name();
            } else {
               System.out.println("not login");
            }
         }
      }//if
      
      //tab에 따른 page 구분
      try {
         tab = URLDecoder.decode(tab, "utf-8");
      } catch (UnsupportedEncodingException e1) {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }
      
      //JSON
      JSONObject res = new JSONObject();         //1
      JSONArray resArr = new JSONArray();         //1-2
      JSONObject userInfo = new JSONObject();      //2
      
      JSONArray resArr2 = new JSONArray();      //3
      JSONObject resInfo = new JSONObject();      //4

      JSONArray rules = new JSONArray();         //5
      JSONObject locationInfo = null;            //6-1
      JSONObject rulesInfo = null;            //6-2
      JSONObject guestInfo = null;            //6-3
      JSONObject guestsInfo = null;            //6-4
      
      AirdndUserVO userVO = airdndUserResInfoService.selectUserInfo(signInIdx);
      
      List<AirdndUserResInfoVO> list = airdndUserResInfoService.selectUserResInfo(signInIdx);
      List<AirdndUserResInfoVO> list1 = new ArrayList<AirdndUserResInfoVO>();
      List<AirdndUserResInfoVO> list2 = new ArrayList<AirdndUserResInfoVO>();
      List<AirdndUserResInfoVO> list3 = new ArrayList<AirdndUserResInfoVO>();
      
      //main picture url
      for(int i = 0; i < list.size(); i++) {
         int home_idx = list.get(i).getHome_idx();
         
         List<AirdndHomePictureVO> mainPictures = airdndUserResInfoService.selectHomeMainPicture(home_idx);
         String url = mainPictures.get(0).getUrl(); //맨 처음 메인 사진만 가져옴
         list.get(i).setUrl(url);
      }//for
      
      SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
      Date date = new Date();
      //Today's date
      int ymd = Integer.parseInt(format.format(date));
      
      String[] checkout_temp = null;
      String checkout = "";
      int checkoutDate = 0;
      
      
      
      if(tab.equalsIgnoreCase("upcoming")) {
         //1. Upcoming reservation list
         for(int i = 0; i < list.size(); i++) {
            if(list.get(i).getIs_canceled() == 0) {
               checkout_temp = list.get(i).getCheckout().split("-");
               checkout = "";
               
               for(int j = 0; j < 3; j++) {
                  checkout += checkout_temp[j];   
               }
               
               checkoutDate = Integer.parseInt(checkout);
               
               if(ymd - checkoutDate <= 0) {
                  //1. Upcoming reservation
                  list1.add(list.get(i));
                  model.addAttribute("list1", list1);
               }
            }
         }//for
         
         for(int i = 0; i < list1.size(); i++) {
            resInfo = new JSONObject(); //4
            rules = new JSONArray();       //5
            locationInfo = new JSONObject(); //6-1
            guestInfo = new JSONObject();    //6-3
            guestsInfo = new JSONObject();    //6-4
            
            int n = 0;
            
            AirdndHostVO hostVO = airdndUserResInfoService.selectHostInfo(list1.get(i).getHome_idx());
            
            resInfo.put("reservationId", list1.get(i).getIdx());
            resInfo.put("homeId", list1.get(i).getHome_idx());
            resInfo.put("hostname", hostVO.getHost_name());
            resInfo.put("homeImage", list1.get(i).getUrl());
            resInfo.put("hostId", hostVO.getIdx());
            resInfo.put("checkin", list1.get(i).getCheckin());
            resInfo.put("checkout", list1.get(i).getCheckout());
            
            AirdndHomeVO homeVO = airdndUserResInfoService.selectHomeInfo(list1.get(i).getHome_idx());
            
            resInfo.put("addr", homeVO.getAddr());
            
            locationInfo.put("lat", homeVO.getLat());
            locationInfo.put("lng", homeVO.getLng());
            
            resInfo.put("location", locationInfo);
            
            resInfo.put("price", homeVO.getPrice());
            
            
            //rules
            List<AirdndUseRuleVO> useRuleList = airdndUserResInfoService.selectUseRuleInfo(list1.get(i).getHome_idx());
            
            for(int j = 0; j < useRuleList.size(); j++) {
               int m = 0;
               
               rulesInfo = new JSONObject();     //6-2
               rulesInfo.put("name", useRuleList.get(j).getUse_rule());
               
               rules.add(m, rulesInfo);
               resInfo.put("rules", rules);
            }
            
            //adult, child, infant
            guestsInfo.put("adult", list1.get(i).getAdult());
            guestsInfo.put("child", list1.get(i).getChild());
            guestsInfo.put("infant", list1.get(i).getInfant());
            resInfo.put("guests", guestsInfo);
            
            resInfo.put("isCanceled", list1.get(i).getIs_canceled());
            resInfo.put("title", homeVO.getTitle());
            
            if(list1.get(i).getGuest_idx() == 0) {
               resInfo.put("withGuest", false);
               resInfo.put("guest", null);
            } else {
               resInfo.put("withGuest", true);
               AirdndUserVO guestVO = airdndUserResInfoService.selectUserInfo(list1.get(i).getGuest_idx());
               
               guestInfo = new JSONObject();
               guestInfo.put("id", guestVO.getUser_idx());
               guestInfo.put("firstName", guestVO.getFirst_name());
               guestInfo.put("lastName", guestVO.getLast_name());
               guestInfo.put("profileImg", guestVO.getProfileImg());
               
               resInfo.put("guest", guestInfo);
            }
            
            resArr2.add(n, resInfo);
            userInfo.put("reservations", resArr2);
            //resArr.add(i, userInfo);
         }//for
         
         userInfo.put("firstName", userVO.getFirst_name());
         userInfo.put("lastName", userVO.getLast_name());
         userInfo.put("profileImg", userVO.getProfileImg());
         userInfo.put("isHost", false);
         res.put("upcoming", userInfo);
         
         model.addAttribute("res", res.toString());
         //System.out.println(resUpcoming.toString());
         
         return res.toString();   
         
         
      } else if(tab.equalsIgnoreCase("past")) {
         //2. Past reservation list
         for(int i = 0; i < list.size(); i++) {
            if(list.get(i).getIs_canceled() == 0) {
               checkout_temp = list.get(i).getCheckout().split("-");
               checkout = "";
               
               for(int j = 0; j < 3; j++) {
                  checkout += checkout_temp[j];   
               }
               
               checkoutDate = Integer.parseInt(checkout);
               
               if(ymd - checkoutDate > 0) {
                  //2. Past reservation
                  list2.add(list.get(i));
                  model.addAttribute("list2", list2);
               }
            }
         }//for
         
         for(int i = 0; i < list2.size(); i++) {
            resInfo = new JSONObject(); //4
            rules = new JSONArray();       //5
            locationInfo = new JSONObject(); //6-1
            guestInfo = new JSONObject();    //6-3
            guestsInfo = new JSONObject();    //6-4
            
            int n = 0;
            
            AirdndHostVO hostVO = airdndUserResInfoService.selectHostInfo(list2.get(i).getHome_idx());
            
            resInfo.put("reservationId", list2.get(i).getIdx());
            resInfo.put("homeId", list2.get(i).getHome_idx());
            resInfo.put("hostname", hostVO.getHost_name());
            resInfo.put("homeImage", list2.get(i).getUrl());
            resInfo.put("hostId", hostVO.getIdx());
            resInfo.put("checkin", list2.get(i).getCheckin());
            resInfo.put("checkout", list2.get(i).getCheckout());
            
            AirdndHomeVO homeVO = airdndUserResInfoService.selectHomeInfo(list2.get(i).getHome_idx());
            
            resInfo.put("addr", homeVO.getAddr());
            
            locationInfo.put("lat", homeVO.getLat());
            locationInfo.put("lng", homeVO.getLng());
            
            resInfo.put("location", locationInfo);
            
            resInfo.put("price", homeVO.getPrice());
            
            
            //rules
            List<AirdndUseRuleVO> useRuleList = airdndUserResInfoService.selectUseRuleInfo(list2.get(i).getHome_idx());
            
            
            for(int j = 0; j < useRuleList.size(); j++) {
               int m = 0;
               
               rulesInfo = new JSONObject();     //6-2
               rulesInfo.put("name", useRuleList.get(j).getUse_rule());
               
               rules.add(m, rulesInfo);
               resInfo.put("rules", rules);
            }
            
            //adult, child, infant
            guestsInfo.put("adult", list2.get(i).getAdult());
            guestsInfo.put("child", list2.get(i).getChild());
            guestsInfo.put("infant", list2.get(i).getInfant());
            resInfo.put("guests", guestsInfo);
            
            resInfo.put("isCanceled", list2.get(i).getIs_canceled());
            resInfo.put("title", homeVO.getTitle());
            
            if(list2.get(i).getGuest_idx() == 0) {
               resInfo.put("withGuest", false);
               resInfo.put("guest", null);
            } else {
               resInfo.put("withGuest", true);
               AirdndUserVO guestVO = airdndUserResInfoService.selectUserInfo(list2.get(i).getGuest_idx());
               
               guestInfo = new JSONObject();
               guestInfo.put("id", guestVO.getUser_idx());
               guestInfo.put("firstName", guestVO.getFirst_name());
               guestInfo.put("lastName", guestVO.getLast_name());
               guestInfo.put("profileImg", guestVO.getProfileImg());
               
               resInfo.put("guest", guestInfo);
            }
            
            resArr2.add(n, resInfo);
            userInfo.put("reservations", resArr2);
            //resArr.add(i, userInfo);
         }//for
         
         userInfo.put("firstName", userVO.getFirst_name());
         userInfo.put("lastName", userVO.getLast_name());
         userInfo.put("profileImg", userVO.getProfileImg());
         userInfo.put("isHost", false);
         res.put("past", userInfo);
         
         model.addAttribute("res", res.toString());
         //System.out.println(resUpcoming.toString());
         
         return res.toString();
         
         
      } else if(tab.equalsIgnoreCase("canceled")) {
         //3. Canceled reservation list
         for(int i = 0; i < list.size(); i++) {
            if(list.get(i).getIs_canceled() != 0) {
               list3.add(list.get(i));
               model.addAttribute("list3", list3);
            }
         }//for
         
         for(int i = 0; i < list3.size(); i++) {
            resInfo = new JSONObject(); //4
            rules = new JSONArray();       //5
            locationInfo = new JSONObject(); //6-1
            guestInfo = new JSONObject();    //6-3
            guestsInfo = new JSONObject();    //6-4
            
            int n = 0;
            
            AirdndHostVO hostVO = airdndUserResInfoService.selectHostInfo(list3.get(i).getHome_idx());
            
            resInfo.put("reservationId", list3.get(i).getIdx());
            resInfo.put("homeId", list3.get(i).getHome_idx());
            resInfo.put("hostname", hostVO.getHost_name());
            resInfo.put("homeImage", list3.get(i).getUrl());
            resInfo.put("hostId", hostVO.getIdx());
            resInfo.put("checkin", list3.get(i).getCheckin());
            resInfo.put("checkout", list3.get(i).getCheckout());
            
            AirdndHomeVO homeVO = airdndUserResInfoService.selectHomeInfo(list3.get(i).getHome_idx());
            
            resInfo.put("addr", homeVO.getAddr());
            
            locationInfo.put("lat", homeVO.getLat());
            locationInfo.put("lng", homeVO.getLng());
            
            resInfo.put("location", locationInfo);
            
            resInfo.put("price", homeVO.getPrice());
            
            
            //rules
            List<AirdndUseRuleVO> useRuleList = airdndUserResInfoService.selectUseRuleInfo(list3.get(i).getHome_idx());
            
            
            for(int j = 0; j < useRuleList.size(); j++) {
               int m = 0;
               
               rulesInfo = new JSONObject();     //6-2
               rulesInfo.put("name", useRuleList.get(j).getUse_rule());
               
               rules.add(m, rulesInfo);
               resInfo.put("rules", rules);
            }
            
            //adult, child, infant
            guestsInfo.put("adult", list3.get(i).getAdult());
            guestsInfo.put("child", list3.get(i).getChild());
            guestsInfo.put("infant", list3.get(i).getInfant());
            resInfo.put("guests", guestsInfo);
            
            resInfo.put("isCanceled", list3.get(i).getIs_canceled());
            resInfo.put("title", homeVO.getTitle());
            
            if(list3.get(i).getGuest_idx() == 0) {
               resInfo.put("withGuest", false);
               resInfo.put("guest", null);
            } else {
               resInfo.put("withGuest", true);
               AirdndUserVO guestVO = airdndUserResInfoService.selectUserInfo(list3.get(i).getGuest_idx());
               
               guestInfo = new JSONObject();
               guestInfo.put("id", guestVO.getUser_idx());
               guestInfo.put("firstName", guestVO.getFirst_name());
               guestInfo.put("lastName", guestVO.getLast_name());
               guestInfo.put("profileImg", guestVO.getProfileImg());
               
               resInfo.put("guest", guestInfo);
            }
            
            resArr2.add(n, resInfo);
            userInfo.put("reservations", resArr2);
            //resArr.add(i, userInfo);
         }//for
         
         userInfo.put("firstName", userVO.getFirst_name());
         userInfo.put("lastName", userVO.getLast_name());
         userInfo.put("profileImg", userVO.getProfileImg());
         userInfo.put("isHost", false);
         res.put("canceled", userInfo);
         
         model.addAttribute("res", res.toString());
         //System.out.println(resUpcoming.toString());
         
         return res.toString();
      }
      return null;
   }
   
   
   
   @RequestMapping(value = "/trips/resCanceled", produces = "application/json;charset=utf8")
   @ResponseBody
   public String user_res_iscanceled() {
      //Login cookie
      HttpSession session = request.getSession();
      Cookie[] cookies = request.getCookies();
      String sessionKey = "";
      int signInIdx = 1; //temp
      String signInEmail = "";
      String signInName = "";
      
      if(cookies == null) {
         System.out.println("not cookies");
      }else {
         for(Cookie cookie : cookies) {
            if("AirdndSES".equals(cookie.getName())) {
               sessionKey = cookie.getValue();
               AirdndUserVO signInVO = (AirdndUserVO)session.getAttribute(sessionKey);
               signInIdx = signInVO.getUser_idx();
               signInEmail = signInVO.getEmail();
               signInName = signInVO.getLast_name() + signInVO.getFirst_name();
            } else {
               System.out.println("not login");
            }
         }
      }//if
      
      int result = airdndUserResInfoService.userResIsCanceled(signInIdx);
      
      JSONObject res = new JSONObject();
      
      if(result == 1) {
         res.put("result", "success");
      } else {
         res.put("result", "fail");
      }
      
      return res.toString();
   }
}