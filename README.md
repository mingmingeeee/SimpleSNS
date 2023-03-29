# SimpleSNS
간단한 SNS 서비스 


## 배포
CloudType 사용 (https://cloudtype.io/)

### Redis
DB: query | Cashing: Key-value
- Caching: DB에 부하를 주지 않으면서 더 쉽게 데이터를 가져올 수 있는 방법

|?|Redis|Local Caching|
|---|---|---|
|특징|In-memory database, key-value 형태의 데이터베이스, single thread|서버 내에 caching|
|장점|여러 instance가 하나의 데이터 공유 가능, 일반 DB보다 빠름|네트워크 타지 않기 때문에 Reids에 비해 빠름|
|단점|Local caching에 비해서는 느림|여러 instance로 구성된 서버의 경우 서버끼리 캐시 공유 불가 -> 하나의 데이터를 봐야할 경우에는 적합하지 않음|
