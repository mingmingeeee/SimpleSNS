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
- [ ] token 인증시 User 조회, 그 이후 또 user 조회

  => 인증 과정에서 2번의 DB I/O 발생

  (1) token에서 username 추출, "username" DB에서 조회

  (2) create할 때 또 userName으로 DB I/O 발생

- [ ] 매 API 요청시마다 User 조회
- [ ] 
어떤 작업이 cost, 부하가 많이 드는지 고려하기

- [ ] Alarm까지 생성해야 응답하는 API (LIKE)

현재는 alarm이 like까지 다 save된 상태에서 일어나는 작업이기 때문에 너무 결속됨

조금의 지연이 일어나도 괜찮음 => 분리시키기

- [ ] Alarm List API를 호출해야만 갱신되는 알람 목록 (새로고침해야 알람이 있음)

업데이트가 없을 수도 있는데 계속 해줘야 해서 서버 부하 있을 듯

- [ ] DB Query들이 최적화 되어있을까

JPA (객체화): Query를 직접 작성하는 게 아니라

Entity들 바탕으로, Method 바탕으로 생성되어 날아감

연관 관계 설정한게 내가 원하는 대로 fetch, join돼서 날아가고 있을까 check 필요


### 요약
- 코드의 비 최적화
- 중복된 DB IO
- 기능간의 강한 결합성


### 대규모 트래픽시 최적화 방법
DB select, 외부 API call과 같은 무거운 작업들일 때 중복된 것 줄여주기

1. Cache(Redis) 사용
변하지 않는 데이터들 조회 위해 캐시 사용
![4545](https://user-images.githubusercontent.com/45472076/230905903-2fa08302-d71e-40be-bf15-13ad7db9290e.PNG)

2. Kafka 사용 (비동기적 처리)
Produce를 하면 Consumer가 받아와서 Message를 처리하는 것
강한 결합 완화

