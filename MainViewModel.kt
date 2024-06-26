package com.example.scrumble.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.scrumble.ui.data.AllOfWords

class MainViewModel : ViewModel() {

    private val _countOfWords: MutableLiveData<Int> = MutableLiveData()
    val countOfWords: LiveData<Int> = _countOfWords

    private val _score: MutableLiveData<Int> = MutableLiveData()
    val score: LiveData<Int> = _score

    private lateinit var currentWord: String

    private var _currentUnscrambledWord: MutableLiveData<String> = MutableLiveData<String>()
    val currentUnscrambledWord: LiveData<String> get() = _currentUnscrambledWord

    private var listOfWordsUsed: MutableList<String> = mutableListOf("")

    private val wordList = AllOfWords.list

    // Add a LiveData variable to indicate if the game is over
    private val _gameOver: MutableLiveData<Boolean> = MutableLiveData(false)
    val gameOver: LiveData<Boolean> = _gameOver

    init {
        _countOfWords.value = 0
        _score.value = 0
        _currentUnscrambledWord.value = ""
        getNewUnscrambledWord()
    }

    fun getNewUnscrambledWord() {
        currentWord = wordList.random()

        val charEdWord = currentWord.toCharArray()
        charEdWord.shuffle()

        while (currentWord == String(charEdWord)) {
            charEdWord.shuffle()
        }

        if (listOfWordsUsed.contains(currentWord)) {
            getNewUnscrambledWord()
        } else {
            _currentUnscrambledWord.value = String(charEdWord)
            listOfWordsUsed.add(currentWord)
            _countOfWords.value = _countOfWords.value!!.inc()
        }
    }

    fun areThereAnyWordsLeft(): Boolean {
        return AllOfWords.MAX_OF_NO_WORDS > countOfWords.value!!
    }

    fun isAnswerCorrect(answer: String): Boolean {
        var ans = answer
        if (answer.endsWith(" ") || answer.startsWith(" ")) {
            ans = answer.dropLastWhile { it.toString() == " " }
        }
        return if (currentWord.equals(ans, true)) {
            _score.value = _score.value!!.plus(AllOfWords.SCORE_INCREASE)
            if (areThereAnyWordsLeft()) {
                getNewUnscrambledWord()
            }
            true
        } else {
            false
        }
    }

    fun restartGame() {
        _countOfWords.value = 0
        _score.value = 0
        currentWord = ""
        _currentUnscrambledWord.value = ""
        listOfWordsUsed.clear()
        getNewUnscrambledWord()
    }

    // Modify onWordsFinished() to set gameOver to true
    fun onWordsFinished() {
        _gameOver.value = true
    }
}
