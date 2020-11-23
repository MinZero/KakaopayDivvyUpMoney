package kr.co.minzero.divvyup.mapper;

import kr.co.minzero.divvyup.model.UserMoney;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMoneyMapper {
    UserMoney selectUserMoney(UserMoney userMoney);

    int updateUserMoney(UserMoney userMoney);
}
