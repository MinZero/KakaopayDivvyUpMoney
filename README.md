# KakaoPay 뿌리기 API
카카오페이 뿌리기 기능 구현하기

## 요구사항
* 뿌리기, 받기, 조회 기능을 수행하는 REST API 를 구현합니다.
    * 요청한 사용자의 식별값은 숫자 형태이며 "X-USER-ID" 라는 HTTP Header로
전달됩니다.
    * 요청한 사용자가 속한 대화방의 식별값은 문자 형태이며 "X-ROOM-ID" 라는
HTTP Header로 전달됩니다.
    * 모든 사용자는 뿌리기에 충분한 잔액을 보유하고 있다고 가정하여 별도로
잔액에 관련된 체크는 하지 않습니다.
* 작성하신 어플리케이션이 다수의 서버에 다수의 인스턴스로 동작하더라도 기능에
문제가 없도록 설계되어야 합니다.
* 각 기능 및 제약사항에 대한 단위테스트를 반드시 작성합니다.

## 구현 기술
* 웹서버 : Spring Boot
* DBMS : Marid DB
* Redisson : 다중 서버를 위한 Distributed Lock 사용

## DBMS
DIVVYUP_MASTER - 뿌리기 마스터 테이블
* TOKEN 의 생성 가능한 조합은 알파벳 대소문자와 숫자를 조합하면 238328 건입니다.
* TOKEN 을 랜덤으로 생성하고 있기 때문에 중복되는 경우가 발생할 수 있을 것 같아
 대화방의 식별값을 묶어서 Primary Key 처리하였습니다.
````
CREATE TABLE `DIVVYUP_MASTER` (
  `TOKEN` varchar(3) NOT NULL,
  `ROOM_ID` varchar(50) NOT NULL,
  `USER_ID` int(11) NOT NULL,
  `MONEY` bigint(20) NOT NULL,
  `COUNT` int(11) NOT NULL,
  `INSERT_DATE` datetime NOT NULL,
  PRIMARY KEY (`ROOM_ID`,`TOKEN`)
)
````

DIVVYUP_TAKE - 뿌려진 금액 목록
* MASTER 테이블에서 작성한 것 처럼 TOKEN 만으로 조회할 경우 
 중복 건이 도출 될 수 있을 것 같아 ROOM_ID 컬럼 추가해 놓았습니다.
* 조회를 위해 TOKEN, ROOM_ID 에 대한 인덱스를 생성하였습니다.
````
CREATE TABLE `DIVVYUP_TAKE_LIST` (
  `SEQNO` int(11) NOT NULL AUTO_INCREMENT,
  `TOKEN` varchar(3) NOT NULL,
  `ROOM_ID` varchar(3) NOT NULL,
  `TAKE_MONEY` bigint(20) NOT NULL,
  `USER_ID` int(11) DEFAULT NULL,
  `TAKE_DATE` datetime DEFAULT NULL,
  PRIMARY KEY (`SEQNO`),
  KEY `DIVVYUP_TAKE_LIST_TOKEN_IDX` (`TOKEN`,`ROOM_ID`) USING BTREE
) 
````

USER_MONEY - 사용자 잔고
* 테스트를 위해 임의로 생성한 테이블 입니다.
````
CREATE TABLE `USER_MONEY` (
  `USER_ID` int(11) NOT NULL,
  `MONEY` bigint(20) NOT NULL,
  PRIMARY KEY (`USER_ID`)
)
````


## API

### 1. 뿌리기 API
기능 : 뿌릴 금액과 뿌릴 인원을 요청값으로 받아 대화방 참여자에게 뿌립니다.

Request
* URI : /api/divvyup/{token}
* Method : PUT
* Header 
    * X-ROOM-ID : 대화방 식별값
    * X-USER-ID : 사용자 식별값
 
Response
* 결과코드 와 받은 금액을 전달합니다.
````
{
    "code":"SUCCESS",
    "message":"",
    "body":{
        "token":"urt"
    }
}
````
### 2. 받기 API
기능 : 대화방에 뿌려진 돈을 선착순으로 받습니다.

Request
* URI : /api/divvyup/{token}
* Method : PUT
* Header 
    * X-ROOM-ID : 대화방 식별값
    * X-USER-ID : 사용자 식별값
    
Response
* 결과코드 와 받은 금액을 전달합니다.
````
{
    "code":"SUCCESS",
    "message":"",
    "body":{
        "takeMoney":476190
    }
}
````

### 3. 조회 API
기능 : 뿌린 사용자가 현재 상태를 확인합니다.
Request
* URI : /api/divvyup/{token}
* Method : PUT
* Header 
    * X-ROOM-ID : 대화방 식별값
    * X-USER-ID : 사용자 식별값

Response
* 결과코드 와 받은 사용자 목록을 전달합니다.
````
{
    "code":"SUCCESS",
    "message":"",
    "body":{
        "data":{
            "token":"gzA",
            "roomId":"R01",
            "userId":123456,
            "totalMoney":100000,
            "count":4,
            "createDate":"2020-11-22T21:50:00.000+00:00",
            "resultCode":"SUCCESS",
            "resultMessage":"",
            "takeList":[
                {
                    "seqNo":636,
                    "token":"gzA",
                    "roomId":"R01",
                    "takeMoney":90900,
                    "takeUserId":0,
                    "takeDate":null,
                    "resultCode":null,
                    "resultMessage":null
                },
                {
                    "seqNo":637,
                    "token":"gzA",
                    "roomId":"R01",
                    "takeMoney":4330,
                    "takeUserId":0,
                    "takeDate":null,
                    "resultCode":null,
                    "resultMessage":null
                }
            ]
        }
    }
}
````

### 4. 공통
오류가 발생했을 경우 code에 FAIL 값이 전달되고 message에 오류 정보가 담깁니다.
````
{
    "code":"FAIL",
    "message":"잔액부족",
    "body":{
            "token":"urt"
    }
}
````

### 개선필요사항
* 뿌리기 실행 시 해당 대화방 식별값과 중복된 토큰이 존재할 경우 SQLException 이 발생됩니다.