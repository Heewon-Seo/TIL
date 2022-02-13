package study;

public class ArrayToBinaryTree {

    // 데이터를 반씩 쪼개서 원하는 데이터를 찾아내는 것이 이진검색트리
    // [ 0 1 2 3 4 5 6 7 8 9 ]
    // 4를 기준으로 반을 쪼개서
    //         [4]
    //    [1]        [7]
    // [0]  [2]    [5]  [8]
    //        [3]   [6]   [9]
    // 한번 이동할때마다 검색해야 하는 데이터의 수가 반으로 줄어드니까
    // 이 트리의 시간복잡도는 O(log n)


    // 반으로 나눠서 중간값을 찾는 일이 반복 > 재귀적으로 하면 될 거 같음
    /*

    - array
    - start index
    - end index

    중간값은 (start + end) / 2
    로 찾으면 됨!

     */

    static class Tree {
        class Node {
            int data;
            Node left;
            Node right;

            Node(int data) {
                this.data = data;
            }
        }

            Node root; // Tree의 멤버변수

            public void makeTree(int[] a) { // 재귀함수에 필요한 데이터를 처음으로 던져줌
                // 재귀호출이 끝나면 제일 꼭대기에 있는 노드를 root에 저장해줌
                root = makeTreeR(a, 0, a.length -1);
            }

            public Node makeTreeR(int[] a, int start, int end) {
                if(start>end) return null; // 끝나는 시점을 명확하게 명시해줘야함
                int mid = (start+end)/2;

                Node node = new Node(a[mid]); // 중간 지점을 노드로 저장함

                node.left = makeTreeR(a, start, mid-1);
                node.right = makeTreeR(a, mid+1, end);

                return node;
            }

            public void searchBTree(Node n, int find) {
                if(find < n.data) {
                    System.out.println("Data is smaller than "+ n.data);
                    searchBTree(n.left, find);
                } else if (find > n.data) {
                    System.out.println("Data is bigger than " + n.data);
                    searchBTree(n.right, find);
                } else {
                    System.out.println("Data found!");
                }
            }
        }

    public static void main(String[] args) {

        int[] a = new int[10];
        for(int i = 0; i < a.length; i++) {
            a[i] = i;
        }

        Tree t = new Tree();
        t.makeTree(a);
        t.searchBTree(t.root, 2);

        /*
             (4)
            /   \
           /     \
          /       \
        (1)       (7)
        /  \     /   \
       (0) (2) (5)   (8)
             \   \     \
             (3) (6)   (9)
         */





    }

}
