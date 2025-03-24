package com.example.fileservice;

public class Main {

    public static void main(String[] args) {
        String[] input = {"one4seveneight", "23four5six7", "2three45sixseven", "123"};
        int[] output = {1478, 234567, 234567, 123};
        Solution solution = new Solution();

        for (int i = 0; i < input.length; i++) {
            String s = input[i];
            int result = solution.solution(s);
            System.out.println(result);
        }
    }

}

class Solution {

    String[] word = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};

    public int solution(String s) {

        StringBuilder sb = new StringBuilder();
        String input = s;
        while(!input.isEmpty()) {

            char c = input.charAt(0);
            if (Character.isDigit(c)) {
                sb.append(c);
                input = input.substring(1);
                continue;
            }

            for (int i = 0; i < word.length; i++) {
                String w = word[i];

                if (input.startsWith(w)) {
                    sb.append(i);
                    input = input.substring(w.length());
                    break;
                }
            }
        }
        return Integer.parseInt(sb.toString());
    }

}