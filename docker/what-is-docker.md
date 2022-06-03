# What is Docker?

## Docker

## Dockerfile

설명서, 레시피

어플리케이션 구동 시 꼭 필요한 파일

어떤 Dependency가 필요한지

환경변수 설정

실행 스크립트 지

## Doker Image

어플리케이션을 실행하는데 필요한 코드, 런타임 환경, 시스템 툴, 시스템 라이브러리, 모든 툴&#x20;

스냅샷 해서 이미지로 만들어 둔다 라고 생각하면 됨

한번 만들어지면 불변

클래스라고 생각하면 좋음&#x20;

## Docker Container

고립된 환경에서 아까 찍은 이미지를 올려놓고 어플리케이션을 구동하게되는 것

컨테이너에서 각각 구동되는 어플리케이션은 파일도 올릴 수 있고 변경이 가능함

각각 컨테이너에서 수정된 파일이 있다면 Image에는 영향을 끼ㅈ치지 않음

생성된 instance라고 생각하면 편함



## Container Registry

생성된 이미지를 올려둘 수 있는 곳

### Public

* docker hub
* red hat
* github packages

### Private

* aws
* google cloud
* microsoft azure

&#x20;

![https://youtu.be/LXJhA3VWXFA](<../.gitbook/assets/image (1).png>)

* 일단 로컬 머신과 서버에 둘다 도커가 깔려있어야 함
* Dockerfile을 토대로 이미지를 생성해서
* 그 이미지를 Container Registry로 푸시해줌
* Server는 이미지를 Pull 해와서&#x20;
* 컨테이너에 넣어서 어플리케이션을 구동할 수 있게 됨

![](../.gitbook/assets/image.png)

* DockerFile을 작성할 때는 제일 빈번하게 업데이트 되는 것을 제일 아래에 적어야 한다.
* 그 이유는 파일을 읽어올 때 Layer 4 부터 읽어오므로, 이미지를 만들고 나중에 소스코드가 수정되어서 이미지를 새로 만들어야 할 때, 수정된 부분만 업데이터 해주고 나머지 변경되지 않은 것은 캐시된 것을 사용하여 빌드하기 때문에 이미지를 만드는 시간을 단축할 수 있
*

![](<../.gitbook/assets/image (2).png>)

컨테이너를 생성하여 어플리케이션을 돌리려면 run 명령어를 입력해야 함

복수의 이미지와 컨테이너들을 한꺼번에 빌드하고 돌리기 위해서는 docker-compose.yml 파일을 이용하여 각각 어떻게 돌릴 것인지를 환경설정 해준다

그 후 `docker-compose up 으로 명령어를 입력해주면 따로따로 빌드하지 않아도 한꺼번에 처리가 되는 것을 알 수 있다.`

다른 것과 마찬가지로 docker-compose up -d 로 옵션을 붙여주면 background에서 실행될 수 있도록 할 수 있다.

{% embed url="https://www.yalco.kr/36_docker/" %}
Docker 명령어들 참
{% endembed %}
