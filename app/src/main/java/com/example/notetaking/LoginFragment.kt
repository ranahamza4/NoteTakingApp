package com.example.notetaking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.notetaking.databinding.FragmentLoginBinding
import com.example.notetaking.models.UserRequest
import com.example.notetaking.utills.TokenManager
import com.example.notetaking.utills.UiStatesForUserNetworkCalls
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {


    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AuthViewModel>()

    @Inject
     lateinit var tokenManager:TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnLogin.setOnClickListener {

            val validationResult = validateUserRequest()
            if (validationResult.first) {
                viewModel.loginUser(getUserRequest())
            } else {
                binding.txtError.text = validationResult.second
            }


        }

        lifecycleScope.launchWhenStarted {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userResponseStateFlow.collect { state ->
                    when (state) {
                        is UiStatesForUserNetworkCalls.Loading -> {
                            binding.txtError.text = ""
                            showProgressBar()
                        }
                        is UiStatesForUserNetworkCalls.Success -> {
                            tokenManager.saveToken(state.userResponse.token)
                            hideProgressBar()
                            Toast.makeText(context,
                                "Welcome ${state.userResponse.user.username} !!",
                                Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                        }
                        is UiStatesForUserNetworkCalls.Error -> {
                            hideProgressBar()
                            binding.txtError.text = state.message
                        }
                        is UiStatesForUserNetworkCalls.Idle -> {
                            hideProgressBar()
                            binding.txtError.text = ""
                        }
                    }

                }
            }
        }

    }

    private fun validateUserRequest(): Pair<Boolean, String> {

        val userRequest = getUserRequest()

        return viewModel.authenticateUserRequest(userRequest.username,
            userRequest.email,
            userRequest.password)
    }

    private fun getUserRequest(): UserRequest {
        val email = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()

        return UserRequest(email, password, "notnull")
    }


    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}