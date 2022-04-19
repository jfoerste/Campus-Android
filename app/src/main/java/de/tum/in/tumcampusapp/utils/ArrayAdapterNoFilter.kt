package de.tum.`in`.tumcampusapp.utils

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

//Workaround see https://github.com/material-components/material-components-android/issues/1464
open class ArrayAdapterNoFilter<T>(context: Context, resource: Int, textViewResourceId: Int, objects: List<T>): ArrayAdapter<T>(context, resource, textViewResourceId, objects) {

    private val noOpFilter = object : Filter() {
        private val noOpResult = FilterResults()
        override fun performFiltering(constraint: CharSequence?): FilterResults = noOpResult
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {}
    }

    override fun getFilter(): Filter = noOpFilter
}