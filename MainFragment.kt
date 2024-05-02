package com.example.scrumble

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.scrumble.R
import com.example.scrumble.databinding.FragmentMainBinding
import com.example.scrumble.ui.MainViewModel
import com.example.scrumble.ui.data.AllOfWords
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeVariables()
        observeGameOver()
    }

    private fun observeVariables() {
        binding.mainViewModel = viewModel
        binding.maxNoOfWords = AllOfWords.MAX_OF_NO_WORDS

        viewModel.score.observe(viewLifecycleOwner) { changedInt ->
            changedInt?.let {
                binding.score = it
            }
        }

        viewModel.countOfWords.observe(viewLifecycleOwner) { changedCount ->
            changedCount?.let {
                binding.wordsCount = it
            }
        }

        viewModel.currentUnscrambledWord.observe(viewLifecycleOwner) { onChangedWord ->
            onChangedWord?.let {
                binding.unscrambledWord = it
            }
        }

        binding.submitButton.setOnClickListener { submitWord() }
        binding.skipButton.setOnClickListener { skipWord() }
    }

    private fun observeGameOver() {
        viewModel.gameOver.observe(viewLifecycleOwner) { isGameOver ->
            if (isGameOver) {
                showGameOverDialog()
            }
        }
    }

    private fun submitWord() {
        val answer = binding.editTextFieldForInput.text.toString()
        binding.editTextFieldForInput.setText("")

        if (viewModel.isAnswerCorrect(answer)) {
            setErrorAccordingToWhetherAnswerIsCorrectOrNot(false)
            if (viewModel.areThereAnyWordsLeft()) {
                viewModel.getNewUnscrambledWord()
            } else {
                onWordsFinished()
            }
        } else {
            setErrorAccordingToWhetherAnswerIsCorrectOrNot(true)
        }
    }

    private fun skipWord() {
        if (viewModel.areThereAnyWordsLeft()) {
            viewModel.getNewUnscrambledWord()
        } else {
            onWordsFinished()
        }
    }

    private fun setErrorAccordingToWhetherAnswerIsCorrectOrNot(error: Boolean) {
        if (error) {
            binding.textInputLayout.error = getString(R.string.set_error_false)
            binding.textInputLayout.isErrorEnabled = true
        } else {
            binding.textInputLayout.error = null
            binding.textInputLayout.isErrorEnabled = false
        }
    }

    private fun onWordsFinished() {
        viewModel.onWordsFinished() // Update the ViewModel to trigger gameOver
    }

    private fun showGameOverDialog() {
        alertDialog(
            getString(R.string.game_over),
            getString(R.string.you_scored_message, viewModel.score.value ?: 0),
            getString(R.string.restart_game),
            getString(R.string.exit_game)
        )
    }

    private fun alertDialog(title: String, message: String, positiveOption: String, negativeOption: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveOption) { _, _ ->
                restartGame()
            }.setNegativeButton(negativeOption) { _, _ ->
                exitGame()
            }.show()
    }

    private fun restartGame() {
        viewModel.restartGame()
    }

    private fun exitGame() {
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
