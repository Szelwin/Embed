package com.example.embed.data

/**
 * Represents a single quiz question.
 *
 * @param id           Unique identifier; used as the key in spaced-repetition state.
 * @param text         The question body. May include a code block formatted with
 *                     triple-backtick markers if [hasCodeBlock] is true.
 * @param options      Exactly four answer choices.
 * @param correctIndex Zero-based index into [options] pointing at the correct answer.
 * @param explanation  Brief explanation shown to the user after a wrong answer.
 * @param domain       Topic domain this question belongs to.
 * @param hasCodeBlock Whether [text] contains a fenced code block that the UI
 *                     should render with a monospace style.
 */
data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val domain: Domain,
    val hasCodeBlock: Boolean = false
) {
    init {
        require(options.size == 4) { "Question $id must have exactly 4 options" }
        require(correctIndex in 0..3) { "correctIndex must be 0–3 for question $id" }
        require(text.isNotBlank()) { "Question $id text must not be blank" }
        require(explanation.isNotBlank()) { "Question $id explanation must not be blank" }
        val longOptions = options.filter { it.length > 28 }
        require(longOptions.isEmpty()) {
            "Question $id has options exceeding 28 chars: $longOptions"
        }
    }
}