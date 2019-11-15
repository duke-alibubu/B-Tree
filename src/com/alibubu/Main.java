package com.alibubu;

public class Main {

    public static void main(String[] args) {

        BTree t = new BTree(3); // A B-Tree with minium degree 3
        for (int i = 10; i <= 300; i += 9 )
            t.insert(i);

        t.traverse();

        int k = 30;
        String out = (t.search(k) != null)?  "\nPresent" : "\nNot Present";
        System.out.println(out);

        for (int i = 100; i <= 200; i += 9 )
            t.remove(i);
        t.traverse();
    }
}
