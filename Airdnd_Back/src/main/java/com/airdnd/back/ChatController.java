package com.airdnd.back;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import service.AirdndChatService;
import vo.AirdndChatMsgsVO;
import vo.AirdndChatVO;
import vo.AirdndHostVO;
import vo.AirdndUserResInfoVO;
import vo.AirdndUserVO;

@Controller
public class ChatController {
   ObjectMapper mapper = new ObjectMapper();
   
   @Autowired
   AirdndChatService airdndChatService;
   
   @Autowired
   HttpServletRequest request;
   
   @RequestMapping(value= {"/guest/inbox"}, produces="application/json;charset=utf8")
   @ResponseBody
   public String chat_list(Model model) {
      //Login cookie
      HttpSession session = request.getSession();
      Cookie[] cookies = request.getCookies();
      String sessionKey = "";
      int signInIdx = 1; //temp
      String signInName = "MR";
      String signInProfileImg = "https://a0.muscache.com/defaults/user_pic-225x225.png?im_w=720";
      
      if(cookies == null) {
         System.out.println("not cookies");
      }else {
         for(Cookie cookie : cookies) {
            if("AirdndSES".equals(cookie.getName())) {
               sessionKey = cookie.getValue();
               AirdndUserVO signInVO = (AirdndUserVO)session.getAttribute(sessionKey);
               signInIdx = signInVO.getUser_idx();
               signInName = signInVO.getFirst_name();
               signInProfileImg = signInVO.getProfileImg();
               
            } else {
               System.out.println("not login");
            }
         }
      }//if
      
      //Final data
      JSONObject res = new JSONObject();         //1
      JSONObject resFilter1 = new JSONObject();   //2
      JSONObject resFilter2 = new JSONObject();   //2
      JSONObject resFilter3 = new JSONObject();   //2

      JSONArray secondArr1 = new JSONArray();      //3
      JSONArray secondArr2 = new JSONArray();      //3
      JSONArray secondArr3 = new JSONArray();      //3
      JSONObject fourthlists = new JSONObject();   //4

      JSONArray info = new JSONArray();         //5
      JSONObject contents = new JSONObject();      //6-1
      JSONObject chatHistory = new JSONObject();   //6-2
      
      //select all chat list
      List<AirdndChatVO> listAll = airdndChatService.selectChatListAll(signInIdx);
      List<AirdndChatVO> listHidden = airdndChatService.selectChatListHidden(signInIdx);
      List<AirdndChatVO> listUnread = airdndChatService.selectChatListUnread(signInIdx);
      
      //Select latest msg in User's chat list - all
      AirdndChatMsgsVO latestMsgVO = new AirdndChatMsgsVO();
      
      List<AirdndHostVO> hostList = new ArrayList<AirdndHostVO>();
      AirdndUserResInfoVO userResInfoVO = new AirdndUserResInfoVO();
      List<AirdndChatMsgsVO> chattingListA = new ArrayList<AirdndChatMsgsVO>();
      List<AirdndChatMsgsVO> chattingListH = new ArrayList<AirdndChatMsgsVO>();
      List<AirdndChatMsgsVO> chattingListU = new ArrayList<AirdndChatMsgsVO>();
      
      
      for(int i = 0; i < listAll.size(); i++) {
         fourthlists = new JSONObject();    //5
         info = new JSONArray();         //6
         contents = new JSONObject();   //7-1
         
         int n = 0;
         
         //Host Info
         hostList = airdndChatService.selectHostList(signInIdx, listAll.get(0).getAll_hidden_unread());
         userResInfoVO = airdndChatService.selectUserResInfo(signInIdx, hostList.get(i).getIdx());
         latestMsgVO = airdndChatService.selectLatestMsg(signInIdx, hostList.get(i).getIdx(), listAll.get(0).getAll_hidden_unread());
         chattingListA = airdndChatService.selectMsgList(signInIdx, hostList.get(i).getIdx(), listAll.get(0).getAll_hidden_unread());
         
         fourthlists.put("id", hostList.get(i).getIdx());
         fourthlists.put("reservationId", userResInfoVO.getIdx());
         fourthlists.put("state", listAll.get(0).getAll_hidden_unread());
         
         //hostname - "님"
         String realName = hostList.get(i).getHost_name().replace("님", "");
         fourthlists.put("hostname", realName);
         
         //contents
         contents.put("hostProfileImg", hostList.get(i).getHost_profileImg());
         contents.put("lastMsg", latestMsgVO.getContent());
         contents.put("lastMsgDate", latestMsgVO.getSend_date_time());
         contents.put("isCanceled", userResInfoVO.getIs_canceled());
         contents.put("checkin", userResInfoVO.getCheckin());
         contents.put("checkout", userResInfoVO.getCheckout());
         
         //info.add(n, contents);
         fourthlists.put("contents", contents);
         
         //chatHistory
         for(int j = 0; j < chattingListA.size(); j++) {
            chatHistory = new JSONObject();
                           
            chatHistory.put("id", chattingListA.get(j).getIdx());
            chatHistory.put("userId", chattingListA.get(j).getFrom_idx());
            
            if(hostList.get(i).getIdx() == chattingListA.get(j).getFrom_idx()) {
               String name = hostList.get(i).getHost_name().replace("님", "");
               chatHistory.put("name", name);
            } else if(signInIdx == chattingListA.get(j).getFrom_idx()) {
               chatHistory.put("name", signInName);
            }
            
            chatHistory.put("timeStamp", chattingListA.get(j).getSend_date_time());
            chatHistory.put("text", chattingListA.get(j).getContent());
            
            info.add(n, chatHistory);
            fourthlists.put("chatHistory", info);
         }
         
         secondArr1.add(i, fourthlists);
         
         res.put("all", secondArr1);
      }
   
      for(int i = 0; i < listHidden.size(); i++) {
         fourthlists = new JSONObject();    //4
         info = new JSONArray();         //5
         contents = new JSONObject();   //6-1
         
         int n = 0;
         
         //Host Info
         hostList = airdndChatService.selectHostList(signInIdx, listHidden.get(0).getAll_hidden_unread());
         userResInfoVO = airdndChatService.selectUserResInfo(signInIdx, hostList.get(i).getIdx());
         latestMsgVO = airdndChatService.selectLatestMsg(signInIdx, hostList.get(i).getIdx(), listHidden.get(0).getAll_hidden_unread());
         chattingListH = airdndChatService.selectMsgList(signInIdx, hostList.get(i).getIdx(), listHidden.get(0).getAll_hidden_unread());
         
         fourthlists.put("id", hostList.get(i).getIdx());
         fourthlists.put("reservationId", userResInfoVO.getIdx());
         fourthlists.put("state", listHidden.get(0).getAll_hidden_unread());
         
         String realName = hostList.get(i).getHost_name().replace("님", "");
         fourthlists.put("hostname", realName);
         
         //contents
         contents.put("hostProfileImg", hostList.get(i).getHost_profileImg());
         contents.put("lastMsg", latestMsgVO.getContent());
         contents.put("lastMsgDate", latestMsgVO.getSend_date_time());
         contents.put("isCanceled", userResInfoVO.getIs_canceled());
         contents.put("checkin", userResInfoVO.getCheckin());
         contents.put("checkout", userResInfoVO.getCheckout());
         
         //info.add(n, contents);
         fourthlists.put("contents", contents);
         
         //chatHistory
         for(int j = 0; j < chattingListH.size(); j++) {
            chatHistory = new JSONObject();
                           
            chatHistory.put("id", chattingListH.get(j).getIdx());
            chatHistory.put("userId", chattingListH.get(j).getFrom_idx());
            
            if(hostList.get(i).getIdx() == chattingListH.get(j).getFrom_idx()) {
               String name = hostList.get(i).getHost_name().replace("님", "");
               chatHistory.put("name", name);
            } else if(signInIdx == chattingListH.get(j).getFrom_idx()) {
               chatHistory.put("name", signInName);
            }
            
            chatHistory.put("timeStamp", chattingListH.get(j).getSend_date_time());
            chatHistory.put("text", chattingListH.get(j).getContent());
            
            info.add(n, chatHistory);
            fourthlists.put("chatHistory", info);
         }

         secondArr2.add(i, fourthlists);
         
         res.put("hidden", secondArr2);
      }
   
      for(int i = 0; i < listUnread.size(); i++) {
         fourthlists = new JSONObject();    //5
         info = new JSONArray();         //6
         contents = new JSONObject();   //7-1
         
         int n = 0;
         
         //Host Info
         hostList = airdndChatService.selectHostList(signInIdx, listUnread.get(0).getAll_hidden_unread());
         userResInfoVO = airdndChatService.selectUserResInfo(signInIdx, hostList.get(i).getIdx());
         latestMsgVO = airdndChatService.selectLatestMsg(signInIdx, hostList.get(i).getIdx(), listUnread.get(0).getAll_hidden_unread());
         chattingListU = airdndChatService.selectMsgList(signInIdx, hostList.get(i).getIdx(), listUnread.get(0).getAll_hidden_unread());
         
         fourthlists.put("id", hostList.get(i).getIdx());
         fourthlists.put("reservationId", userResInfoVO.getIdx());
         fourthlists.put("state", listUnread.get(0).getAll_hidden_unread());
         
         String realName = hostList.get(i).getHost_name().replace("님", "");
         fourthlists.put("hostname", realName);
         
         //contents
         contents.put("hostProfileImg", hostList.get(i).getHost_profileImg());
         contents.put("lastMsg", latestMsgVO.getContent());
         contents.put("lastMsgDate", latestMsgVO.getSend_date_time());
         contents.put("isCanceled", userResInfoVO.getIs_canceled());
         contents.put("checkin", userResInfoVO.getCheckin());
         contents.put("checkout", userResInfoVO.getCheckout());
         
         //info.add(n, contents);
         fourthlists.put("contents", contents);
         
         //chatHistory
         for(int j = 0; j < chattingListU.size(); j++) {
            chatHistory = new JSONObject();
                           
            chatHistory.put("id", chattingListU.get(j).getIdx());
            chatHistory.put("userId", chattingListU.get(j).getFrom_idx());
            
            if(hostList.get(i).getIdx() == chattingListU.get(j).getFrom_idx()) {
               String name = hostList.get(i).getHost_name().replace("님", "");
               chatHistory.put("name", name);
            } else if(signInIdx == chattingListU.get(j).getFrom_idx()) {
               chatHistory.put("name", signInName);
            }
            
            chatHistory.put("timeStamp", chattingListU.get(j).getSend_date_time());
            chatHistory.put("text", chattingListU.get(j).getContent());
            
            info.add(n, chatHistory);
            fourthlists.put("chatHistory", info);
         }

         secondArr3.add(i, fourthlists);
         
         res.put("unread", secondArr3);
      }
      
      //res.put("message", resFilter);
      
      //model.addAttribute("res", res.toString());
      
      return res.toString();
      //return Common.VIEW_PATH + "chat.jsp";
   }
   
   @RequestMapping(value = "/message_insert", method=RequestMethod.POST,
         produces = "application/json;charset=utf8", consumes = MediaType.ALL_VALUE)
   @ResponseBody
   public String insert_chat(Model model, @RequestBody String payload) {
      HttpHeaders resHeaders = new HttpHeaders();
      resHeaders.add("Content-Type", "application/json;charset=UTF-8");
      
      //initialization
            JSONObject result = new JSONObject();
            Map<String, Object> javaObject = null;
            int result_idx = 0;
            int res = 0;
      
      //from Payload
            try {
               javaObject = mapper.readValue(payload, Map.class);
            } catch (Exception e) {
               System.out.println("payload 오류");
            }
            System.out.println("javaObject: " + javaObject);
      
      //Login cookie
      HttpSession session = request.getSession();
      Cookie[] cookies = request.getCookies();
      String sessionKey = "";
      int signInIdx = 1; //temp
      String signInProfileImg = "";
      String signInName = "";
      
      if(cookies == null) {
         System.out.println("not cookies");
      }else {
         for(Cookie cookie : cookies) {
            if("AirdndSES".equals(cookie.getName())) {
               sessionKey = cookie.getValue();
               AirdndUserVO signInVO = (AirdndUserVO)session.getAttribute(sessionKey);
               signInIdx = signInVO.getUser_idx();
               signInProfileImg = signInVO.getProfileImg();
               signInName = signInVO.getLast_name() + signInVO.getFirst_name();
            } else {
               System.out.println("not login");
            }
         }
      }//if
      
      
      
      AirdndChatVO chatVO = new AirdndChatVO();
      AirdndChatMsgsVO chatMsgVO = new AirdndChatMsgsVO();
      
      int host_idx = 0;
      String host_profile_imgurl = null;
      String all_hidden_unread = null;
      
      //from Login cookie
      chatVO.setUser_idx(signInIdx);
      chatMsgVO.setFrom_idx(signInIdx);
      
      
      
      host_idx = Integer.parseInt(javaObject.get("hostId").toString());
      host_profile_imgurl = javaObject.get("hostProfileImg").toString();
      all_hidden_unread = javaObject.get("filter").toString();
      
      AirdndChatVO chatVO2 = airdndChatService.selectHostChat(signInIdx, host_idx);
      
      if(chatVO2 == null) {
         chatVO.setUser_idx(signInIdx);
         chatVO.setHost_idx(host_idx);
         chatVO.setHost_profile_imgurl(host_profile_imgurl);
         chatVO.setAll_hidden_unread(all_hidden_unread);
         
         AirdndUserResInfoVO userResInfoVO = airdndChatService.selectUserResInfo(chatVO.getUser_idx(), chatVO.getHost_idx());
         
         chatVO.setCheckin(userResInfoVO.getCheckin());
         chatVO.setCheckout(userResInfoVO.getCheckout());
         
         chatVO2 = airdndChatService.insertChat(chatVO);
         chatMsgVO.setMessage_idx(chatVO2.getIdx());
      } else {
         chatMsgVO.setMessage_idx(chatVO.getIdx());
      }
      
      chatMsgVO.setFrom_idx(signInIdx);
      chatMsgVO.setTo_idx(host_idx);
      chatMsgVO.setFrom_profile_imgurl(signInProfileImg);
      
      res = airdndChatService.insertMsg(chatMsgVO);
       
      //Real
      //vo.setContent(request.getParameter("content"));
      
      //airdndChatService.insertChat(vo);
      if(res == 1) {
         
      }
      
      model.addAttribute("vo", chatVO);
      
      return "redirect:chat";
   }
}