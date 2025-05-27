package com.fakeco.security.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Base Fragment class with ViewBinding support.
 * @param VB The type of ViewBinding.
 */
abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    /**
     * Abstract function to inflate the ViewBinding.
     * @param inflater The LayoutInflater to use.
     * @param container The parent view.
     * @param attachToParent Whether to attach to the parent.
     * @return The inflated ViewBinding.
     */
    abstract fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateBinding(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
    }

    /**
     * Setup UI components. Override this in subclasses.
     */
    protected open fun setupUI() {}

    /**
     * Observe ViewModel state. Override this in subclasses.
     */
    protected open fun observeViewModel() {}

    /**
     * Collect flow when the view is in STARTED state.
     * @param flow The flow to collect.
     * @param collector The collector function.
     */
    protected fun <T> collectFlow(flow: Flow<T>, collector: suspend (T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect(collector)
            }
        }
    }

    /**
     * Show a toast message.
     * @param message The message to show.
     * @param duration The duration of the toast.
     */
    protected fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(requireContext(), message, duration).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}