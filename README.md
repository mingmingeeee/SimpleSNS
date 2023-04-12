# SimpleSNS
간단한 SNS 서비스 


## 배포
CloudType 사용 (https://cloudtype.io/)

## 사용 기술
### Redis
DB: query | Cashing: Key-value
- Caching: DB에 부하를 주지 않으면서 더 쉽게 데이터를 가져올 수 있는 방법
- In-memory database: 데이터 휘발성, cache로 사용하는 경우에는 DB에 원본 데이터가 있으므로 큰 문제는 없음 but 죽었을 때 순간적으로 부하 높아질 것

|    | Redis | 로컬 캐싱 |
|---|---|---|
| 특징 | In-memory database, key-value 형태의 데이터베이스, single thread | 서버 내에 caching |
| 장점 | 여러 instance가 하나의 데이터 공유 가능, 일반 DB보다 빠름 | 네트워크 타지 않기 때문에 Redis에 비해 빠름 |
| 단점 | 로컬 캐싱에 비해서는 느림 | 여러 instance로 구성된 서버의 경우 서버끼리 캐시 공유 불가 -> 하나의 데이터를 봐야할 경우에는 적합하지 않음 |


## 대규모 트래픽시 고려사항
### 기존 문제점 
- [x] token 인증시 User 조회, 그 이후 또 user 조회

  => 인증 과정에서 2번의 DB I/O 발생

  (1) token에서 username 추출, "username" DB에서 조회

  (2) create할 때 또 userName으로 DB I/O 발생

- [x] 매 API 요청시마다 User 조회

  어떤 작업이 cost, 부하가 많이 드는지 고려하기

- [ ] Alarm까지 생성해야 응답하는 API (LIKE)

  현재는 alarm이 like까지 다 save된 상태에서 일어나는 작업이기 때문에 너무 결속됨

  조금의 지연이 일어나도 괜찮음 => 분리시키기

- [x] Alarm List API를 호출해야만 갱신되는 알람 목록 (새로고침해야 알람이 있음)

  업데이트가 없을 수도 있는데 계속 해줘야 해서 서버 부하 있을 듯

- [x] DB Query들이 최적화 되어있을까

  JPA (객체화): Query를 직접 작성하는 게 아니라

  Entity들 바탕으로, Method 바탕으로 생성되어 날아감

  연관 관계 설정한게 내가 원하는 대로 fetch, join돼서 날아가고 있을까 check 필요


### 요약
- 코드의 비 최적화
- 중복된 DB IO
- 기능간의 강한 결합성


## 대규모 트래픽시 최적화 방법
DB select, 외부 API call과 같은 무거운 작업들일 때 중복된 것 줄여주기

1. Cache(Redis) 사용
변하지 않는 데이터들 조회 위해 캐시 사용
![4545](https://user-images.githubusercontent.com/45472076/230905903-2fa08302-d71e-40be-bf15-13ad7db9290e.PNG)

2. Kafka 사용 (비동기적 처리)
Produce를 하면 Consumer가 받아와서 Message를 처리하는 것
강한 결합 완화

## N + 1 문제 (JPA 사용시)
연관 관계가 설정된 엔티티를 조회할 경우에 조회된 데이터 갯수(n) 만큼 연관관계의 조회 쿼리가 추가로 발생하여 데이터를 읽어오는 현상

알람 + user 한 개의 쿼리를 가져오는 걸 기대했지만

join해서 알림 entity 가져오는 쿼리한 개,

이 결과 값이 n개라고 하면  user의 row마다 총 n개의 쿼리 날아감

== n + 1

- fetch.eager

즉시 가져옴 (user를 사용하든 하지 않든)
- fetch.lazy

user 사용 전까지는 불리지 않음

## Delete 문제 (JPA 사용시) 
JPA: 영속성 관리 (Application에서 DB의 Life Cycle 관리)

삭제할 data 가져온 후 삭제함 => 가져올 필요가 없는 data인데 왜 굳이 가져와서 삭제해야 함?

===> JPA에서 사용하지 말고 쿼리 직접 작성하는 것이 좋음

## 주기적으로 Client에서 데이터를 가지고 와야 하는 경우
### Polling
- 일정 주기를 가지고 서버의 API 호출하는 방법
- **실시간으로 데이터가 업데이트 되지 않는**다는 **단점**
- **불필요한 요청** 발생, 따라서 **불필요한 서버 부하 발생**
-  대규모 트래픽에는 적합 X 서버 부하 높아질 것
-  호환성이 좋다는 장점

### Long-Polling
- 서버로 요청이 들어올 경우 일정 시간동안 댇기하였다가 요청한 데이터가 업데이트 된 경우 웹 브라우저로 응답 보냄
- 연결이 된 경우 실시간으로 데이터가 들어올 수 있다는 **장점**
- 데이터 업데이트가 빈번한 경우 Polling과 유사 **단점** -> 어차피... 계속해서 응답 보내야하기 때문

### SSE (Server-Sent-Event)
- 서버에서 웹브라우저로 데이터 전송 가능
- 웹브라우저에서 서버에 "데이터 업데이트 일어나면 알려줘~" Subscribe
- 서버쪽에서는 이 subscribe를 저장하고 있다가 업데이트 될 때마다 보내줌
- 트래픽 부하 적음
- 최대 동시 접속 횟수 **제한**

### WebSocket
- 서버에서 웹브라우저 사이 양방향 통신 가능


## Kafka (메시징 큐)
![77](https://user-images.githubusercontent.com/45472076/231314704-9492f1e6-ebe6-4114-a830-63aa8447e966.PNG)
1. Producer: 메시지 생성

Event(메시지) 생성할 때 **Key** 설정

- Key: 메시지가 어떻게 파티션이 될 지 정하는 값
- Topic: topic을 중심으로 메시지 생성, 그 topic에서 메시지 소비

  => Producer와 Consumer를 이어주는 key같은 값
  
  => 순서 보장을 위해 Key값이 같아야 함 => Consumer는 파티션을 차례대로 읽음


2. Broker: producer가 생성한 메시지 저장
3. Consumer: 메시지 소비

   => Consumer Group: 같은 Topic을 나눠서 읽음 (처리)
   
   => Consumer는 나 읽었어~ 하는 ack 보내줌

### RabbitMQ
메시징 큐 
