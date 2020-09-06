package service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dao.AirdndSearchDAO;
import vo.AirdndHomePictureVO;
import vo.AirdndSearchVO;
import vo.AirdndUserVO;

@Service("airdndsearchService")
public class AirdndSearchService implements AirdndSearchServiceI{

	@Autowired
	AirdndSearchDAO airdnd_search_dao;

	@Override
	public List<AirdndSearchVO> searchselect(Map<Object, Object> param){

		List<AirdndSearchVO> list = airdnd_search_dao.select(param);
		return list;
	}

	@Override
	public List<AirdndHomePictureVO> pictureselect(int home_idx){

		List<AirdndHomePictureVO> list = airdnd_search_dao.pictureselect(home_idx);
		return list;
	}

	@Override
	public List<AirdndSearchVO> searchtotalselect(Map<Object, Object> param){

		List<AirdndSearchVO> list = airdnd_search_dao.totalselect(param);
		return list;
	}

	public List<AirdndSearchVO> unitpriceselect(String place) {

		List<AirdndSearchVO> list = airdnd_search_dao.unitpriceselect(place);
		return list;
	}
	
	public List<AirdndSearchVO> facilityList(String place) {
		
		List<AirdndSearchVO> list = airdnd_search_dao.facilityList(place);
		return list;
	}
	
	@Override
	public List<AirdndUserVO> hostLanlist(String place) {
		
		List<AirdndUserVO> list = airdnd_search_dao.hostLanlist(place);
		return list;
	}
	
	public List<AirdndSearchVO> select_one(int homeIdx) {
	      
	      List<AirdndSearchVO> list = airdnd_search_dao.select_one(homeIdx);
	      return list;
	   }

}
