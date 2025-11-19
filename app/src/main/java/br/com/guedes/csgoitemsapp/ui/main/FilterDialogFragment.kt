package br.com.guedes.csgoitemsapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import br.com.guedes.csgoitemsapp.R

class FilterDialogFragment(
    private val options: List<String>,
    private val onFilterSelected: (String) -> Unit,
    private val onClearFilter: () -> Unit
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_filter, container, false)
        val radioGroup = view.findViewById<RadioGroup>(R.id.filter_options_group)
        val clearButton = view.findViewById<Button>(R.id.clear_filter_button)

        options.forEach { option ->
            val radioButton = RadioButton(context).apply {
                text = option
                setOnClickListener {
                    onFilterSelected(option)
                    dismiss()
                }
            }
            radioGroup.addView(radioButton)
        }

        clearButton.setOnClickListener {
            onClearFilter()
            dismiss()
        }

        return view
    }
}
