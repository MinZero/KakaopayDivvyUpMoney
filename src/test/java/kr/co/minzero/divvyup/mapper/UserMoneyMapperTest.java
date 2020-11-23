package kr.co.minzero.divvyup.mapper;

import kr.co.minzero.divvyup.model.UserMoney;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMoneyMapperTest {
    @Autowired
    private UserMoneyMapper userMoneyMapper;

    /**
     * 사용자 금액이 정상적으로 변경되는지 확인한다.
     */
    @Test
    public void testUpdateUserMoney() {
        UserMoney userMoney = new UserMoney();

        userMoney.setUserId(123456);
        userMoney.setMoney(1000000000);

        int rows = userMoneyMapper.updateUserMoney(userMoney);
        assertThat(rows).isGreaterThan(0);
    }

    /**
     * 사용자 금액이 정상적으로 조회되는지 확인한다.
     */
    @Test
    public void testSelectUserMoney() {
        UserMoney userMoney = userMoneyMapper.selectUserMoney(new UserMoney(123456));

        assertThat(userMoney.getUserId()).isEqualTo(123456);
        assertThat(userMoney.getMoney()).isGreaterThan(0);
    }
}
