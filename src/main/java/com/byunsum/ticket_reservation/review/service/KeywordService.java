package com.byunsum.ticket_reservation.review.service;

import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import scala.collection.Seq;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeywordService {
    public List<String> extractTopNouns(List<String> reviews, int minLength, int keywordLimit) {
        Map<String, Integer> freqMap = new HashMap<>();

        for(String text : reviews){
            // null 또는 공백 문자열은 건너뜀
            if(text == null || text.trim().isEmpty()) {
                continue;
            }

            CharSequence normalized = OpenKoreanTextProcessorJava.normalize(text);
            Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);
            List<KoreanTokenJava> tokenList = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);

            tokenList.stream()
                    .filter(token -> token.getPos().name().equals("Noun"))
                    .map(KoreanTokenJava::getText)
                    .filter(word -> word.length() >= minLength)
                    .forEach(word -> freqMap.put(word, freqMap.getOrDefault(word,0)+1));
        }

        return freqMap.entrySet().stream()
                .sorted((a,b) -> b.getValue() - a.getValue())
                .limit(keywordLimit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
