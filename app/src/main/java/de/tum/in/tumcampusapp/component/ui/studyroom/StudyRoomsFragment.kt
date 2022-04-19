package de.tum.`in`.tumcampusapp.component.ui.studyroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.generic.adapter.GridEqualSpacingDecoration
import de.tum.`in`.tumcampusapp.component.other.generic.fragment.FragmentForAccessingTumCabe
import de.tum.`in`.tumcampusapp.component.ui.studyroom.model.StudyRoomGroup
import de.tum.`in`.tumcampusapp.databinding.FragmentStudyRoomsBinding
import de.tum.`in`.tumcampusapp.utils.ArrayAdapterNoFilter
import kotlin.math.roundToInt

class StudyRoomsFragment : FragmentForAccessingTumCabe<List<StudyRoomGroup>>(
        R.layout.fragment_study_rooms,
        R.string.study_rooms
), AdapterView.OnItemClickListener, AutoCompleteTextView.OnDismissListener {

    private val binding by viewBinding(FragmentStudyRoomsBinding::bind)

    private val viewModel: StudyRoomsViewModel by activityViewModels()

    private var groupAdapter: ArrayAdapter<StudyRoomGroup>? = null
    private var roomAdapter: StudyRoomAdapter? = null

    private fun getGroupAdapter(groups: List<StudyRoomGroup>): ArrayAdapter<StudyRoomGroup> {
        val adapter = object : ArrayAdapterNoFilter<StudyRoomGroup>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                android.R.id.text1,
                groups
        ) {
            val inflater = LayoutInflater.from(context)

            override fun getDropDownView(pos: Int, ignored: View?, parent: ViewGroup): View {
                val v = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
                val studyRoomGroup = getItem(pos) ?: return v
                val nameTextView = v.findViewById<TextView>(android.R.id.text1)
                nameTextView.text = studyRoomGroup.name
                return v
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        return adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentItemDetailRecyclerview.apply {
            val spanCount = 2
            val spacing = resources.getDimension(R.dimen.material_card_view_padding).roundToInt()
            layoutManager = GridLayoutManager(context, spanCount)
            addItemDecoration(GridEqualSpacingDecoration(spacing, spanCount))
        }

        viewModel.current.observe(viewLifecycleOwner) { setCurrentStudyRooms(it) }
        viewModel.alternatives.observe(viewLifecycleOwner) { setAlternativeStudyRoomGroups(it) }
        viewModel.error.observe(viewLifecycleOwner) { showError(it) }
        viewModel.refreshing.observe(viewLifecycleOwner) { swipeRefreshLayout!!.isRefreshing = it }

        binding.autoCompleteTextView.setOnItemClickListener(this)
        binding.autoCompleteTextView.setOnDismissListener(this)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.selectAlternative(position)
    }

    override fun onRefresh() {
        viewModel.refresh()
    }

    private fun setCurrentStudyRooms(group: StudyRoomGroup) {
        binding.autoCompleteTextView.setText(group.name)

        binding.studyRoomPlaceholder.visibility = if (group.rooms.isEmpty()) View.VISIBLE else View.GONE

        if (roomAdapter == null) {
            roomAdapter = StudyRoomAdapter(this@StudyRoomsFragment, group.rooms)
            binding.fragmentItemDetailRecyclerview.adapter = roomAdapter
        } else {
            roomAdapter!!.apply {
                studyRooms = group.rooms
                notifyDataSetChanged()
            }
        }
    }

    private fun setAlternativeStudyRoomGroups(groups: List<StudyRoomGroup>) {
        if (groupAdapter == null) {
            groupAdapter = getGroupAdapter(groups)
            binding.autoCompleteTextView.setAdapter(groupAdapter)
        } else {
            groupAdapter!!.apply {
                clear()
                addAll(groups)
            }
        }
    }

    private fun loadStudyRooms() {
        //fetch(apiClient.studyRoomGroups)
    }

    /*override fun onDownloadSuccessful(response: List<StudyRoomGroup>) {
        studyRoomGroupManager.updateDatabase(response) {
            runOnUiThread {
                groups = response
                displayStudyRooms()
            }
        }
    }*/

    /*private fun displayStudyRooms() {
        selectCurrentSpinnerItem()
        binding.spinnerContainer.visibility = View.VISIBLE
        showLoadingEnded()
    }*/

    companion object {
        fun newInstance() = StudyRoomsFragment()
    }

    override fun onDismiss() {
        println("Test")
        binding.autoCompleteTextView.clearFocus()
        //binding.focusLayout.requestFocus()
    }

}
