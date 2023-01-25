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
import com.example.notetaking.databinding.FragmentRegisterBinding
import com.example.notetaking.models.UserRequest
import com.example.notetaking.utills.TokenManager
import com.example.notetaking.utills.UiStatesForUserNetworkCalls
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()

    @Inject
  lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        if(tokenManager.getToken()!=null){
            findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener {
            val validationResult = validateUserRequest()
            if (validationResult.first) {
                authViewModel.registerUser(getUserRequest())
            } else {
                binding.txtError.text = validationResult.second
            }
        }

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }


        lifecycleScope.launchWhenStarted {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.userResponseStateFlow.collect { state ->
                    when (state) {
                        is UiStatesForUserNetworkCalls.Loading -> {
                            showProgressBar()

                            binding.txtError.text = ""
                        }
                        is UiStatesForUserNetworkCalls.Idle -> {
                            hideProgressBar()
                        }
                        is UiStatesForUserNetworkCalls.Success -> {
                            tokenManager.saveToken(state.userResponse.token)
                            hideProgressBar()
                            Toast.makeText(context,
                                "User Created Successfully! : ${state.userResponse.user.username}",
                                Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_registerFragment_to_mainFragment)

                        }
                        is UiStatesForUserNetworkCalls.Error -> {
                            hideProgressBar()
                            binding.txtError.text = state.message
                        }
                    }

                }
            }

        }


    }

    private fun validateUserRequest(): Pair<Boolean, String> {

        val userRequest = getUserRequest()

        return authViewModel.authenticateUserRequest(userRequest.username,
            userRequest.email,
            userRequest.password)
    }

    private fun getUserRequest(): UserRequest {
        val email = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()
        val username = binding.txtUsername.text.toString()

        return UserRequest(email, password, username)
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