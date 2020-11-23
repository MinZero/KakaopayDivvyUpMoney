package kr.co.minzero.divvyup.mapper;

import kr.co.minzero.divvyup.model.DivvyupMaster;
import kr.co.minzero.divvyup.model.DivvyupTake;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DivvyupTakeMapper {
    int insertDivvyupTake(DivvyupTake divvyupTake);

    DivvyupTake selectDivvyupTake(DivvyupTake divvyupTake);

    List<DivvyupTake> selectDivvyupTakeList(DivvyupTake divvyupTake);

    int updateDivvyupTake(DivvyupTake divvyupTake);
}
