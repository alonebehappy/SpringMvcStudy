package com.alibaba.util;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Set;

public class Tree {  
	  
    /** 
     * 根据map生成一个由Node组成的优先队列 
     *  
     * @param map 
     *            需要生成队列的map对象 
     * @return 优先队列对象 
     */  
    public PriorityQueue<Node> map2Queue(HashMap<Byte, Integer> map) {  
        // 创建队列对象  
        PriorityQueue<Node> queue = new PriorityQueue<Node>();  
        if (map != null) {  
            // 获取map的key  
            Set<Byte> set = map.keySet();  
            for (Byte b : set) {  
                // 将获取到的key中的值连同key一起保存到node结点中  
                Node node = new Node(b, map.get(b));  
                // 写入到优先队列  
                queue.add(node);  
            }  
        }  
        return queue;  
    }  
  
    /** 
     * 根据优先队列创建一颗哈夫曼树 
     *  
     * @param queue 
     *            优先队列 
     * @return 哈夫曼树的根结点 
     */  
    public Node queue2Tree(PriorityQueue<Node> queue) {  
        // 当优先队列元素大于1的时候，取出最小的两个元素之和相加后再放回到优先队列,留下的最后一个元素便是根结点  
        while (queue.size() > 1) {  
            // poll方法获取并移除此队列的头，如果此队列为空，则返回 null  
            // 取出最小的元素  
            Node n1 = queue.poll();  
            // 取出第二小的元素  
            Node n2 = queue.poll();  
            // 将两个元素的字节次数值相加构成新的结点  
            Node newNode = new Node(n1.number + n2.number);  
            // 将新结点的左孩子指向最小的，而右孩子指向第二小的  
            newNode.leftChild = n1;  
            newNode.rightChild = n2;  
            n1.type = "0";  
            n2.type = "1";  
            // 将新结点再放回队列  
            queue.add(newNode);  
        }  
        // 优先队列中留下的最后一个元素便是根结点，将其取出返回  
        return queue.poll();  
    }  
  
    /** 
     * 根据传入的结点遍历树 
     *  
     * @param node 
     *            遍历的起始结点 
     */  
    public void ergodicTree(Node node) {  
        if (node != null) {  
            System.out.println(node.number);  
            // 递归遍历左孩子的次数  
            ergodicTree(node.leftChild);  
            // 递归遍历右孩子的次数  
            ergodicTree(node.rightChild);  
        }  
    }  
  
    /** 
     * 根据哈夫曼树生成对应叶子结点的哈夫曼编码 
     *  
     * @param root 
     *            树的根结点 
     * @return 保存叶子结点的哈夫曼map 
     */  
    public HashMap<Byte, String> tree2HfmMap(Node root) {  
        HashMap<Byte, String> hfmMap = new HashMap<>();  
        getHufmanCode(root, "", hfmMap);  
        return hfmMap;  
    }  
  
    /** 
     * 根据输入的结点获得哈夫曼编码 
     *  
     * @param node 
     *            遍历的起始结点 
     * @param code 
     *            传入结点的编码类型 
     * @param hfmMap 
     *            用来保存字节对应的哈夫曼编码的map 
     */  
    private void getHufmanCode(Node node, String code, HashMap<Byte, String> hfmMap) {  
        if (node != null) {  
            code += node.type;  
            // 当node为叶子结点的时候  
            if (node.leftChild == null && node.rightChild == null) {  
                hfmMap.put(node.by, code);  
            }  
            // 递归遍历左孩子的次数  
            getHufmanCode(node.leftChild, code, hfmMap);  
            // 递归遍历右孩子的次数  
            getHufmanCode(node.rightChild, code, hfmMap);  
        }  
    }  
  
}
