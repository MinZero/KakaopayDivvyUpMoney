package kr.co.minzero.divvyup.mapper;

import kr.co.minzero.divvyup.model.DivvyupTake;
import kr.co.minzero.divvyup.util.DivvyupUtility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DivvyupTakeMapperTest {
    @Autowired
    private DivvyupTakeMapper divvyupTakeMapper;

    @Test
    public void testInsertDivvyupTake(){
        int rows = divvyupTakeMapper.insertDivvyupTake(new DivvyupTake(DivvyupUtility.createToken(), "ROOM032", 2331231));

        assertThat(rows).isGreaterThan(0);
    }

    @Test
    public void testSelectDivvyupTake(){
        String token = DivvyupUtility.createToken();
        int rows = divvyupTakeMapper.insertDivvyupTake(new DivvyupTake(token, "ROOM032", 2331231));

        assertThat(rows).isGreaterThan(0);

        DivvyupTake take = divvyupTakeMapper.selectDivvyupTakeList(new DivvyupTake(token, "ROOM032")).get(0);
        take.setTakeUserId(222222);
        take.setTakeDate(new Date());
        int result = divvyupTakeMapper.updateDivvyupTake(take);

        assertThat(result).isGreaterThan(0);

        take.setType("userId");
        DivvyupTake divvyupTake = divvyupTakeMapper.selectDivvyupTake(take);

        assertThat(divvyupTake.getTakeMoney()).isGreaterThan(0);
    }

    @Test
    public void testSelectDivvyupTakeList(){
        String token = DivvyupUtility.createToken();
        divvyupTakeMapper.insertDivvyupTake(new DivvyupTake(token, "ROOM032", 2331231));

        List<DivvyupTake> divvyupTakeList = divvyupTakeMapper.selectDivvyupTakeList(new DivvyupTake(token, "ROOM032"));

        assertThat(divvyupTakeList.size()).isGreaterThan(0);
    }

    @Test
    public void testUpdateDivvyupTake(){
        String token = DivvyupUtility.createToken();
        int rows = divvyupTakeMapper.insertDivvyupTake(new DivvyupTake(token, "ROOM032", 2331231));

        assertThat(rows).isGreaterThan(0);

        DivvyupTake take = divvyupTakeMapper.selectDivvyupTakeList(new DivvyupTake(token, "ROOM032")).get(0);
        take.setTakeUserId(222222);
        take.setTakeDate(new Date());
        int result = divvyupTakeMapper.updateDivvyupTake(take);

        assertThat(result).isGreaterThan(0);
    }
}
