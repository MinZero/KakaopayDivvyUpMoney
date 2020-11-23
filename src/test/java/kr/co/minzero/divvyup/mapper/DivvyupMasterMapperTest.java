package kr.co.minzero.divvyup.mapper;

import kr.co.minzero.divvyup.model.DivvyupMaster;
import kr.co.minzero.divvyup.util.DivvyupUtility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DivvyupMasterMapperTest {
    @Autowired
    private DivvyupMasterMapper divvyupMasterMapper;

    @Test
    public void testInsertDivvyupMaster() {
        DivvyupMaster master = new DivvyupMaster(DivvyupUtility.createToken(), "ROOM009", 123456, 30000, 5);

        int rows = divvyupMasterMapper.insertDivvyupMaster(master);

        assertThat(rows).isGreaterThan(0);
    }

    @Test
    public void testSelectDivvyupMaster() {
        String token = DivvyupUtility.createToken();

        int rows = divvyupMasterMapper.insertDivvyupMaster(new DivvyupMaster(token, "ROOM009", 123456, 30000, 5));

        assertThat(rows).isGreaterThan(0);

        DivvyupMaster master = divvyupMasterMapper.selectDivvyupMaster(new DivvyupMaster(token, "ROOM009"));

        assertThat(master.getTotalMoney()).isEqualTo(30000);
        assertThat(master.getUserId()).isEqualTo(123456);
    }
}
