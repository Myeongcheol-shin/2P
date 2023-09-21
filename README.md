<img src="https://capsule-render.vercel.app/api?type=waving&color=auto&height=200&section=header&text=위치기반플래너&fontSize=90" />

<p align="center"> 
  <img src="https://github.com/Myeongcheol-shin/location/assets/82868004/13b76cf1-3be4-4148-a5cb-d9a23f3504f0" height="300" width="300"> 
</p>

# 2P?
`2P`는 `Place Planner`로 장소 기반 플래너입니다.  
기존 플래너와 달리 해당 장소에 실제로 위치해야만, 계획을 완료시킬 수 있습니다.

# 개발 배경과 앱 설명
일반적인 <span style="color:#ffff00" >**'Planner'** </span>앱은 일반적으로 시간과 장소 그리고 컨텐츠를 입력으로 받습니다. 
**'운동을 하기', '독서실 가기'** 와 같이 계획은 하지만, 실제로는 "다음에 가지뭐~" 라고 생각하며 실제로 가지않는 때가 많았습니다. 이러한 경험을 통해서 나는<span style="color:#ffff00"> **'위치 기반 플래너'**</span> 를 만들기로 했습니다. 

일반적인 플래너와 동일하게 시간 장소 그리고 컨텐츠를 입력받습니다. 이 때 장소의 경우에는 실제 존재하는 장소를 사용자가 등록합니다.    
실제로 사용자가 해당 계획을 완료하기 위해서는 설정된 장소에 100m 이내의 위치 하면서, 지정한 시간의 +-30분인 시간일 경우에만 완료가 가능합니다.  
그리고 계획을 했지만, 시간을 확인하지 않아 놓치는 경우가 발생할 수도 있으므로, 알람 기능을 통해 사용자에게 알림을 보냅니다.  
`Kakao Api`의 `장소 검색 API`와 `MAPVIEW`를 사용하여 검색된 장소에 대한 위도와 경도 값을 알아내고, MAPVIEW를 통해 사용자가 선택한 위치의 지도 정보를 보여줌으로써 확인하도록 하였습니다.

# 기능
대표적인 기능으로는 크게 다음과 같습니다.

1.  <span style="color:#00b050">일주일 단위로 일정을 확인을 할 수 있습니다.</span> [구현]
2.  <span style="color:#00b050">지정한 장소와 지정된 시간이 아니면, 계획을 완료할 수 없습니다.</span>[구현]
3. <span style="color:#00b050"> 계획 관리</span>[구현]
	* 계획을 잘못 등록해 놓을 수 있기 때문에 계획의 수정 기능은 제공됩니다. 
		* 단! **강제로 계획을 완료시킬 수 없습니다.**
    * 반드시 장소 기반으로 완료해야 합니다.
	* 삭제 기능
4. <span style="color:#00b050">플랜을 다양한 필터를 통해 정렬 가능</span>[구현]
	1. 날짜 순
	2. 완료한 계획
	3. 미완료 계획
5. <span style="color:#00b050">알림 기능</span> [구현]
	* 정해진 시간에 도달하면, 사용자에게 인앱 알림을 보냅니다.

# Skill
개발을 위해 사용한 기술들은 아래와 같습니다.
`Android` / `Kotlin` / `Hilt` / `Room` / `Coroutine` / `Flow` / `KaKao API` / `Retrofit` / `MVVM` / `Fused Location Provider API` / `AlarmManager` /`Notification`

# 발생한 문제와 사용한 기술정리
관련된 정리는 해당 레포지토리의 `위치 기반 플래너` 폴더에 정리되어있습니다.  
[바로가기 - Link](https://github.com/Myeongcheol-shin/location/tree/main/%E1%84%8B%E1%85%B1%E1%84%8E%E1%85%B5%20%E1%84%80%E1%85%B5%E1%84%87%E1%85%A1%E1%86%AB%20%E1%84%91%E1%85%B3%E1%86%AF%E1%84%85%E1%85%A2%E1%84%82%E1%85%A5)

# 사용 영상

