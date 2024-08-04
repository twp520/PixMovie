package com.flight.movie.infra.master.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.databinding.FragmentItemListDialogListDialogBinding
import com.flight.movie.infra.master.databinding.FragmentItemListDialogListDialogItemBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

const val ARG_ITEM_COUNT = "item_count"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    LinesListDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class LinesListDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentItemListDialogListDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var itemClickListener: ((position: Int) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentItemListDialogListDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.list.layoutManager =
            LinearLayoutManager(context)
        binding.list.adapter = arguments?.getInt(ARG_ITEM_COUNT)?.let { LinesAdapter(it) }
    }

    private inner class ViewHolder(binding: FragmentItemListDialogListDialogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val text: TextView = binding.text
    }

    private inner class LinesAdapter(private val mItemCount: Int) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            return ViewHolder(
                FragmentItemListDialogListDialogItemBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.text.text = getString(R.string.string_lines, (position + 1))
            holder.itemView.setOnClickListener {
                itemClickListener?.invoke(position)
                dismiss()
            }
        }

        override fun getItemCount(): Int {
            return mItemCount
        }
    }

    companion object {

        fun newInstance(itemCount: Int): LinesListDialogFragment =
            LinesListDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ITEM_COUNT, itemCount)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}