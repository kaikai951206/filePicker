package com.sky.filepicker.upload

import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sky.filepicker.model.FileBean
import com.sky.filepicker.utils.Utils
import com.sky.filepicker.R
import java.io.File

class StorageAdapter(val context: Context, val list: List<FileBean>) :
    RecyclerView.Adapter<StorageAdapter.ViewHolder>() {
    private var mOnItemClickListener: OnItemClickListener? = null
    private val sparseBoolean = SparseBooleanArray()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.findViewById(R.id.iv_icon)
        val ivChoose: ImageView = view.findViewById(R.id.iv_choose)
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val tvFileNum: TextView = view.findViewById(R.id.tv_file_num)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_file, parent, false))
        viewHolder.itemView.setOnClickListener {
            mOnItemClickListener?.onItemClick(viewHolder.adapterPosition)
            notifyDataSetChanged()
        }
        return viewHolder
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(sparseBoolean.get(position)){
            holder.ivChoose.setImageResource(R.drawable.ic_pick)
        }else{
            holder.ivChoose.setImageResource(R.drawable.ic_not_pick)
        }
        val fileBean = list[position]
        holder.tvName.text = fileBean.name
        if (fileBean.isDirectory) {
            holder.ivChoose.visibility = View.GONE
            Glide.with(context).load(R.drawable.ic_file_icon).into(holder.ivIcon)
            holder.tvFileNum.text = "${Utils.getFileLastModifyTime(File(fileBean.path))} | 文件:${fileBean.fileNum}"
        } else {
            holder.ivChoose.visibility = View.VISIBLE
            holder.tvFileNum.text = "${Utils.getFileLastModifyTime(File(fileBean.path))} | ${Utils.getFormatSize(
                Utils.getFileSize(File(fileBean.path)))}"
            if(fileBean.path.endsWith("png") || fileBean.path.endsWith("jpg") || fileBean.path.endsWith("jpeg") || fileBean.path.endsWith("JPEG") || fileBean.path.endsWith("mp4")){
                Glide.with(context).load(fileBean.path).into(holder.ivIcon)
            }else if(fileBean.path.endsWith("doc") || fileBean.path.endsWith("docx")){
                Glide.with(context).load(R.drawable.ic_doc).into(holder.ivIcon)
            }else if(fileBean.path.endsWith("xls") || fileBean.path.endsWith("xlsx")){
                Glide.with(context).load(R.drawable.ic_excel).into(holder.ivIcon)
            }else if(fileBean.path.endsWith("pdf")){
                Glide.with(context).load(R.drawable.ic_pdf).into(holder.ivIcon)
            }else if(fileBean.path.endsWith("txt")){
                Glide.with(context).load(R.drawable.ic_text).into(holder.ivIcon)
            }else{
                Glide.with(context).load(R.drawable.ic_unknow_file).into(holder.ivIcon)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun addOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mOnItemClickListener = onItemClickListener
    }


    fun setItemChecked(position: Int, isChecked: Boolean) {
        sparseBoolean?.let {
            it.put(position, isChecked)
        }
    }

    fun isSelected(position: Int): Boolean {
        sparseBoolean?.let {
            return it.get(position)
        }
        return false
    }
}