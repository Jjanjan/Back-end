package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import vo.AirdndChatMsgsVO;
import vo.AirdndChatVO;
import vo.AirdndHostVO;
import vo.AirdndUserResInfoVO;

@Repository("airdndChatDAO")
public class AirdndChatDAO implements AirdndChatDAOI {
	@Autowired
	DataSource dataSource;
	
	//Select User's chat list - all
	@Override
	public List<AirdndChatVO> selectChatListAll(int user_idx){
		String sql = "select * from airdnd_chatting where user_idx=" + user_idx + " and all_hidden_unread='all' order by idx";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		List<AirdndChatVO> list = jdbcTemplate.query(sql, new RowMapper<AirdndChatVO>() {
			@Override
			public AirdndChatVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				AirdndChatVO list = new AirdndChatVO(
					rs.getInt("idx"),
					rs.getInt("host_idx"),
					rs.getString("host_profile_imgurl"),
					rs.getInt("user_idx"),
					rs.getString("all_hidden_unread"),
					rs.getString("checkin"),
					rs.getString("checkout"));

				return list;
			}
		});
		
		return list;
	}
	
	//Select User's chat list - hidden
	@Override
	public List<AirdndChatVO> selectChatListHidden(int user_idx){
		String sql = "select * from airdnd_chatting where user_idx=" + user_idx + " and all_hidden_unread='hidden' order by idx";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		List<AirdndChatVO> list = jdbcTemplate.query(sql, new RowMapper<AirdndChatVO>() {
			@Override
			public AirdndChatVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				AirdndChatVO list = new AirdndChatVO(
					rs.getInt("idx"),
					rs.getInt("host_idx"),
					rs.getString("host_profile_imgurl"),
					rs.getInt("user_idx"),
					rs.getString("all_hidden_unread"),
					rs.getString("checkin"),
					rs.getString("checkout"));

				return list;
			}
		});
		
		return list;
	}
	
	//Select User's chat list - unread
	@Override
	public List<AirdndChatVO> selectChatListUnread(int user_idx){
		String sql = "select * from airdnd_chatting where user_idx=" + user_idx + " and all_hidden_unread='unread' order by idx";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		List<AirdndChatVO> list = jdbcTemplate.query(sql, new RowMapper<AirdndChatVO>() {
			@Override
			public AirdndChatVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				AirdndChatVO list = new AirdndChatVO(
					rs.getInt("idx"),
					rs.getInt("host_idx"),
					rs.getString("host_profile_imgurl"),
					rs.getInt("user_idx"),
					rs.getString("all_hidden_unread"),
					rs.getString("checkin"),
					rs.getString("checkout"));

				return list;
			}
		});
		
		return list;
	}
	
	//Select User's messages list with filter
	@Override
	public List<AirdndChatMsgsVO> selectMsgList(int user_idx, int host_idx, String all_hidden_unread){
		String sql = "SELECT * FROM airdnd_chatting_msgs where message_idx in (SELECT idx from airdnd_chatting where user_idx=" + user_idx + " AND host_idx=" + host_idx + " AND all_hidden_unread='" + all_hidden_unread + "') order by send_date_time asc";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		List<AirdndChatMsgsVO> list = jdbcTemplate.query(sql, new RowMapper<AirdndChatMsgsVO>() {
			@Override
			public AirdndChatMsgsVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				AirdndChatMsgsVO list = new AirdndChatMsgsVO(
					rs.getInt("idx"),
					rs.getInt("message_idx"),
					rs.getInt("from_idx"),
					rs.getInt("to_idx"),
					rs.getString("content"),
					rs.getString("send_date_time"),
					rs.getString("from_profile_imgurl"));

				return list;
			}
		});
		
		return list;
	}
	
	//Select latest msg in User's chat list
	@Override
	public AirdndChatMsgsVO selectLatestMsg(int user_idx, int host_idx, String all_hidden_unread){
		String sql = "select * from airdnd_chatting_msgs where message_idx in (SELECT idx FROM airdnd_chatting where all_hidden_unread='" + all_hidden_unread + "' AND user_idx=" + user_idx + " AND host_idx=" + host_idx + ") order by send_date_time desc limit 1";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		List<AirdndChatMsgsVO> list = jdbcTemplate.query(sql, new RowMapper<AirdndChatMsgsVO>() {
			@Override
			public AirdndChatMsgsVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				AirdndChatMsgsVO list = new AirdndChatMsgsVO(
					rs.getInt("idx"),
					rs.getInt("message_idx"),
					rs.getInt("from_idx"),
					rs.getInt("to_idx"),
					rs.getString("content"),
					rs.getString("send_date_time"),
					rs.getString("from_profile_imgurl"));

				return list;
			}
		});
		
		return list.get(0);
	}
	
	/*
	//Select the user info
	@Override
	public AirdndUserVO selectUser(int user_idx) {
		String sql = "select * from airdnd_user where user_idx=" + user_idx;
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		List<AirdndUserVO> list = jdbcTemplate.query(sql, new RowMapper<AirdndUserVO>() {
			@Override
			public AirdndUserVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				AirdndUserVO vo = new AirdndUserVO(
					rs.getInt("user_idx"),
					rs.getString("email"),
					rs.getString("pwd"),
					rs.getString("last_name"),
					rs.getString("first_name"),
					rs.getString("birthday"),
					rs.getString("profileImg"),
					rs.getString("phone"),
					rs.getString("signupDate"),
					rs.getString("description"));

				return vo;
			}
		});
		
		return list.get(0);
	}
	*/
	//Select the host info list
	@Override
	public List<AirdndHostVO> selectHostList(int user_idx, String all_hidden_unread) {
		String sql = "SELECT * FROM airdnd_host where idx in (SELECT host_idx FROM airdnd_chatting where user_idx=" + user_idx + " AND all_hidden_unread='" + all_hidden_unread + "')";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		List<AirdndHostVO> list = jdbcTemplate.query(sql, new RowMapper<AirdndHostVO>() {
			@Override
			public AirdndHostVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				AirdndHostVO vo = new AirdndHostVO(
					rs.getInt("idx"),
					rs.getInt("home_idx"),
					rs.getBoolean("check_superhost"),
					rs.getBoolean("check_certification"),
					rs.getInt("host_review_num"),
					rs.getString("host_name"),
					rs.getString("host_sign_in_date"),
					rs.getString("host_status_message"),
					rs.getString("interaction_with_guests"),
					rs.getString("host_language"),
					rs.getString("response_rate"),
					rs.getString("response_time"),
					rs.getString("host_profileImg"));

				return vo;
			}
		});
		
		return list;
	}
	
	//Select the userResInfo
	@Override
	public AirdndUserResInfoVO selectUserResInfo(int user_idx, int host_idx) {
		String sql = "select * from airdnd_user_res_info where user_idx=" + user_idx + " AND home_idx=(select home_idx from airdnd_host where idx=" + host_idx + ")";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		List<AirdndUserResInfoVO> list = jdbcTemplate.query(sql, new RowMapper<AirdndUserResInfoVO>() {
			@Override
			public AirdndUserResInfoVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				AirdndUserResInfoVO vo = new AirdndUserResInfoVO(
						rs.getInt("idx"),
						rs.getInt("user_idx"),
						rs.getInt("home_idx"),
						rs.getString("checkin"),
						rs.getString("checkout"),
						rs.getInt("guest_idx"),
						rs.getInt("is_canceled"),
						rs.getInt("adult"),
						rs.getInt("child"),
						rs.getInt("infant"));
				
				return vo;
			}
		});
		
		return list.get(0);
	}
	
	//Select host_idx chatting
	@Override
	public AirdndChatVO selectHostChat(int user_idx, int host_idx) {
		String sql = "select * from airdnd_chatting where user_idx=" + user_idx + " AND host_idx=" + host_idx;
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		List<AirdndChatVO> list = jdbcTemplate.query(sql, new RowMapper<AirdndChatVO>() {
			@Override
			public AirdndChatVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				AirdndChatVO list = new AirdndChatVO(
					rs.getInt("idx"),
					rs.getInt("host_idx"),
					rs.getString("host_profile_imgurl"),
					rs.getInt("user_idx"),
					rs.getString("all_hidden_unread"),
					rs.getString("checkin"),
					rs.getString("checkout"));

				return list;
			}
		});
		
		return list.get(0);
	}
	
	//Insert chatting
	@Override
	public AirdndChatVO insertChat(AirdndChatVO vo) {
		String sql = "insert into airdnd_chatting(host_idx, host_profile_imgurl, user_idx, all_hidden_unread, checkin, checkout) values(?, ?, ?, ?, ?, ?)";
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql, vo.getHost_idx(), vo.getHost_profile_imgurl(), vo.getUser_idx(), vo.getAll_hidden_unread(), vo.getCheckin(), vo.getCheckout());
		
		return vo;
	}
	
	//Insert message
	@Override
	public int insertMsg(AirdndChatMsgsVO vo) {
		String sql = "insert into airdnd_chatting_msgs(message_idx, from_idx, to_idx, content, send_date_time) values(?, ?, ?, ?, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 9 HOUR))";
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		int res = jdbcTemplate.update(sql, vo.getMessage_idx(), vo.getFrom_idx(), vo.getTo_idx(), vo.getContent());
		
		return res;
	}
}
