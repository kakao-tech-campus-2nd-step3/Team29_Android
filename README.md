# Team29_Android
29조 안드로이드

## 리뷰 요구 사항
### 🔎 작업 내용

>- #72
>- #75
>- #76
>- #77
>-  #78
>- #82
>- #83


### 💬 리뷰 요구사항 (선택)

> 1. 그 때 멘토링에서 리뷰 받은 대로 documentId 를 arguments 를 통해 전달하도록 리팩토링하였습니다. 그런데 다음과 같은 에러가 발생했습니다. 사용성을 심각하게 해치는 에러가 아니고, 다른 작업들을 먼저 해야할 것 같아 일단은 기록만하고 다른 작업(녹음)을 하고 있긴 합니다. #84 에 버그를 기록해두었는데 혹시 해당 이슈와 관련해서 의견을 받을 수 있을까요? 또한 다음처럼 수정하는게 맞는지 궁금합니다.
>>- 추측으로는 처음 Fragment 를 어답터에 끼울때는 currentId 를 0으로 세팅하고 그 이후는 페이지 이동 이벤트를 통해서만 pageNumber 을 세팅하기 때문에 처음에 표시되지 않는 AIFragment 는 초기 전달했던 currentId 를 띄우는 것인가 했습니다..

> 2. 즐겨찾기 기능을 구현할 때 Room말고 Sharedpreferences를 사용할 예정인데, Sharedpreferences는 비교적 간단한 것들을 저장하는데 유용하다고 해서 사용해도 될 지 궁금합니다!

> 3. 위와 비슷하게 녹음 기능을 현재 구현 중인데 녹음이 종료되면 녹음 도중에 있던 페이지 이벤트들도 같이 서버에 전송하고자 했습니다. 그 후 바로 해당 이벤트들은 삭제할 예정입니다. 이 경우 RoomDB 를 사용해서 저장하는게 좋을까요 아니면 다음처럼 텍스트로 파일에 적어서 내부저장소에 저장해두는게 좋을까요?
``` fun savePageTurnEvents(recordingId: Long, events: List<PageTurnEventDto>) {
        val file = File(baseDir, "page_turn_events_$recordingId.txt")
        file.writeText(events.joinToString(separator = "\n") { event ->
            "${event.prevPage},${event.nextPage},${event.timestamp}"
        })
    }
```

> 4. 녹음 기능을 구현하고 있는데 NoteActivity의 툴바에서 녹음 시작 혹은 종료를 누르면 이 상태를 어떤식으로 RecordFragment 에 전달하는 것이 좋을까요?
>> - 현재는 'NotetakingActivity 에서 클릭 리스너를 통해 SidebarFragment 로 이벤트 전달 -> RecordbarFragment 의 녹음 시작 메서드 호출'의 방식으로 전달하고 있는데 괜찮을까요? (현재 다른 부분에서 에러가 발생해서 해당 PR에 관련 커밋은 올리지 못했습니다.. 해결되면 여기에 PR 링크 추가하겠습니다)
>> - 참고 구조입니다.

![제목 없는 다이어그램 drawio (1)](https://github.com/user-attachments/assets/c3f9f707-217b-4f13-ba5d-16b9fcf13896)


### 📸 이미지 첨부 (선택)
- 현재 PR에 기능적 변화(UI)는 멘토링 이후로 없습니다.


### ➕ 이슈 링크

>- #84
>- #72
>- #75
>- #76
>- #77
>-  #78
>- #82
>- #83 
