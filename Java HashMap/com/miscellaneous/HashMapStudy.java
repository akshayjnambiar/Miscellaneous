package com.miscellaneous;

import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings("all")
public class HashMapStudy {

    static class Node {
        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public String toString() {
            return "1";
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }

    public static void main(String[] args) throws Exception{
        Map<Node, Integer> map = new HashMap<>();

        List<Node> keys = new ArrayList<>();

        for(int i =0; i<11;i++) {
            keys.add(new Node());
            map.put(keys.get(i), i);
            printMap(map);
        }


        keys.stream().map(map::get).forEach(System.out::println);

    }

    private static <K,V> void printMap(Map<K,V> map) throws Exception {
        Class clazz = map.getClass();
        Field table = clazz.getDeclaredField("table");
        table.setAccessible(true);
        Map.Entry<K, V>[] realTable = (Map.Entry<K, V>[]) table.get(map);

        //Iterate and do pretty printing
        System.out.println("######## START ########");
        System.out.println(" table length = " + realTable.length);

        for (int i = 0; i < realTable.length; i++) {
            if(realTable[i] != null) System.out.println(String.format("Bucket : %d, Entry: %s", i, bucketToString(realTable[i])));
        }
        System.out.println("######## END ########");
    }

    private static <K,V> String bucketToString(Map.Entry<K, V> entry) throws Exception{
        if (entry == null) return null;
        StringBuilder sb = new StringBuilder();

        //Access to the "next" filed of HashMap$Node
        Class clazz = entry.getClass();
        Field next = null;
        Field left = null;
        Field right = null;
        Field parent = null;
        Field prev = null;

        try {
            next = clazz.getDeclaredField("next");
        }catch (Exception e) {

            left = clazz.getDeclaredField("left");
            right = clazz.getDeclaredField("right");
            parent = clazz.getDeclaredField("parent");
            prev = clazz.getDeclaredField("prev");
        }
        if(next!= null) next.setAccessible(true);
        if(left!= null) left.setAccessible(true);
        if(right!= null) right.setAccessible(true);
        if(parent!= null) parent.setAccessible(true);
        if(prev!= null) prev.setAccessible(true);

        //going through the bucket
        if(next != null) {
            while (entry != null) {
                sb.append(entry.getValue());
                entry = (Map.Entry<K, V>) next.get(entry);
                if (null != entry) sb.append(" -> ");
            }
        } else if(left !=null && right!=null && parent!=null && prev!=null){
            sb.append("\n");
            Queue<Map.Entry<K, V>> q = new LinkedList<>();
            Map<K, V> mapt = new HashMap<>();
            mapt.put((K)new Node(), (V)Integer.valueOf(-1));
            q.add(entry);
            q.add(mapt.entrySet().stream().findFirst().get());

            while(!q.isEmpty() && q.size()>1) {
                Map.Entry<K,V> e1 = q.poll();
                while(!e1.getValue().equals(-1)) {
                    sb.append(e1.getValue());
                    sb.append(",");
                    if(parent.get(e1)!= null )sb.append(((Map.Entry<K, V>)parent.get(e1)).getValue());
                    sb.append(",");
                    if(prev.get(e1)!= null ) sb.append(((Map.Entry<K, V>)prev.get(e1)).getValue());
                    sb.append(" ");
                    if(left.get(e1) != null) q.add((Map.Entry<K, V>)left.get(e1));
                    if(right.get(e1) != null) q.add((Map.Entry<K, V>)right.get(e1));

                    e1 = q.poll();
                }
                q.add(mapt.entrySet().stream().findFirst().get());
                sb.append("\n");
            }
        }else {
            throw new IllegalStateException("Neither bin nor Tree");
        }
        return sb.toString();
    }


}
