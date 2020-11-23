package kr.co.minzero.divvyup.service;

import kr.co.minzero.divvyup.constants.Config;
import kr.co.minzero.divvyup.constants.ResponseCode;
import kr.co.minzero.divvyup.mapper.DivvyupMasterMapper;
import kr.co.minzero.divvyup.mapper.DivvyupTakeMapper;
import kr.co.minzero.divvyup.mapper.UserMoneyMapper;
import kr.co.minzero.divvyup.model.DivvyupMaster;
import kr.co.minzero.divvyup.model.DivvyupTake;
import kr.co.minzero.divvyup.model.UserMoney;
import kr.co.minzero.divvyup.util.DivvyupUtility;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 서비스 클래스
 */
@Service
public class DivvyupService {
    @Autowired
    private DivvyupMasterMapper divvyupMasterMapper;

    @Autowired
    private DivvyupTakeMapper divvyupTakeMapper;

    @Autowired
    private UserMoneyMapper userMoneyMapper;

    @Autowired
    private RedissonClient redissonClient;

    /**
     *
     * @param roomId 대화방 식별값
     * @param userId 사용자 식별값
     * @param money 뿌리는 금액
     * @param count 분배 인원수
     * @return 뿌려진
     */
    @Transactional
    public DivvyupMaster divvyup(String roomId, long userId, long money, int count) {
        DivvyupMaster divvyupMaster = new DivvyupMaster();

        if(userId == 0) {
            divvyupMaster.setResultCode(ResponseCode.FAIL);
            divvyupMaster.setResultMessage("사용자가 존재하지 않습니다.");
        } else if(roomId == null) {
            divvyupMaster.setResultCode(ResponseCode.FAIL);
            divvyupMaster.setResultMessage("대화방 식별값이 존재하지 않습니다.");
        } else if (count == 0) {
            divvyupMaster.setResultCode(ResponseCode.FAIL);
            divvyupMaster.setResultMessage("분배인원수는 0보다 커야 합니다.");
        } else if (money == 0) {
            divvyupMaster.setResultCode(ResponseCode.FAIL);
            divvyupMaster.setResultMessage("뿌리기 금액은 0원보다 커야 합니다.");
        } else {
            RLock lock = null;
            try {
                lock = redissonClient.getLock("divvyup:" + roomId + ":" + userId);
                if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                    UserMoney userMoney = userMoneyMapper.selectUserMoney(new UserMoney(userId));

                    String token = DivvyupUtility.createToken();
                    divvyupMaster = new DivvyupMaster(token, roomId, userId, money, count);

                    if (userMoney == null) { // 요청자가 존재하는 경우
                        divvyupMaster.setResultCode(ResponseCode.FAIL);
                        divvyupMaster.setResultMessage("사용자가 존재하지 않습니다.");
                    } else if (userMoney.getMoney() < money) { // 요청자의 잔고가 요청 금액보다 크거나 같 경우
                        divvyupMaster.setResultCode(ResponseCode.FAIL);
                        divvyupMaster.setResultMessage("잔고가 부족합니다.");
                    } else {
                        try {
                            divvyupMasterMapper.insertDivvyupMaster(divvyupMaster);

                            userMoney.setMoney(userMoney.getMoney() - money);
                            userMoneyMapper.updateUserMoney(userMoney);

                            long[] splitMoneys = DivvyupUtility.splitMoney(money, count);
                            for (long splitMoney : splitMoneys) {
                                divvyupTakeMapper.insertDivvyupTake(new DivvyupTake(token, roomId, splitMoney));
                            }
                            divvyupMaster.setResultCode(ResponseCode.SUCCESS);
                            divvyupMaster.setResultMessage("");
                        } catch (Exception e) {
                            e.printStackTrace();
                            divvyupMaster.setResultCode(ResponseCode.FAIL);
                            divvyupMaster.setResultMessage(e.toString());
                        }
                    }
                } else {
                    divvyupMaster.setResultCode(ResponseCode.FAIL);
                    divvyupMaster.setResultMessage("잠시 후 다시 시도하십시오.");
                }
            } catch(Exception e) {
                divvyupMaster.setResultCode(ResponseCode.FAIL);
                divvyupMaster.setResultMessage(e.getMessage());
                e.printStackTrace();
            } finally {
                if(lock != null)
                    lock.unlock();
            }
        }
        return divvyupMaster;
    }

    /**
     * 뿌려진 금액을 받는다
     *
     * @param roomId 대화방 식별값
     * @param userId 사용자 식별값
     * @param token 토큰 식별값
     *
     * @return 받는 금액 정보
     */
    @Transactional
    public DivvyupTake take(String roomId, long userId, String token) {
        DivvyupTake takeList = new DivvyupTake();

        if(roomId == null) {
            takeList.setResultCode(ResponseCode.FAIL);
            takeList.setResultMessage("대화방 식별값이 존재하지 않습니다.");
        } else if(userId == 0) {
            takeList.setResultCode(ResponseCode.FAIL);
            takeList.setResultMessage("사용자가 존재하지 않습니다.");
        } else if(token == null) {
            takeList.setResultCode(ResponseCode.FAIL);
            takeList.setResultMessage("뿌리기 식별값이 존재하지 않습니다.");
        } else {
            RLock lock = null;
            try {
                lock = redissonClient.getLock("divvyup|take|" + roomId + "|" + token);
                if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                    UserMoney userMoney = userMoneyMapper.selectUserMoney(new UserMoney(userId));

                    if (userMoney == null) {
                        takeList.setResultCode(ResponseCode.FAIL);
                        takeList.setResultMessage("사용자가 존재하지 않습니다.");
                    } else {
                        DivvyupMaster master = divvyupMasterMapper.selectDivvyupMaster(new DivvyupMaster(token, roomId));
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.MINUTE, Config.untilMinutes * -1);
                        if (master == null) {
                            takeList.setResultCode(ResponseCode.FAIL);
                            takeList.setResultMessage("뿌리기 존재하지 않습니다.");
                        } else if (master.getUserId() == userId) { // 받으려는 사용자가 생성한 경우
                            takeList.setResultCode(ResponseCode.FAIL);
                            takeList.setResultMessage("뿌린 사용자는 받을 수 없습니다.");
                        } else if (master.getCreateDate().before(cal.getTime())) {
                            takeList.setResultCode(ResponseCode.FAIL);
                            takeList.setResultMessage("받을 수 있는 시간이 지났습니다.");
                        } else {
                            DivvyupTake take = new DivvyupTake();
                            take.setToken(token);
                            take.setRoomId(roomId);
                            take.setTakeUserId(userId);
                            take.setType("userId");
                            DivvyupTake divvyupTake = divvyupTakeMapper.selectDivvyupTake(take);
                            if (divvyupTake != null) {
                                takeList.setResultCode(ResponseCode.FAIL);
                                takeList.setResultMessage("한번만 받을 수 있습니다.");
                            } else {
                                take.setType("unTake");
                                List<DivvyupTake> untakeList = divvyupTakeMapper.selectDivvyupTakeList(take);
                                if (untakeList != null && untakeList.size() > 0) {
                                    takeList = untakeList.get(0);
                                    takeList.setTakeUserId(userId);
                                    takeList.setTakeDate(new Date());
                                    divvyupTakeMapper.updateDivvyupTake(takeList);
                                    userMoney.setMoney(userMoney.getMoney() + takeList.getTakeMoney());
                                    userMoneyMapper.updateUserMoney(userMoney);
                                    takeList.setResultCode(ResponseCode.SUCCESS);
                                    takeList.setResultMessage("");
                                } else {
                                    takeList.setResultCode(ResponseCode.FAIL);
                                    takeList.setResultMessage("뿌린 금액을 모두 받았습니다.");
                                }
                            }
                        }
                    }
                } else {
                    takeList.setResultCode(ResponseCode.FAIL);
                    takeList.setResultMessage("잠시 후 다시 시도하십시오.");
                }
            } catch (Exception e) {
                takeList.setResultCode(ResponseCode.FAIL);
                takeList.setResultMessage(e.getMessage());
                e.printStackTrace();
            } finally {
                if(lock != null)
                    lock.unlock();
            }
        }
        return takeList;
    }

    /**
     * 뿌리기 현황 조회
     *
     * @param roomId 대화방 식별값
     * @param userId 사용자 식별값
     * @param token 토큰 식별값
     * @return 뿌리기 현황이 조회된다
     */
    public DivvyupMaster viewResult(String roomId, long userId, String token) {
        DivvyupMaster divvyupMaster = new DivvyupMaster();
        if(roomId == null) {
            divvyupMaster.setResultCode(ResponseCode.FAIL);
            divvyupMaster.setResultMessage("대화방 식별값이 존재하지 않습니다.");
        } else if(userId == 0) {
            divvyupMaster.setResultCode(ResponseCode.FAIL);
            divvyupMaster.setResultMessage("사용자가 존재하지 않습니다.");
        } else if(token == null) {
            divvyupMaster.setResultCode(ResponseCode.FAIL);
            divvyupMaster.setResultMessage("뿌리기 식별값이 존재하지 않습니다.");
        } else {
            divvyupMaster = divvyupMasterMapper.selectDivvyupMaster(new DivvyupMaster(token, roomId));

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, Config.untilViewDate * -1);

            if (divvyupMaster == null) {
                divvyupMaster = new DivvyupMaster();
                divvyupMaster.setResultCode(ResponseCode.FAIL);
                divvyupMaster.setResultMessage("조회 건이 없습니다.");
            } else if (userId != divvyupMaster.getUserId()) {
                divvyupMaster.setResultCode(ResponseCode.FAIL);
                divvyupMaster.setResultMessage("조회 권한이 없습니다.");
            } else if (divvyupMaster.getCreateDate().before(cal.getTime())) {
                divvyupMaster.setResultCode(ResponseCode.FAIL);
                divvyupMaster.setResultMessage("조회 기간이 지났습니다.");
            } else {
                divvyupMaster.setTakeList(divvyupTakeMapper.selectDivvyupTakeList(new DivvyupTake(token, roomId)));
                divvyupMaster.setResultCode(ResponseCode.SUCCESS);
                divvyupMaster.setResultMessage("");
            }
        }
        return divvyupMaster;
    }
}
