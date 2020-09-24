package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import vo.AirdndBookmarkVO;
import vo.AirdndBookmarkedHomesVO;
import vo.AirdndHomePictureVO;

@Repository("airdndBookmarkDAO")
public class AirdndBookmarkDAO implements AirdndBookmarkDAOI {
	@Autowired
	DataSource dataSource;
	
	//Select bookmark list
	@Override
	public List<AirdndBookmarkVO> selectBookmark(int user_idx) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		//서버 연결할 때 쿼리문 뒤에 "WHERE user_idx= + user_idx <- 추가
		List<AirdndBookmarkVO> list = jdbcTemplate.query("select * from airdnd_bookmark where user_idx=" + user_idx + " order by update_date_time desc", new RowMapper<AirdndBookmarkVO>() {
			@Override
			public AirdndBookmarkVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				AirdndBookmarkVO list = new AirdndBookmarkVO(
					rs.getInt("idx"),
					rs.getInt("user_idx"),
					rs.getString("bookmark_list_title"),
					rs.getString("checkin"),
					rs.getString("checkout"),
					rs.getString("update_date_time"));

				return list;
			}
		});
		
		return list;
	}
	
	//Select bookmark homes
	@Override
	public List<AirdndBookmarkedHomesVO> selectBookmarkHomes(int user_idx) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		List<AirdndBookmarkedHomesVO> list = jdbcTemplate.query("select * from airdnd_bookmarked_homes where user_idx=" + user_idx + " order by bookmark_idx", new RowMapper<AirdndBookmarkedHomesVO>() {
			@Override
			public AirdndBookmarkedHomesVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				AirdndBookmarkedHomesVO list = new AirdndBookmarkedHomesVO(
					rs.getInt("idx"),
					rs.getInt("bookmark_idx"),
					rs.getInt("user_idx"),
					rs.getInt("home_idx"));

				return list;
			}
		});
		
		return list;
	}
	
	public List<AirdndBookmarkedHomesVO> selectBookmarkHomesforUser(int bookmark_idx) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		List<AirdndBookmarkedHomesVO> list = jdbcTemplate.query("select * from airdnd_bookmarked_homes where bookmark_idx=" + bookmark_idx + " order by bookmark_idx", new RowMapper<AirdndBookmarkedHomesVO>() {
			@Override
			public AirdndBookmarkedHomesVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				AirdndBookmarkedHomesVO list = new AirdndBookmarkedHomesVO(
					rs.getInt("idx"),
					rs.getInt("bookmark_idx"),
					rs.getInt("user_idx"),
					rs.getInt("home_idx"));

				return list;
			}
		});
		
		return list;
	}
	
	//Select bookmark homes where bookidx
	@Override
	public List<AirdndBookmarkedHomesVO> selectBookmarkHomesIdx(int bookmark_idx) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		List<AirdndBookmarkedHomesVO> list = jdbcTemplate.query("select * from airdnd_bookmarked_homes where bookmark_idx=" + bookmark_idx, new RowMapper<AirdndBookmarkedHomesVO>() {
			@Override
			public AirdndBookmarkedHomesVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				AirdndBookmarkedHomesVO list = new AirdndBookmarkedHomesVO(
					rs.getInt("idx"),
					rs.getInt("bookmark_idx"),
					rs.getInt("user_idx"),
					rs.getInt("home_idx"));

				return list;
			}
		});
		
		return list;
	}
	
	//Search bookmark homes' count
	@Override
	public int selectBookmarkHomesCount(int bookmark_idx) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		List<AirdndBookmarkedHomesVO> list = jdbcTemplate.query("select * from airdnd_bookmarked_homes where bookmark_idx=" + bookmark_idx, new RowMapper<AirdndBookmarkedHomesVO>() {
			@Override
			public AirdndBookmarkedHomesVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				AirdndBookmarkedHomesVO list = new AirdndBookmarkedHomesVO(
					rs.getInt("idx"),
					rs.getInt("bookmark_idx"),
					rs.getInt("user_idx"),
					rs.getInt("home_idx"));

				return list;
			}
		});
		
		int size = list.size();
		
		return size;
	}
	
	//Select user's reservation home's main picture
	@Override
	public List<AirdndHomePictureVO> selectHomeMainPicture(int home_idx){
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		List<AirdndHomePictureVO> list = jdbcTemplate.query("SELECT * FROM (SELECT * FROM airdnd_home_picture as P WHERE home_idx=" + home_idx + ") P", new RowMapper<AirdndHomePictureVO>() {
			@Override
			public AirdndHomePictureVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				AirdndHomePictureVO list = new AirdndHomePictureVO(
					rs.getInt("idx"),
					rs.getInt("home_idx"),
					rs.getString("url"));
				return list;
			}
		});

		return list;
	}

	//Create a new bookmark
	@Override
	public int insert_bookmark(AirdndBookmarkVO vo) {
		String sql = "insert into airdnd_bookmark(user_idx, bookmark_list_title, checkin, checkout) values(?, ?, ?, ?)";
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		int res = jdbcTemplate.update(sql, vo.getUser_idx(), vo.getBookmark_list_title(), vo.getCheckin(), vo.getCheckout());
		
		return res;
	}

	//Select a new bookmark info
	@Override
	public int selectNewBookmarkInfo() {
		String sql = "SELECT idx FROM airdnd_bookmark order by update_date_time desc limit 1";
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		int idx = jdbcTemplate.queryForInt(sql);

		return idx;
	}
	
	//Search an idx
	@Override
	public int selectIdx(String bookmark_list_title) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		int idx = jdbcTemplate.queryForInt("select idx from airdnd_bookmark where bookmark_list_title='" + bookmark_list_title + "'");
		
		return idx;
	}
	
	//Add the home in the bookmark
	@Override
	public int insert_bookmarkHome(AirdndBookmarkedHomesVO vo) {
		String sql = "insert into airdnd_bookmarked_homes(bookmark_idx, user_idx, home_idx) values(?, ?, ?)";
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		int res = jdbcTemplate.update(sql, vo.getBookmark_idx(), vo.getUser_idx(), vo.getHome_idx());
		
		return res;
	}
	
	//Select a bookmark where idx=
	@Override
	public int selectPreviousBookmarkInfo(int idx) {
		String sql = "SELECT idx FROM airdnd_bookmark where idx=" + idx;
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		int bookmark_idx = jdbcTemplate.queryForInt(sql);

		return bookmark_idx;
	}
	
	//Delete the home in the bookmark
	@Override
	public int delete_bookmarkHome(int home_idx) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		String sql = "select bookmark_idx from airdnd_bookmarked_homes where home_idx=" + home_idx;
		int bookmark_idx = jdbcTemplate.queryForInt(sql);
		
		sql = "delete from airdnd_bookmarked_homes where home_idx=" + home_idx;
		jdbcTemplate.update(sql);
		
		return bookmark_idx;
	}
	
	//Delete the bookmark
	@Override
	public int delete_bookmark(int idx) {
		String sql = "delete from airdnd_bookmark where idx=" + idx;
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sql);
		
		sql = "delete from airdnd_bookmarked_homes where bookmark_idx=" + idx;
		int res = jdbcTemplate.update(sql);
		
		return res;
	}
	
	//Update update_date_time
	@Override
	public int update_updateTime(int idx) {
		String sql = "update airdnd_bookmark set update_date_time=CURRENT_TIMESTAMP where idx=" + idx;
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		int res = jdbcTemplate.update(sql);
		
		return res;
	}

}
