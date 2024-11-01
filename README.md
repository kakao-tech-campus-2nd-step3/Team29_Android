# Team29_Android
29조 안드로이드

> 1. 아래 **리뷰 받고 싶은 부분 동영상**을 보면 `텍스트` 버튼이 `텍스트 추가` 와 `텍스트 편집바 토글` 두 가지 역할을 담당하다보니 사용성이 떨어진다는 느낌을 받았습니다. 관련해서 어떠한 해결 방법을 채택하면 좋을까요? 
>> - 1. `텍스트` 버튼을 누르면 `텍스트 편집바` 만 토글되도록 하며 -> 텍스트 추가는 `텍스트 편집바` 에서 가능하도록 버튼(아이콘)을 추가한다
>> - 2. `텍스트` 버튼을 누르면 `텍스트`만 추가되며, 해당 `텍스트` 를 누르면 `텍스트 편집바` 가 표시되도록 한다.
>> - 3. 제3의 방법

> 2. 주석을 관리하기 위해 `PdfEditor` 은 주석의 PDF 주석의 UI적 요소(상자 추가 및 이동 등)를 처리하며, `PdfPageViewModel`에서는 각 페이지에서 주석의 데이터 처리(스토리지 저장 및 로드)를 담당하고 있습니다. 파일이 많아지니 확실히 복잡해지는 것 같습니다. 주석을 처리하기 위해 PdfEditor (UI적 요소 처리) 와  PdfViewModel (데이터적 요소 처리) 로 나눴는데 이에 관한 의견을 듣고 싶습니다!
>> 정리하면 `PdfEditor`는 PDF 페이지 UI에 텍스트 상자를 추가 및 이동을 담당하며, 데이터와는 연관되지 않고 있고, `PdfPageViewModel`은 개별 페이지의 주석 데이터를 관리하며, (서버 및 로컬으로부터) 주석의 추가와 불러오기를 처리합니다. 
`PdfPageFragment`는 개별 페이지를 나타내며, 여기서 ViewModel 과 PdfEditor 을 통해 주석 데이터와 UI를 연결하여 페이지별 주석 표시를 담당합니다. `PdfViewerFragment`는 여러 PdfPageFragment를 포함하는 컨테이너입니다. (어답터를 사용)

> 3. 현재 페이지를 넘기면 [(1). 기존에 화면에 있던 주석들을 삭제한다.  (2). 로컬 또는 서버에서 주석 정보를 가져와서 해당 페이지에 기존에 있던 주석들을 로드한다. (3). 주석을 추가하면 이를 바로 UI에 추가하고 +  로컬 및 서버에 주석을 저장한다. ] 의 플로우로 되어있습니다. 해당 플로우가 괜찮을 지 궁금합니다. 

> 4. 또한 서버에서 documentId 로 해당 document 에 대해 모든 주석을 조회하는 API 는 있지만 (https://www.notion.so/351aaf0cfd6b464194f18ddcc425e2e5) document에서 페이지별로 주석을 조회하는 API 는 따로 없는 상태에서 각 페이지 별로 주석을 띄운다고 할 때 어떤 방식이 권장될까요?
(1) PDF 에 들어가면 가장 처음에 서버에 모든 주석 조회를 요청 후 이를 local DB 에 copy 를 해두고 시작한다 (2) 서버에 페이지별로 주석을 조회하는 API 를 생성해달라고 요청한 후 해당 페이지에 진입 시 그 때마다 서버에 주석 요청을 보낸다. 

> 5. 지난번에 Sidebar Bug(#84) 에 대한 리뷰도 받았는데 리뷰 받은 대로 SharedViewModel 을 써서 했는데 AI 탭 전환 시 값이 안넘어 오는 거 같아 이 부분은 더 조사해야할 것 같습니다. 시간이 걸릴 것 같아서 우선 다른 작업들 먼저 PR 올리겠습니다 ㅠㅠ (아래 **관련 로그 사진** 첨부했습니다)

## 📸 이미지 첨부 (선택)
- 리뷰 받고 싶은 부분 (**리뷰 1번**)
> - `텍스트` 를 누르면 텍스트 박스가 생성됩니다 동시에 텍스트 편집바가 눈에 보이게 됩니다. 다시 한 번 텍스트를 누르면 텍스트 편집바가 눈에 안보이게 됩니다. 여기서 다시 텍스트를 누르면 텍스트 편집바가 눈에 보이게 되며 다시 텍스트 박스가 생성됩니다.

https://github.com/user-attachments/assets/64efa2fb-50c4-4b02-9535-9bbcb6ea14f8

- **리뷰 5번 관련 로그**
> - 로그찍어서 봤는데 AITab으로 가면 SharedViewmodel 의  pageNumber 가 null 로 가져와짐 (관련 commit [Refactor: arguemnts 를 통한 데이터 전달](https://github.com/kakao-tech-campus-2nd-step3/Team29_Android/pull/83/commits/b4792eaf7050cef6bd46782d602933b122dd711a))
![image](https://github.com/user-attachments/assets/a687e519-4950-4bf5-93e7-ba2740cb9371)


- 주석 추가 (현재 화면에 다음처럼 띄우기 & 드래그까지 구현 / 세부적인 텍스트 편집 로직은 아직 X )
> - 텍스트 버튼 클릭 시 텍스트 편집바가 화면에 표시됩니다

https://github.com/user-attachments/assets/3a666b4d-cdaf-44ac-9baf-e7d9878628b9


- 녹음 도중 발생한 PageEvent Room 에 저장 (리팩토링)
![image](https://github.com/user-attachments/assets/c60c9ae5-9aa1-4c15-ac89-1bd4bc0ed1e7)
- 주석 Annotation Room 에 저장 (아직 세부적으로 위치나 width, height 정보를 가져오는 로직은 구현하지 못했습니다)
![image](https://github.com/user-attachments/assets/38bb4ab9-8dff-4f20-91e8-97b633e34e4a)
