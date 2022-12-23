package ru.kalistratov.template.beauty.presentation.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import ru.kalistratov.template.beauty.R


class ListAlertDialog(
    context: Context,
    private val items: List<String>
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_alert_list)
        setCancelable(false)

        val listView = findViewById<ListView>(R.id.list)
        listView.adapter = CustomListAdapterDialog(context, items)

        listView.setOnItemClickListener { adapterView, view, i, l ->
            dismiss()
        }
    }

    fun show(show: Boolean) = if (show) show() else hide()
}

class CustomListAdapterDialog(
    context: Context,
    val items: List<String>
) : BaseAdapter() {

    private val layoutInflater by lazy { LayoutInflater.from(context) }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder = if (convertView == null) ViewHolder(
            layoutInflater.inflate(R.layout.list_item_title, null)
        ).apply { root.tag = this } else convertView.tag as ViewHolder

        holder.title.text = items[position]
        return holder.root
    }

    internal class ViewHolder(val root: View) {
        val title: TextView by lazy { root.findViewById(R.id.title) }
    }
}