package com.alibaba.util;

public class Node implements Comparable<Node>{  
  
    // 元素内容  
    public int number;  
    // 元素次数对应的字节  
    public byte by;  
    // 表示结点是左结点还是右结点,0表示左，1表示右  
    public String type = "";  
    // 指向该结点的左孩子  
    public Node leftChild;  
    // 指向该结点的右孩子  
    public Node rightChild;  
  
    /** 
     * 构造方法，需要将结点的值传入 
     *  
     * @param number 
     *            结点元素的值 
     */  
    public Node(int number) {  
        this.number = number;  
    }  
  
    /** 
     * 构造方法 
     *  
     * @param by 
     *            结点元素字节值 
     * @param number 
     *            结点元素的字节出现次数 
     *  
     */  
    public Node(byte by, int number) {  
        super();  
        this.by = by;  
        this.number = number;  
    }  
  
    @Override  
    public int compareTo(Node o) {  
        // TODO Auto-generated method stub  
        return this.number - o.number;  
    }  
  
}
