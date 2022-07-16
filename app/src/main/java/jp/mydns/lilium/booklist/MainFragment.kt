package jp.mydns.lilium.booklist

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.mydns.lilium.booklist.databinding.FragmentMainBinding
import jp.mydns.lilium.booklist.databinding.ListItemBinding
import kotlin.properties.Delegates

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private var _model: MainModel? = null
    private val binding get() = _binding!!
    private val model get() = _model!!
    private lateinit var menuProvider: MenuProvider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        _model = ViewModelProvider(this)[MainModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_settings -> {
                        findNavController().navigate(R.id.action_MainFragment_to_SettingsFragment)
                    }
                }
                return true
            }
        }
        menuHost.addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.edit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                model.query.value = binding.edit.text.toString()
            }
            false
        }

        val adapter = MyAdapter().apply {
            model.items.observe(viewLifecycleOwner) {
                items = model.items.value ?: emptyList()
            }
        }
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.apply {
            this.layoutManager = layoutManager
            addItemDecoration(
                DividerItemDecoration(
                    requireActivity(),
                    layoutManager.orientation,
                )
            )
            this.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _model = null
    }

    private inner class MyAdapter : RecyclerView.Adapter<VH>() {
        private var inflater: LayoutInflater? = null
        var items: List<Book> by Delegates.observable(emptyList()) { _, old, new ->
            DiffUtil.calculateDiff(DiffCallback(old, new), true)
                .dispatchUpdatesTo(this)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.textView.text = items.getOrNull(position)?.name
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            if (inflater == null) {
                inflater = LayoutInflater.from(parent.context)
            }
            return VH(ListItemBinding.inflate(inflater!!, parent, false))
        }
    }

    private inner class VH(binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val textView = binding.text
    }
}