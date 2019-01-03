package com.assessment;


import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class Assessment {

	private List<String> sentences;

	public Assessment() {
		sentences = new ArrayList<>();
	}

	/**
	 * To count no of words.
	 *
	 * @param words List of words.
	 * @return count of words.
	 */
	private int countTotalWords(List<String> words) {
		return words.size();
	}

	/**
	 * To read a file and add words to a list.
	 *
	 * @param pathname Filename.
	 * @return list of words.
	 * @throws IOException
	 */
	private List<String> readTextFileToWordList(String pathname) throws IOException {
		List<String> words = new ArrayList<>();
		FileInputStream fstream = new FileInputStream(pathname);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String strLine;
		while ((strLine = br.readLine()) != null) {
			if (strLine.equals(""))
				continue;
			String[] splitWords = strLine.split("\\s+");
			sentences.add(strLine);
			words.addAll(Arrays.asList(splitWords));
		}
		br.close();
		fstream.close();
		return words;
	}


	/**
	 * To get Top 10 used words.
	 *
	 * @param words List of words.
	 * @return Top 10 words in a list.
	 */
	private List<String> getTopTenWords(List<String> words) {
		Map<String, Integer> frequencyMap = words.stream()
				.collect(toMap(
						s -> s,
						s -> 1,
						Integer::sum));
		return words.stream()
				.sorted(comparing(frequencyMap::get).reversed())
				.distinct()
				.limit(10)
				.collect(toList());
	}


	/**
	 * To get sentences.
	 *
	 * @return List of sentences.
	 */
	private List<String> getSentences() {
		return this.sentences;
	}

	/**
	 * To get Sentence of most used words.
	 *
	 * @param sentenceInJson Map.
	 * @return Sentence in string.
	 */
	private String getSentenceOfMaximumWords(Map<Integer, Map<String, Object>> sentenceInJson) {
		return sentenceInJson
				.entrySet()
				.stream()
				.max(Comparator.comparing(dt -> (Long) (dt.getValue().get("count"))))
				.get()
				.getValue().get("sentence").toString();
	}

	/**
	 * To get a sentence of most used words in a file.
	 *
	 * @param data File data.
	 * @return Sentence.
	 */
	private Map<Integer, Map<String, Object>> getSentenceOfMostUsedWords(List<String> data, List<String> topWords) {
		Map<Integer, Map<String, Object>> jsonObject = new HashMap<>();
		AtomicInteger counter = new AtomicInteger(0);
		data.stream()
				.forEach(sentence -> {
					Map<String, Object> dataObject = new HashMap<>();
					dataObject.put("count", Arrays
							.asList(sentence.split("\\s+"))
							.stream()
							.map(word -> topWords.contains(word))
							.filter(flag -> flag)
							.count());
					dataObject.put("sentence", sentence);
					jsonObject.put(counter.incrementAndGet(), dataObject);
				});
		return jsonObject;
	}

	public static void main(String[] args) throws Exception {
		Assessment assessment = new Assessment();

		List<String> strings = assessment.readTextFileToWordList("passage.txt");
		System.out.println("1. Total Words :: " + assessment.countTotalWords(strings));
		List<String> topTenWords = assessment.getTopTenWords(strings);
		System.out.println("2. Top Ten Words :: " + topTenWords);
		Map<Integer, Map<String, Object>> sentenceOfMostUsedWords =
				assessment.getSentenceOfMostUsedWords(assessment.getSentences(), topTenWords);
		System.out.println("3. Sentence with most words :: " + assessment.getSentenceOfMaximumWords(sentenceOfMostUsedWords));
	}
}
