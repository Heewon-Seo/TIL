# ListIterator 인터페이스

## Iterator란?

자바의 컬렉션(Collection)에 저장되어 있는 요소들을 순회하는 인터페이스

## Interator와 반복분의 차이

메모리적으로 중요한 차이가 있다.

예컨대, 링크드리스트에서 1부터 100까지의 요소를 가져오게 된다면&#x20;

![](<../.gitbook/assets/image (1).png>)

이렇게 시작 주소부터 index 만큼 요소들을 밟아가면서 조회하게 된다. 100까지 조회하려면 5151번을 조회해야 한다. 하지만 Iterator는 1번째부터 101번째까지의 요소에 대해 내부적으로 객체로 생성한 후, 순차적으로 조회하기 때문에 처음 주소로 돌아갈 필요가 없다. next 메서드를 통해 조회 시 요소 개수인 101번만 조회하게 된다.

## ListIterator는?

Iterator 인터페이스를 상속받아 여러 기능을 추가한 인터페이스이다. 원래 Iterator 인터페이스는 컬렉션 요소에 접근할 때 한 방향으로만 이동할 수 있지만, ListIterator는 요소의 추가, 대체, 인덱스 검색에서 양방향 이동을 지원한다.

```java
LinkedList<Integer> lnkList = new LinkedList<Integer>();

 

lnkList.add(4);

lnkList.add(2);

lnkList.add(3);

lnkList.add(1);

 

ListIterator<Integer> iter = lnkList.listIterator();

while (iter.hasNext()) {

    System.out.print(iter.next() + " ");

}

 

while (iter.hasPrevious()) {

    System.out.print(iter.previous() + " ");

}
```
