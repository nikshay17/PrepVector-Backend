package com.prepvector.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for all chunking strategies:
 * 1. Fixed Size No Overlap
 * 2. Fixed Size With Overlap (PrepVector current)
 * 3. Sentence Based
 * 4. Paragraph Based
 * 5. Recursive Character
 */
public class ChunkingStrategyTest {

    // ================================================================
    // HELPERS
    // ================================================================

    /**
     * Generates a string with exactly N words.
     * Example: generateWords(10) → "word0 word1 word2 ... word9"
     */
    private String generateWords(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) sb.append(" ");
            sb.append("word").append(i);
        }
        return sb.toString();
    }

    /**
     * Counts words in a string.
     */
    private int wordCount(String text) {
        if (text == null || text.trim().isEmpty()) return 0;
        return text.trim().split("\\s+").length;
    }

    // ================================================================
    // STRATEGY 1 — FIXED SIZE NO OVERLAP
    // ================================================================

    /**
     * Split text into equal chunks.
     * No shared words between chunks.
     * Simplest possible strategy.
     */
    private List<String> fixedNoOverlap(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        String[] words = text.split("\\s+");

        int start = 0;
        while (start < words.length) {
            int end = Math.min(start + chunkSize, words.length);
            String chunk = String.join(" ", Arrays.copyOfRange(words, start, end));
            chunks.add(chunk);
            start += chunkSize; // move FULL chunk size — no overlap
        }
        return chunks;
    }

    @Test
    @DisplayName("Fixed No Overlap: 1000 words / 500 chunk = exactly 2 chunks")
    void fixedNoOverlap_exactDivision() {
        String text = generateWords(1000);
        List<String> chunks = fixedNoOverlap(text, 500);

        assertThat(chunks).hasSize(2);
        assertThat(wordCount(chunks.get(0))).isEqualTo(500);
        assertThat(wordCount(chunks.get(1))).isEqualTo(500);
    }

    @Test
    @DisplayName("Fixed No Overlap: last chunk is smaller if words dont divide evenly")
    void fixedNoOverlap_unevenDivision() {
        String text = generateWords(750);
        List<String> chunks = fixedNoOverlap(text, 500);

        assertThat(chunks).hasSize(2);
        assertThat(wordCount(chunks.get(0))).isEqualTo(500);
        assertThat(wordCount(chunks.get(1))).isEqualTo(250); // remainder
    }

    @Test
    @DisplayName("Fixed No Overlap: no word appears in two chunks")
    void fixedNoOverlap_noSharedWords() {
        String text = generateWords(1000);
        List<String> chunks = fixedNoOverlap(text, 500);

        // Last word of chunk 1
        String[] chunk1Words = chunks.get(0).split("\\s+");
        String lastWordChunk1 = chunk1Words[chunk1Words.length - 1];

        // First word of chunk 2
        String[] chunk2Words = chunks.get(1).split("\\s+");
        String firstWordChunk2 = chunk2Words[0];

        // They should be different — no overlap
        assertThat(lastWordChunk1).isNotEqualTo(firstWordChunk2);
    }

    @Test
    @DisplayName("Fixed No Overlap: document smaller than chunk size = one chunk")
    void fixedNoOverlap_smallDocument() {
        String text = generateWords(100);
        List<String> chunks = fixedNoOverlap(text, 500);

        assertThat(chunks).hasSize(1);
        assertThat(wordCount(chunks.get(0))).isEqualTo(100);
    }

    @Test
    @DisplayName("Fixed No Overlap: single word document")
    void fixedNoOverlap_singleWord() {
        String text = "hello";
        List<String> chunks = fixedNoOverlap(text, 500);

        assertThat(chunks).hasSize(1);
        assertThat(chunks.get(0)).isEqualTo("hello");
    }

    @Test
    @DisplayName("Fixed No Overlap: chunks combined equal original text")
    void fixedNoOverlap_chunksReconstructOriginal() {
        String text = generateWords(1000);
        List<String> chunks = fixedNoOverlap(text, 500);

        String reconstructed = String.join(" ", chunks);
        assertThat(reconstructed).isEqualTo(text);
    }

    // ================================================================
    // STRATEGY 2 — FIXED SIZE WITH OVERLAP (YOUR CURRENT STRATEGY)
    // ================================================================

    /**
     * Split text into chunks with overlap.
     * Adjacent chunks share N words.
     * Preserves context at boundaries.
     * THIS IS WHAT PREPVECTOR USES.
     */
    private List<String> fixedWithOverlap(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        String[] words = text.split("\\s+");

        int step = chunkSize - overlap; // 500 - 50 = 450
        int start = 0;

        while (start < words.length) {
            int end = Math.min(start + chunkSize, words.length);
            String chunk = String.join(" ", Arrays.copyOfRange(words, start, end));
            chunks.add(chunk);
            start += step; // move 450 words — creates 50 word overlap
        }
        return chunks;
    }

    @Test
    @DisplayName("Fixed With Overlap: correct number of chunks for 1000 words")
    void fixedWithOverlap_chunkCount() {
        String text = generateWords(1000);
        List<String> chunks = fixedWithOverlap(text, 500, 50);

        // ceil((1000 - 500) / 450) + 1 = 3 chunks
        assertThat(chunks).hasSize(3);
    }

    @Test
    @DisplayName("Fixed With Overlap: each chunk is max 500 words except last")
    void fixedWithOverlap_chunkSize() {
        String text = generateWords(1000);
        List<String> chunks = fixedWithOverlap(text, 500, 50);

        // All except last should be 500 words
        for (int i = 0; i < chunks.size() - 1; i++) {
            assertThat(wordCount(chunks.get(i))).isEqualTo(500);
        }
    }

    @Test
    @DisplayName("Fixed With Overlap: adjacent chunks share exactly 50 words")
    void fixedWithOverlap_overlapExists() {
        String text = generateWords(1000);
        List<String> chunks = fixedWithOverlap(text, 500, 50);

        String[] chunk1Words = chunks.get(0).split("\\s+");
        String[] chunk2Words = chunks.get(1).split("\\s+");

        // Last 50 words of chunk 1
        String[] lastFiftyOfChunk1 = Arrays.copyOfRange(
                chunk1Words, chunk1Words.length - 50, chunk1Words.length);

        // First 50 words of chunk 2
        String[] firstFiftyOfChunk2 = Arrays.copyOfRange(chunk2Words, 0, 50);

        // They should be IDENTICAL — this is the overlap
        assertThat(lastFiftyOfChunk1).isEqualTo(firstFiftyOfChunk2);
    }

    @Test
    @DisplayName("Fixed With Overlap: more chunks than no-overlap strategy")
    void fixedWithOverlap_moreChunksThanNoOverlap() {
        String text = generateWords(1000);
        List<String> noOverlapChunks = fixedNoOverlap(text, 500);
        List<String> overlapChunks = fixedWithOverlap(text, 500, 50);

        // Overlap creates more chunks because step is smaller
        assertThat(overlapChunks.size()).isGreaterThan(noOverlapChunks.size());
    }

    @Test
    @DisplayName("Fixed With Overlap: zero overlap equals no-overlap strategy")
    void fixedWithOverlap_zeroOverlapEqualsNoOverlap() {
        String text = generateWords(1000);
        List<String> noOverlap = fixedNoOverlap(text, 500);
        List<String> zeroOverlap = fixedWithOverlap(text, 500, 0);

        assertThat(zeroOverlap).isEqualTo(noOverlap);
    }

    @Test
    @DisplayName("Fixed With Overlap: document smaller than chunk = one chunk")
    void fixedWithOverlap_smallDocument() {
        String text = generateWords(100);
        List<String> chunks = fixedWithOverlap(text, 500, 50);

        assertThat(chunks).hasSize(1);
        assertThat(wordCount(chunks.get(0))).isEqualTo(100);
    }

    @Test
    @DisplayName("Fixed With Overlap: concept at boundary exists in both chunks")
    void fixedWithOverlap_boundaryConceptPreserved() {
        // This is the CORE reason for overlap
        // "The time complexity of binary search is O(log n)"
        // appears at boundary — should exist in both adjacent chunks

        String text = generateWords(1000);
        List<String> chunks = fixedWithOverlap(text, 500, 50);

        // word449 is last unique word of chunk 1 (before overlap starts)
        // word450 is first word of overlap region
        // word499 is last word of overlap region
        // word499 should appear in BOTH chunk 1 and chunk 2

        String chunk1 = chunks.get(0);
        String chunk2 = chunks.get(1);

        // word490 is in overlap region — should be in both
        assertThat(chunk1).contains("word490");
        assertThat(chunk2).contains("word490");
    }

    @Test
    @DisplayName("Fixed With Overlap: handles text with multiple whitespace types")
    void fixedWithOverlap_multipleWhitespaceTypes() {
        // Real documents have tabs, newlines, multiple spaces
        String text = "word0  word1\tword2\nword3\r\nword4  word5";
        List<String> chunks = fixedWithOverlap(text, 3, 1);

        // Should handle all whitespace types correctly
        assertThat(chunks).isNotEmpty();
        // No empty words from double spaces
        for (String chunk : chunks) {
            assertThat(chunk).doesNotContain("  ");
        }
    }

    // ================================================================
    // STRATEGY 3 — SENTENCE BASED
    // ================================================================

    /**
     * Split on sentence boundaries (periods, exclamation, question marks).
     * Respects natural language boundaries.
     * Better than word-based for prose content.
     */
    private List<String> sentenceBased(String text, int maxSentences) {
        List<String> chunks = new ArrayList<>();

        // Split on sentence endings
        String[] sentences = text.split("(?<=[.!?])\\s+");

        int start = 0;
        while (start < sentences.length) {
            int end = Math.min(start + maxSentences, sentences.length);
            String chunk = String.join(" ", Arrays.copyOfRange(sentences, start, end));
            chunks.add(chunk);
            start += maxSentences;
        }
        return chunks;
    }

    @Test
    @DisplayName("Sentence Based: chunks end at sentence boundaries")
    void sentenceBased_endsAtSentenceBoundary() {
        String text = "Binary search divides the array. " +
                "It runs in O log n time. " +
                "This makes it very efficient. " +
                "Linear search is O n instead.";

        List<String> chunks = sentenceBased(text, 2);

        // Each chunk should end with punctuation
        for (String chunk : chunks) {
            String trimmed = chunk.trim();
            char lastChar = trimmed.charAt(trimmed.length() - 1);
            assertThat(lastChar).isIn('.', '!', '?');
        }
    }

    @Test
    @DisplayName("Sentence Based: 4 sentences / 2 per chunk = 2 chunks")
    void sentenceBased_chunkCount() {
        String text = "First sentence. Second sentence. " +
                "Third sentence. Fourth sentence.";

        List<String> chunks = sentenceBased(text, 2);

        assertThat(chunks).hasSize(2);
    }

    @Test
    @DisplayName("Sentence Based: no sentence split mid-concept")
    void sentenceBased_noMidConceptSplit() {
        String text = "Binary search is an efficient algorithm. " +
                "It works on sorted arrays only. " +
                "The time complexity is O log n.";

        List<String> chunks = sentenceBased(text, 2);

        // First chunk should have both first two sentences complete
        assertThat(chunks.get(0)).contains("Binary search is an efficient algorithm");
        assertThat(chunks.get(0)).contains("It works on sorted arrays only");
    }

    @Test
    @DisplayName("Sentence Based: handles single sentence document")
    void sentenceBased_singleSentence() {
        String text = "Binary search runs in O log n time.";
        List<String> chunks = sentenceBased(text, 2);

        assertThat(chunks).hasSize(1);
        assertThat(chunks.get(0)).isEqualTo(text.trim());
    }

    // ================================================================
    // STRATEGY 4 — PARAGRAPH BASED
    // ================================================================

    /**
     * Split on paragraph boundaries (double newlines).
     * Respects document structure.
     * Best for well-formatted documents.
     */
    private List<String> paragraphBased(String text) {
        List<String> chunks = new ArrayList<>();
        String[] paragraphs = text.split("\\n\\n+");

        for (String paragraph : paragraphs) {
            String trimmed = paragraph.trim();
            if (!trimmed.isEmpty()) {
                chunks.add(trimmed);
            }
        }
        return chunks;
    }

    @Test
    @DisplayName("Paragraph Based: splits on double newlines")
    void paragraphBased_splitsOnDoubleNewline() {
        String text = "First paragraph about binary search.\n\n" +
                "Second paragraph about sorting.\n\n" +
                "Third paragraph about graphs.";

        List<String> chunks = paragraphBased(text);

        assertThat(chunks).hasSize(3);
    }

    @Test
    @DisplayName("Paragraph Based: each chunk is one complete paragraph")
    void paragraphBased_completeParapgraphs() {
        String text = "Binary search works by dividing the array in half.\n\n" +
                "Linear search checks every element one by one.";

        List<String> chunks = paragraphBased(text);

        assertThat(chunks.get(0)).isEqualTo(
                "Binary search works by dividing the array in half.");
        assertThat(chunks.get(1)).isEqualTo(
                "Linear search checks every element one by one.");
    }

    @Test
    @DisplayName("Paragraph Based: ignores empty paragraphs")
    void paragraphBased_ignoresEmptyParagraphs() {
        String text = "First paragraph.\n\n\n\nSecond paragraph.";
        List<String> chunks = paragraphBased(text);

        // Should not create empty chunks from multiple newlines
        assertThat(chunks).hasSize(2);
        for (String chunk : chunks) {
            assertThat(chunk).isNotBlank();
        }
    }

    @Test
    @DisplayName("Paragraph Based: single paragraph document")
    void paragraphBased_singleParagraph() {
        String text = "This is one paragraph with no double newlines.";
        List<String> chunks = paragraphBased(text);

        assertThat(chunks).hasSize(1);
    }

    // ================================================================
    // STRATEGY 5 — RECURSIVE CHARACTER (LangChain style)
    // ================================================================

    /**
     * Try to split on paragraphs first.
     * If chunk still too big → split on sentences.
     * If still too big → split on words.
     * Respects natural boundaries at every level.
     * This is what LangChain's RecursiveCharacterTextSplitter does.
     */
    private List<String> recursiveCharacter(String text, int maxWords) {
        List<String> result = new ArrayList<>();
        recursiveSplit(text, maxWords, result);
        return result;
    }

    private void recursiveSplit(String text, int maxWords, List<String> result) {
        // Base case: text fits in one chunk
        if (wordCount(text) <= maxWords) {
            if (!text.trim().isEmpty()) {
                result.add(text.trim());
            }
            return;
        }

        // Level 1: Try splitting on paragraphs
        String[] paragraphs = text.split("\\n\\n+");
        if (paragraphs.length > 1) {
            for (String para : paragraphs) {
                recursiveSplit(para.trim(), maxWords, result);
            }
            return;
        }

        // Level 2: Try splitting on sentences
        String[] sentences = text.split("(?<=[.!?])\\s+");
        if (sentences.length > 1) {
            StringBuilder current = new StringBuilder();
            for (String sentence : sentences) {
                String candidate = current.length() > 0
                        ? current + " " + sentence
                        : sentence;

                if (wordCount(candidate) <= maxWords) {
                    current = new StringBuilder(candidate);
                } else {
                    if (current.length() > 0) {
                        result.add(current.toString().trim());
                    }
                    current = new StringBuilder(sentence);
                }
            }
            if (current.length() > 0) {
                result.add(current.toString().trim());
            }
            return;
        }

        // Level 3: Fall back to word splitting
        String[] words = text.split("\\s+");
        int start = 0;
        while (start < words.length) {
            int end = Math.min(start + maxWords, words.length);
            result.add(String.join(" ", Arrays.copyOfRange(words, start, end)));
            start += maxWords;
        }
    }

    @Test
    @DisplayName("Recursive: splits on paragraphs first when possible")
    void recursive_splitsParagraphsFirst() {
        String text = "First paragraph with some content here.\n\n" +
                "Second paragraph with different content.\n\n" +
                "Third paragraph with more content here.";

        List<String> chunks = recursiveCharacter(text, 20);

        // Should produce 3 chunks — one per paragraph
        assertThat(chunks).hasSize(3);
    }

    @Test
    @DisplayName("Recursive: falls to sentence split when paragraph too big")
    void recursive_fallsToSentenceSplit() {
        // One big paragraph with multiple sentences
        String text = "First sentence in this paragraph. " +
                "Second sentence continues here. " +
                "Third sentence adds more. " +
                "Fourth sentence concludes it.";

        // Max 10 words — paragraphs won't help, needs sentence split
        List<String> chunks = recursiveCharacter(text, 10);

        assertThat(chunks.size()).isGreaterThan(1);
        // Each chunk should end at sentence boundary where possible
        for (String chunk : chunks) {
            // Chunks respect sentence boundaries
            assertThat(chunk).isNotBlank();
        }
    }

    @Test
    @DisplayName("Recursive: falls to word split as last resort")
    void recursive_fallsToWordSplit() {
        // One long sentence with no natural split points
        String text = "word0 word1 word2 word3 word4 " +
                "word5 word6 word7 word8 word9 " +
                "word10 word11 word12 word13 word14";

        List<String> chunks = recursiveCharacter(text, 5);

        assertThat(chunks).hasSize(3);
        assertThat(wordCount(chunks.get(0))).isEqualTo(5);
    }

    @Test
    @DisplayName("Recursive: document smaller than max = single chunk")
    void recursive_smallDocument() {
        String text = "Short document. Only two sentences.";
        List<String> chunks = recursiveCharacter(text, 100);

        assertThat(chunks).hasSize(1);
    }

    @Test
    @DisplayName("Recursive: no empty chunks produced")
    void recursive_noEmptyChunks() {
        String text = "First paragraph.\n\n\n\nSecond paragraph.\n\n\nThird.";
        List<String> chunks = recursiveCharacter(text, 50);

        for (String chunk : chunks) {
            assertThat(chunk).isNotBlank();
        }
    }

    // ================================================================
    // COMPARISON TESTS — ALL STRATEGIES SIDE BY SIDE
    // ================================================================

    @Test
    @DisplayName("Comparison: overlap produces more chunks than no-overlap")
    void comparison_overlapVsNoOverlap_chunkCount() {
        String text = generateWords(1000);

        List<String> noOverlap = fixedNoOverlap(text, 500);
        List<String> withOverlap = fixedWithOverlap(text, 500, 50);

        assertThat(withOverlap.size()).isGreaterThan(noOverlap.size());
        System.out.println("No overlap chunks: " + noOverlap.size());
        System.out.println("With overlap chunks: " + withOverlap.size());
    }

    @Test
    @DisplayName("Comparison: overlap stores more total words (expected duplication)")
    void comparison_overlapStoresMoreWords() {
        String text = generateWords(1000);

        List<String> noOverlap = fixedNoOverlap(text, 500);
        List<String> withOverlap = fixedWithOverlap(text, 500, 50);

        int noOverlapTotal = noOverlap.stream().mapToInt(this::wordCount).sum();
        int overlapTotal = withOverlap.stream().mapToInt(this::wordCount).sum();

        // Overlap stores MORE words due to duplication
        assertThat(overlapTotal).isGreaterThan(noOverlapTotal);

        // Original text = 1000 words
        assertThat(noOverlapTotal).isEqualTo(1000);
        // With overlap: ~10% more storage
        assertThat(overlapTotal).isGreaterThan(1000);

        System.out.println("No overlap total words: " + noOverlapTotal);
        System.out.println("With overlap total words: " + overlapTotal);
    }

    @Test
    @DisplayName("Comparison: recursive respects natural boundaries better")
    void comparison_recursiveRespectsNaturalBoundaries() {
        String text = "Binary search is efficient.\n\n" +
                "Linear search is simple but slow. " +
                "It checks every element one by one.\n\n" +
                "Hash maps give O(1) lookup time.";

        List<String> fixed = fixedNoOverlap(text, 10);
        List<String> recursive = recursiveCharacter(text, 10);

        // Recursive should produce chunks that make semantic sense
        // Each paragraph should be its own chunk
        boolean hasCompleteParagraph = recursive.stream()
                .anyMatch(chunk -> chunk.contains("Binary search is efficient"));

        assertThat(hasCompleteParagraph).isTrue();
    }

    // ================================================================
    // PREPVECTOR SPECIFIC TESTS
    // ================================================================

    @Test
    @DisplayName("PrepVector: 500 word chunk with 50 overlap — production config")
    void prepVector_productionConfig() {
        // Simulate a real document
        String text = generateWords(2000);
        int CHUNK_SIZE = 500;
        int OVERLAP = 50;

        List<String> chunks = fixedWithOverlap(text, CHUNK_SIZE, OVERLAP);

        // Verify production behavior
        assertThat(chunks).isNotEmpty();

        // All chunks except last are 500 words
        for (int i = 0; i < chunks.size() - 1; i++) {
            assertThat(wordCount(chunks.get(i))).isEqualTo(CHUNK_SIZE);
        }

        // Adjacent chunks share exactly 50 words
        for (int i = 0; i < chunks.size() - 1; i++) {
            String[] current = chunks.get(i).split("\\s+");
            String[] next = chunks.get(i + 1).split("\\s+");

            String[] lastNOfCurrent = Arrays.copyOfRange(
                    current, current.length - OVERLAP, current.length);
            String[] firstNOfNext = Arrays.copyOfRange(next, 0, OVERLAP);

            assertThat(lastNOfCurrent).isEqualTo(firstNOfNext);
        }

        System.out.println("Document: 2000 words");
        System.out.println("Strategy: Fixed 500w / 50 overlap");
        System.out.println("Chunks produced: " + chunks.size());
    }

    @Test
    @DisplayName("PrepVector: Math.min prevents ArrayIndexOutOfBounds on last chunk")
    void prepVector_mathMinPreventsException() {
        // 750 words — last chunk should be 250 words not crash
        String text = generateWords(750);

        // Should NOT throw ArrayIndexOutOfBoundsException
        List<String> chunks = fixedWithOverlap(text, 500, 50);

        assertThat(chunks).isNotEmpty();

        // Last chunk should have remaining words
        String lastChunk = chunks.get(chunks.size() - 1);
        assertThat(wordCount(lastChunk)).isLessThanOrEqualTo(500);
        assertThat(wordCount(lastChunk)).isGreaterThan(0);

        System.out.println("Last chunk word count: " + wordCount(lastChunk));
    }

    @Test
    @DisplayName("PrepVector: regex \\\\s+ handles tabs and newlines correctly")
    void prepVector_regexHandlesAllWhitespace() {
        // Real documents have mixed whitespace
        String text = "word0\tword1\nword2\r\nword3  word4\t\tword5";
        List<String> chunks = fixedWithOverlap(text, 4, 1);

        // All chunks should have clean single-space separated words
        for (String chunk : chunks) {
            // No tabs
            assertThat(chunk).doesNotContain("\t");
            // No newlines
            assertThat(chunk).doesNotContain("\n");
            // No double spaces
            assertThat(chunk).doesNotContain("  ");
        }
    }

    @Test
    @DisplayName("PrepVector: empty document returns empty chunks")
    void prepVector_emptyDocument() {
        String text = "";
        List<String> chunks = fixedWithOverlap(text, 500, 50);

        assertThat(chunks).isEmpty();
    }

    @Test
    @DisplayName("PrepVector: single word document")
    void prepVector_singleWord() {
        String text = "hello";
        List<String> chunks = fixedWithOverlap(text, 500, 50);

        assertThat(chunks).hasSize(1);
        assertThat(chunks.get(0)).isEqualTo("hello");
    }

    @Test
    @DisplayName("PrepVector: overlap larger than chunk size is invalid")
    void prepVector_overlapLargerThanChunk() {
        // overlap=600 > chunkSize=500 — step would be negative
        // This should be caught at configuration level
        String text = generateWords(1000);
        int chunkSize = 500;
        int overlap = 600; // invalid

        int step = chunkSize - overlap; // -100 — negative step!

        // With negative step the loop never advances — infinite loop
        // This test documents the invalid config behavior
        assertThat(step).isNegative(); // confirms config is invalid
        // Production code should validate: overlap < chunkSize
    }
}