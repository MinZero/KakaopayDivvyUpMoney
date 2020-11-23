package kr.co.minzero.divvyup.controller;

import kr.co.minzero.divvyup.common.DivvyupRequest;
import kr.co.minzero.divvyup.common.DivvyupResponse;
import kr.co.minzero.divvyup.constants.ResponseCode;
import kr.co.minzero.divvyup.model.DivvyupMaster;
import kr.co.minzero.divvyup.model.DivvyupTake;
import kr.co.minzero.divvyup.service.DivvyupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value="/api/divvyup", produces="text/plain;charset=UTF-8")
public class DivvyupController {

    @Autowired
    private DivvyupService service;

    /**
     * 뿌리기 기능 API
     * @param userId 사용자 식별값
     * @param roomId 대화방 식별값
     * @param request 파라미터
     * @return token 식별
     */
    @PostMapping(produces = "application/json; charset=utf8")
    public ResponseEntity<DivvyupResponse> divvyupMoney(@RequestHeader(value = "X-USER-ID") long userId,
                                                        @RequestHeader(value = "X-ROOM-ID") String roomId,
                                                        @RequestBody DivvyupRequest request) {
        Map<String, String> resultMap = new HashMap<>();
        DivvyupMaster master = service.divvyup(roomId, userId, request.getMoney(), request.getCount());
        resultMap.put("token", master.getToken());
        return ResponseEntity.ok(new DivvyupResponse(master.getResultCode(), master.getResultMessage(), resultMap));
    }

    /**
     * 받기 기능 API
     * @param userId 사용자 식별값
     * @param roomId 대화방 식별값
     * @param token 경로상의 token 값
     * @return 받은 금액
     */
    @PutMapping(value = "/{token:[a-zA-Z0-9]{3}}", produces = "application/json; charset=utf8")
    public ResponseEntity<DivvyupResponse> takeMoney(@RequestHeader(value = "X-USER-ID") long userId,
                                                     @RequestHeader(value = "X-ROOM-ID") String roomId,
                                                     @PathVariable("token") String token) {
        Map<String, Long> resultMap = new HashMap<>();
        DivvyupTake userTake = service.take(roomId, userId, token);
        resultMap.put("takeMoney", userTake.getTakeMoney());
        return ResponseEntity.ok(new DivvyupResponse(userTake.getResultCode(), userTake.getResultMessage(), resultMap));
    }

    /**
     * 조회 기능 API
     * @param userId 사용자 식별값
     * @param roomId 대화방 식별값
     * @param token 경로상의 token 값
     * @return 뿌리기 결과 목록
     */
    @GetMapping(value = "/{token:[a-zA-Z0-9]{3}}", produces = "application/json; charset=utf8")
    public ResponseEntity<DivvyupResponse> viewResult(@RequestHeader(value = "X-USER-ID") long userId,
                                                      @RequestHeader(value = "X-ROOM-ID") String roomId,
                                                      @PathVariable("token") String token) {
        Map<String, Object> resultMap = new HashMap<>();
        DivvyupMaster master = service.viewResult(roomId, userId, token);
        resultMap.put("data", master);
        return ResponseEntity.ok(new DivvyupResponse(master.getResultCode(), master.getResultMessage(), resultMap));
    }
}
