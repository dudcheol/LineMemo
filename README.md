# LineMemo
* 이미지 첨부 기능을 포함하는 메모앱입니다.  
* 이미지는 [ 로컬에 저장된 이미지, 직접 촬영한 이미지, 외부 URL 이미지 ]를 첨부할 수 있습니다.
* 자체 카메라와 디바이스에서 기본으로 제공하는 카메라, 기기에 존재하는 다른 카메라 앱으로 촬영이 가능합니다.
  
### 디자인 패턴
* LiveData를 활용한 Model - View - ViewModel (이하 MVVM) 패턴을 사용  
  
### 사용 라이브러리
* Room (AndroidX)
* CameraX (AndroidX)
* Material Design (Android)
* [Junit](https://junit.org/junit4/)
* [Glide](https://github.com/bumptech/glide)
* [PhotoView](https://github.com/chrisbanes/PhotoView)
  
## 스크린샷
![main](https://user-images.githubusercontent.com/40655666/76815438-fa9cd800-6840-11ea-82a6-05814ad75ef0.png)
![create](https://user-images.githubusercontent.com/40655666/76820108-7735b380-684d-11ea-97e0-8a1bf9a79484.png)  
![view](https://user-images.githubusercontent.com/40655666/76815656-9af2fc80-6841-11ea-8660-efd8b8f1326e.png)
![detail](https://user-images.githubusercontent.com/40655666/76816071-9f6be500-6842-11ea-89eb-a9b7e0dedeef.png)  
![edit](https://user-images.githubusercontent.com/40655666/76817297-c677e600-6845-11ea-8a36-0ef15dfbf8d8.png)
![cameraPick](https://user-images.githubusercontent.com/40655666/76815724-beb64280-6841-11ea-98c5-83756312394f.png)  
![externalImage](https://user-images.githubusercontent.com/40655666/76816122-c6c2b200-6842-11ea-9ec9-851a9241553c.png)
![loadFail](https://user-images.githubusercontent.com/40655666/76816833-9a0f9a00-6844-11ea-8a08-6d259661436d.png)  
![camera](https://user-images.githubusercontent.com/40655666/76815781-ea392d00-6841-11ea-91d9-e97bc5a2fe19.png)
![captured](https://user-images.githubusercontent.com/40655666/76816159-e4901700-6842-11ea-808e-eeba406deffd.png)  
  
## 좋은 점
* 유닛테스트 사용
* 뷰모델과 DB의 기능 테스트 코드 작성
* 구조화가 잘 되어 있고 함수 분리가 잘 되어있어 가독성이 좋음
* 분류별로 패키지 분리를 잘해주었고 세부적으로 더 쪼개지는 경우에는 하위에 패키지를 더 분리해 줌
  
## 개선해야 할 점
* 카메라 구현 시 가로/세로에 대한 예외처리가 부족(가로 모드에서 line camera로 찍은 결과물을 메모에 붙일 때 기존 이미지가 모두 사라지는 현상)
* 기본 카메라로 진입 시 권한 팝업 부재
* AsyncTask를 사용하였지만 get()을 사용하여 작업이 완료될때까지 UI쓰레드를 대기상태로 만듦(DB)
* AsyncTask는 지원중단예정인 api임. 다른 방식으로 비동기작업을 구현할 것
