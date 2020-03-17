# LineMemo
> 이미지 첨부 기능을 포함하는 메모앱입니다.  
이미지는 [ 로컬에 저장된 이미지, 직접 촬영한 이미지, 외부 URL 이미지 ]를 첨부할 수 있습니다.

## 스크린샷

### 디자인 패턴
LiveData를 활용한 Model - View - ViewModel (이하 MVVM) 패턴을 사용  

### 사용 라이브러리
* Room DB (AndroidX)
* CameraX API (AndroidX)
* [PhotoView](https://github.com/chrisbanes/PhotoView)

## 개선해야 할 점
* 카메라 구현 시 가로/세로에 대한 예외처리가 부족
* 카메라 진입 시에는 권한에 대한 팝업
* AsyncTask를 사용하였지만 get()을 사용하여 작업이 완료될때까지 UI쓰레드를 대기상태로 만듦(DB)
* AsyncTask는 지원중단예정인 api임. 다른 방식으로 비동기작업을 구현할 것
