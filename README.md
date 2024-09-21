# Team29_Android
29조 안드로이드

## 리뷰 요규 사항
- 로그인 기능 구현 시 API 서버가 완성되지 않은 상태에서 현재 수준으로 구현해둬도 괜찮나요? 아니면 모킹같은 작업이 필요할까요?
(feature/login/src/main/java/com/iguana/login/LoginApi.kt)
- 로그인 기능 구현 시 뷰모델에서 레포지토리에 context 를 넘기고 있습니다.(SharedPref 사용을 위함) 이렇게 컨텍스트를 직접 넘겨도 되나요? (feature/login/src/main/java/com/iguana/login/LoginRepository.kt)
- 찾아보니 멀티 모듈이 협업하기에 좋은 것 같아 채택하기로 하였습니다. app 모듈, core:data 모듈, core:domain 모듈, feature:login 모듈, feature:notetaking 모듈 .. 기타 피쳐별 모듈.. 라이브러리 버전 관리를 위한 build-logic 모듈로 구성을 했는데 다중 모듈을 사용하는 팀들이 없는 것 같아 이런식으로 구현해도 될 지 모르겠습니다. 이런 구성으로 가도 괜찮을까요?
